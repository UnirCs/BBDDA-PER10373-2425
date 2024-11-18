package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Date;

@AllArgsConstructor
@Getter
public class MySqlDeptEmp {
    private int employeeId;
    private String deptId;
    private Date fromDate;
    private Date toDate;
}
