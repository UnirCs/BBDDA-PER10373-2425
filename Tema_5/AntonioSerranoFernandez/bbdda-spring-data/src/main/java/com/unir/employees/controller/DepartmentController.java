package com.unir.employees.controller;

import com.unir.employees.data.DepartmentRepository;
import com.unir.employees.model.db.Department;
import com.unir.employees.specifications.DepartmentSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    /**
     * Obtener departamentos basados en filtros dinámicos.
     *
     * @param deptName      - nombre parcial o completo del departamento.
     * @param deptNo        - número exacto del departamento.
     * @param hasEmployees  - si debe incluir departamentos con empleados asignados.
     * @param hasManagers   - si debe incluir departamentos con managers asignados.
     * @return lista de departamentos que coinciden con los parámetros.
     */
    @GetMapping
    public List<Department> getDepartments(
            @RequestParam(value = "deptName", required = false) String deptName,
            @RequestParam(value = "deptNo", required = false) String deptNo,
            @RequestParam(value = "deptNoFragment", required = false) String deptNoFragment,
            @RequestParam(value = "hasEmployees", required = false) Boolean hasEmployees,
            @RequestParam(value = "hasManagers", required = false) Boolean hasManagers) {

        // Ejercicio Tema 5, Parte 2: Ampliación de Repositorios
        // Construcción dinámica de la consulta con Specification
        Specification<Department> specification = Specification.where(
                        DepartmentSpecifications.withDeptName(deptName))
                .and(DepartmentSpecifications.withDeptNo(deptNo))
                .and(DepartmentSpecifications.withDeptNoContaining(deptNoFragment));

        // Manejar filtro de empleados
        if (Boolean.TRUE.equals(hasEmployees)) {
            specification = specification.and(DepartmentSpecifications.hasEmployees(true));
        } else if (Boolean.FALSE.equals(hasEmployees)) {
            specification = specification.and(DepartmentSpecifications.hasEmployees(false));
        }

        // Manejar filtro de managers
        if (Boolean.TRUE.equals(hasManagers)) {
            specification = specification.and(DepartmentSpecifications.hasManagers(true));
        } else if (Boolean.FALSE.equals(hasManagers)) {
            specification = specification.and(DepartmentSpecifications.hasManagers(false));
        }

        List<Department> departments = departmentRepository.findAll(specification);

        log.info("Consulta ejecutada con los siguientes parámetros:");
        log.info("deptName: {}, deptNo: {}, hasEmployees: {}, hasManagers: {}", deptName, deptNo, hasEmployees, hasManagers);
        log.info("Departamentos encontrados: {}", departments.size());

        return departments;
    }

    /**
     * Obtener un departamento por nombre exacto.
     *
     * @param deptName - nombre del departamento.
     * @return departamento encontrado o null.
     */
    @GetMapping("/{name}")
    public ResponseEntity<Department> getDepartmentByName(@PathVariable("name") String deptName) {
        return ResponseEntity.ok(departmentRepository.findByDeptName(deptName).orElse(null));
    }

    /**
     * Crear un nuevo departamento.
     *
     * @param department - departamento.
     * @return departamento creado.
     */
    @PostMapping("/")
    @Transactional
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        return ResponseEntity.ok(createDepartmentTransact(department));
    }

    /**
     * Crear un departamento con rollback si se supera el límite de 13.
     *
     * @param department - departamento.
     * @return departamento creado.
     */
    @Transactional
    protected Department createDepartmentTransact(Department department) throws IllegalStateException {

        Department created = departmentRepository.save(department);
        departmentRepository.findByDeptName(department.getDeptName())
                .ifPresent(
                        recentlyCreated -> log.info("En el contexto de esta conexión, el departamento {} ha sido creado",
                                recentlyCreated.getDeptName()));

        long count = departmentRepository.count();
        if (count >= 13) {
            throw new IllegalStateException("No puede haber más de 13 departamentos. Haciendo rollback...");
        }
        return created;
    }

    /**
     * Obtener departamentos que tienen al menos un empleado asignado.
     *
     * @return Lista de departamentos con empleados asignados.
     */
    @GetMapping("/with-employees")
    public List<Department> getDepartmentsWithEmployees() {
        return departmentRepository.findAll(DepartmentSpecifications.hasEmployees(true));
    }

    /**
     * Obtener departamentos que tienen al menos un manager asignado.
     *
     * @return Lista de departamentos con managers asignados.
     */
    @GetMapping("/with-managers")
    public List<Department> getDepartmentsWithManagers() {
        return departmentRepository.findAll(DepartmentSpecifications.hasManagers(true));
    }

    /**
     * Obtener todos los departamentos por número de departamento.
     *
     * @param deptNo - número del departamento.
     * @return Lista de departamentos.
     */
    @GetMapping("/by-number")
    public List<Department> getDepartmentsByNumber(@RequestParam String deptNo) {
        return departmentRepository.findByDeptNo(deptNo);
    }

    /**
     * Obtener departamentos cuyo nombre contiene una cadena específica.
     *
     * @param partialName - Parte del nombre del departamento a buscar.
     * @return Lista de departamentos que coinciden.
     */
    @GetMapping("/filter/by-name")
    public List<Department> getDepartmentsByPartialName(@RequestParam String partialName) {
        return departmentRepository.findByDeptNameContaining(partialName);
    }

    /**
     * Obtener departamentos cuyo nombre comienza con un prefijo específico.
     *
     * @param prefix - Prefijo con el que deben comenzar los nombres de los departamentos.
     * @return Lista de departamentos que coinciden con el prefijo.
     */
    @GetMapping("/filter/by-prefix")
    public List<Department> getDepartmentsByPrefix(@RequestParam String prefix) {
        return departmentRepository.findByDeptNameStartingWith(prefix);
    }

    /**
     * Obtener departamentos cuyo número contiene un fragmento específico.
     *
     * @param deptNoFragment - Fragmento que debe estar contenido en el número del departamento.
     * @return Lista de departamentos cuyos números contienen el fragmento proporcionado.
     */
    @GetMapping("/filter/by-number-fragment")
    public List<Department> getDepartmentsByNumberFragment(@RequestParam String deptNoFragment) {
        return departmentRepository.findAll((root, query, builder) ->
                builder.like(root.get("deptNo"), "%" + deptNoFragment + "%"));
    }
}
