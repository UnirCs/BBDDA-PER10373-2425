package com.unir.employees.data;

import com.unir.employees.model.db.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    //Documentacion sobre Derivacion de consultas: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
    //Documentacion sobre consultas nativas: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.at-query

    // Metodo para buscar un departamento por nombre
    Optional<Department> findByDeptName(String deptName);

    // Metodo para obtener el total de departamentos
    long count();

    // Metodo para buscar un departamento por id
    Optional<Department> findByDeptNo(String deptNo);

    // Metodo para buscar departamentos con nombre incompleto
    List<Department> findByDeptNameContaining(String deptName);

}
