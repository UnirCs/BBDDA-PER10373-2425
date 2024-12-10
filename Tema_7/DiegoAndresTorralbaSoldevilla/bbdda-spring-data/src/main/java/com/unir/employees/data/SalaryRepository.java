package com.unir.employees.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unir.employees.model.db.Salaries;
import com.unir.employees.model.db.SalariesId;
import com.unir.employees.model.db.Titles;

@Repository
public interface SalaryRepository extends JpaRepository<Salaries, SalariesId> {

	//Documentacion sobre Derivacion de consultas: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	//Documentacion sobre consultas nativas: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.at-query

    //Un método que nos permita obtener, a partir del ID de empleado únicamente, el salario actual del trabajo del empleado.
    @Query(value = "select * from salaries\n"
            + "where salaries.emp_no  = :id ORDER BY salaries.from_date desc LIMIT 1", nativeQuery = true)
    Optional<Salaries> getSalaryById(@Param("id") Integer id);
}
