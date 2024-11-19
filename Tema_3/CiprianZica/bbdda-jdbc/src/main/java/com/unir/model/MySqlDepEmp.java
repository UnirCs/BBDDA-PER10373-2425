package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MySqlDepEmp {
    private int emp_no;
    private String dept_no;
    private String from_date;
    private String to_date;
}
