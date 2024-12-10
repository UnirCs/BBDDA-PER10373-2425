package com.unir.employees.model.db;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data public class SalariesId implements Serializable {
    private Employee empNo;
    private Date fromDate;
}
