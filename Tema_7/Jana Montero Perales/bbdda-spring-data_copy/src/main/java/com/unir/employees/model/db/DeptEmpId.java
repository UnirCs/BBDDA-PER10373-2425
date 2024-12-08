package com.unir.employees.model.db;

import lombok.Data;

@Data
public class DeptEmpId implements java.io.Serializable {
    private Integer employee;
    private String department;
}
