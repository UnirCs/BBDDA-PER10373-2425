package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Date;

@AllArgsConstructor
@Getter
public class MySqlDeptEmp {
    private int empNo; // emp_no
    private String deptNo; // dept_no
    private Date fromDate; // from_date
    private Date toDate; // to_date
}
