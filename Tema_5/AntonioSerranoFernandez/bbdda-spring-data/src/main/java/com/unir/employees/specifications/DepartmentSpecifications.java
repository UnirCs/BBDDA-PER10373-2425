// Ejercicio Tema 5, Parte 2: Ampliación de Repositorios
// Clase creada para permitir consultas dinámicas relacionadas
// con Departments
package com.unir.employees.specifications;

import com.unir.employees.model.db.Department;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class DepartmentSpecifications {

    /**
     * Filtrar departamentos por nombre (parcial).
     *
     * @param deptName el nombre del departamento a buscar.
     * @return Specification<Department> para aplicar a la consulta.
     */
    public static Specification<Department> withDeptName(String deptName) {
        return (root, query, builder) ->
                StringUtils.hasText(deptName) ? builder.like(root.get("deptName"), "%" + deptName + "%") : null;
    }

    /**
     * Filtrar departamentos por número de departamento exacto.
     *
     * @param deptNo el número del departamento a buscar.
     * @return Specification<Department> para aplicar a la consulta.
     */
    public static Specification<Department> withDeptNo(String deptNo) {
        return (root, query, builder) ->
                StringUtils.hasText(deptNo) ? builder.equal(root.get("deptNo"), deptNo) : null;
    }

    /**
     * Filtrar departamentos que tienen al menos un empleado asignado.
     *
     * @return Specification<Department> para aplicar a la consulta.
     */
    public static Specification<Department> hasEmployees() {
        return (root, query, builder) ->
                builder.isNotEmpty(root.get("deptEmps"));
    }

    /**
     * Filtrar departamentos que tienen al menos un manager asignado.
     *
     * @return Specification<Department> para aplicar a la consulta.
     */
    public static Specification<Department> hasManagers(Boolean hasManagers) {
        return (root, query, builder) -> {
            if (Boolean.TRUE.equals(hasManagers)) {
                return builder.isNotEmpty(root.get("deptManagers"));
            } else if (Boolean.FALSE.equals(hasManagers)) {
                return builder.isEmpty(root.get("deptManagers"));
            }
            return null; // Si es nulo, no se aplica ningún filtro.
        };
    }

    /**
     * Filtrar departamentos que no tienen empleados asignados.
     *
     * @return Specification<Department> para aplicar a la consulta.
     */
    public static Specification<Department> hasNoEmployees() {
        return (root, query, builder) -> builder.isEmpty(root.get("deptEmps"));
    }

    /**
     * Filtrar departamentos según si tienen o no empleados asignados.
     *
     * @param hasEmployees Indica si el departamento debe tener empleados asignados.
     * @return Specification<Department> para aplicar a la consulta.
     */
    public static Specification<Department> hasEmployees(Boolean hasEmployees) {
        return (root, query, builder) -> {
            if (Boolean.TRUE.equals(hasEmployees)) {
                return builder.isNotEmpty(root.get("deptEmps"));
            } else if (Boolean.FALSE.equals(hasEmployees)) {
                return builder.isEmpty(root.get("deptEmps"));
            }
            return null; // Si es nulo, no se aplica ningún filtro.
        };
    }

    /**
     * Filtrar departamentos cuyo número contiene un fragmento específico.
     *
     * @param deptNoFragment - Fragmento que debe estar contenido en el número del departamento.
     * @return Specification<Department> para aplicar a la consulta.
     */
    public static Specification<Department> withDeptNoContaining(String deptNoFragment) {
        return (root, query, builder) ->
                StringUtils.hasText(deptNoFragment) ? builder.like(root.get("deptNo"), "%" + deptNoFragment + "%") : null;
    }
}
