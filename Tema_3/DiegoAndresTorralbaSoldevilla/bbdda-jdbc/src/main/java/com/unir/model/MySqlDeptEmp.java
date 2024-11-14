package com.unir.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MySqlDeptEmp {
    private String emp_no;
    private String dept_no;
    private Date from_date;
    private Date to_date;
}
