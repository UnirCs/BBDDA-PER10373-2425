package com.unir.employees.data;

import com.unir.employees.model.db.Department;
import com.unir.employees.model.db.DeptEmp;
import com.unir.employees.model.db.DeptEmpId;
import com.unir.employees.model.db.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeptEmpRepository  extends JpaRepository<DeptEmp, DeptEmpId> {

    @Query("SELECT d FROM DeptEmp d " +
            "WHERE d.employee.empNo = :employeeId ")
    Optional<DeptEmp> findDeptEmpByEmployeeId(@Param("employeeId") Integer employeeId);
}
