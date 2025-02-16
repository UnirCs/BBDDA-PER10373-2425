package com.unir.employees.data;

import com.unir.employees.model.db.SalariesId;
import com.unir.employees.model.db.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, SalariesId> {
    @Query(value = "SELECT * FROM salaries " +
            "WHERE emp_no = :emp_no ORDER BY from_date DESC LIMIT 1", nativeQuery = true)
    Optional<Salary> findByEmpNo(@Param("emp_no") Integer empNo);
}
