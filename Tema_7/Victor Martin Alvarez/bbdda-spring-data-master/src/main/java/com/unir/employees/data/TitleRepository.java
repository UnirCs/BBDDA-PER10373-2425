package com.unir.employees.data;

import com.unir.employees.model.db.Title;
import com.unir.employees.model.db.TitleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TitleRepository extends JpaRepository<Title, TitleId> {
    @Query(value = "SELECT * FROM titles " +
            "WHERE emp_no = :emp_no ORDER BY from_date DESC LIMIT 1", nativeQuery = true)
    Optional<Title> findByEmpNo(@Param("emp_no") Integer empNo);
}
