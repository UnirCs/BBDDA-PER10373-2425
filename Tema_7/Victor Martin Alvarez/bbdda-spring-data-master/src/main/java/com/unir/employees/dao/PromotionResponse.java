package com.unir.employees.dao;

import java.util.Date;

public record PromotionResponse(
        Integer employeeId,
        String departmentId,
        String title,
        Integer salary,
        Date fromDate) {}
