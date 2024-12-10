package com.unir.employees.data;

import com.unir.employees.model.db.Salary;
import com.unir.employees.model.db.SalaryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, SalaryId> {

    Optional<Salary> findFirstByEmployeeEmpNoOrderByFromDateDesc(Integer employeeId);

}
