package com.unir.employees.data;

import com.unir.employees.model.db.Department;
import com.unir.employees.model.db.Salary;
import com.unir.employees.model.db.SalaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SalariesRepository  extends JpaRepository<Salary, SalaryId> {

    @Query("SELECT s FROM Salary s " +
            "WHERE s.employee.empNo = :employeeId ORDER BY s.fromDate DESC\n" +
            "LIMIT 1")
    Optional<Salary> findCurrentSalaryByEmployeeId(@Param("employeeId") Integer employeeId);


}
