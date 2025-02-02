package com.unir.employees.data;

import com.unir.employees.model.db.DeptEmp;
import com.unir.employees.model.db.DeptEmpId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeptEmpRepository extends JpaRepository<DeptEmp, DeptEmpId> {

    Optional<DeptEmp> findFirstByEmployeeEmpNoOrderByFromDateDesc(Integer employeeId);

}
