package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Date;

@AllArgsConstructor
@Getter

public class MySqlEmpDpto {
    private int employeeId;
    private String dptoId;
    private Date fromDate;
    private Date toDate;
}
