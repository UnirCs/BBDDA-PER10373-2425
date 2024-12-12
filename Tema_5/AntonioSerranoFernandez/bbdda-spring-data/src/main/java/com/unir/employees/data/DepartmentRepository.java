package com.unir.employees.data;

import com.unir.employees.model.db.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String>, JpaSpecificationExecutor<Department> {

    // Buscar un departamento por nombre exacto
    Optional<Department> findByDeptName(String deptName);

    // Contar el total de departamentos
    long count();

    // Obtener todos los departamentos con un número específico
    List<Department> findByDeptNo(String deptNo);

    // Obtener todos los departamentos que contienen empleados
    @Query("SELECT d FROM Department d WHERE SIZE(d.deptEmps) > 0")
    List<Department> findDepartmentsWithEmployees();

    // Método para buscar departamentos por nombre parcial
    List<Department> findByDeptNameContaining(String partialName);

    // Método para buscar departamentos por prefijo en el nombre
    List<Department> findByDeptNameStartingWith(String prefix);
}