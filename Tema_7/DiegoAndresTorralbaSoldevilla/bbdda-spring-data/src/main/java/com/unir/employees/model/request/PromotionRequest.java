package com.unir.employees.model.request;

import lombok.Data;
import java.util.Date;

@Data
public class PromotionRequest {

    private Integer employeeId;
    private String departmentId; //Nuevo departamento
    private String title; //Nuevo titulo
    private Integer salary; //Nuevo salario
    private Date fromDate;
}