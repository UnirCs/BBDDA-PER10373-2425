package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.sql.Date;

@AllArgsConstructor
@Getter

public class MySqlDeptEmp {
    private int emp_no;
    private String dept_no;
    private Date from_date;
    private Date to_date;
}
