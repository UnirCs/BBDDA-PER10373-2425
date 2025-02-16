package com.unir.employees.model.db;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SalariesId implements Serializable {
    private Employee empNo;
    private Date fromDate;
}
