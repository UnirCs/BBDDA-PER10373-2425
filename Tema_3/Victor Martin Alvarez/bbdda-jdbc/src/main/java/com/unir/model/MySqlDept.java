package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Date;

@AllArgsConstructor
@Getter
public class MySqlDept {
    private String deptNo; // dept_no
    private String deptName; // dept_name
}
