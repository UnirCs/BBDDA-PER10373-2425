package com.unir.employees.data;

import com.unir.employees.model.db.Title;
import com.unir.employees.model.db.TitleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TitleRepository extends JpaRepository<Title, TitleId> {

    Optional<Title> findFirstByEmployeeEmpNoOrderByFromDateDesc(Integer employeeId);

}
