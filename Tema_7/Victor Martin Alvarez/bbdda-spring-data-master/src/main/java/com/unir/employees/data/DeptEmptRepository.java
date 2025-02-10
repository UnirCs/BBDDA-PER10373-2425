package com.unir.employees.data;

import com.unir.employees.model.db.DeptEmp;
import com.unir.employees.model.db.DeptEmpId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeptEmptRepository extends JpaRepository<DeptEmp, DeptEmpId> {

    @Query(value = "SELECT * FROM dept_emp " +
            "WHERE emp_no = :emp_no " +
            "ORDER BY from_date DESC LIMIT 1", nativeQuery = true)
    Optional<DeptEmp> findByEmpNo(@Param("emp_no") Integer empNo);
}
