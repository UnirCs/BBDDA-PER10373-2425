package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.sql.Date;

@AllArgsConstructor
@Getter
public class MySqlDeptEmpt {
    private int empNo;
    private String deptNo;
    private Date fromDate;
    private Date toDate;
}