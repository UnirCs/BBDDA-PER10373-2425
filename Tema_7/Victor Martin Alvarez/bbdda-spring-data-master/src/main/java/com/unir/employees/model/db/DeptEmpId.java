package com.unir.employees.model.db;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeptEmpId implements Serializable {
    private Integer employee;
    private String department;
}
