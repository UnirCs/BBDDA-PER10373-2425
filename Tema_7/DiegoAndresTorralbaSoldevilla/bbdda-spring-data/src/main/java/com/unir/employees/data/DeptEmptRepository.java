package com.unir.employees.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unir.employees.model.db.DeptEmp;
import com.unir.employees.model.db.DeptEmpId;
import com.unir.employees.model.db.Salaries;

@Repository
public interface DeptEmptRepository extends JpaRepository<DeptEmp, DeptEmpId> {

	//Documentacion sobre Derivacion de consultas: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	//Documentacion sobre consultas nativas: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.at-query

    //Un método que nos permita obtener, a partir del ID de empleado únicamente, el departamento actual del trabajo del empleado.
    @Query(value = "select * from dept_emp\n"
            + "where dept_emp.emp_no  = :id ORDER BY dept_emp.from_date desc LIMIT 1", nativeQuery = true)
    Optional<DeptEmp> getDeptById(@Param("id") Integer id);
}
