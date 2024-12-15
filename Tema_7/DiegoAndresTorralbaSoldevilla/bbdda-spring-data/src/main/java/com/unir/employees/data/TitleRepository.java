package com.unir.employees.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unir.employees.model.db.Department;
import com.unir.employees.model.db.Titles;
import com.unir.employees.model.db.TitlesId;

@Repository
public interface TitleRepository extends JpaRepository<Titles, TitlesId> {

	//Documentacion sobre Derivacion de consultas: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	//Documentacion sobre consultas nativas: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.at-query

    //Un método que nos permita obtener, a partir del ID de empleado únicamente, el título actual del trabajo del empleado.
    @Query(value = "select * from titles\n"
            + "where titles.emp_no  = :id ORDER BY titles.from_date desc LIMIT 1", nativeQuery = true)
    Optional<Titles> getTitleById(@Param("id") Integer id);
}
