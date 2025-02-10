package com.unir.employees.dao;

import java.util.Date;

public record PromotionRequest(
        Integer employeeId,
        String newDepartmentId,
        String newTitle,
        Integer newSalary,
        Date fromDate) {}
