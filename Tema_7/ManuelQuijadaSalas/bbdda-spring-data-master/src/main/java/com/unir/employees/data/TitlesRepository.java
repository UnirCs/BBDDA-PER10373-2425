package com.unir.employees.data;

import com.unir.employees.model.db.Department;
import com.unir.employees.model.db.Salary;
import com.unir.employees.model.db.Title;
import com.unir.employees.model.db.TitleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TitlesRepository  extends JpaRepository<Title, TitleId> {


    @Query("SELECT t FROM Title t " +
            "WHERE t.employee.empNo = :employeeId ")
    Optional<Title> findTitleEmpByEmployeeId(@Param("employeeId") Integer employeeId);

}
