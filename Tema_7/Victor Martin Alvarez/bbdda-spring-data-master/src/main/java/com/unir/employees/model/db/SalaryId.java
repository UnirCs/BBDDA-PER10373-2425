package com.unir.employees.model.db;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SalaryId implements Serializable {
    private Integer employee;
    private Date fromDate;
}
