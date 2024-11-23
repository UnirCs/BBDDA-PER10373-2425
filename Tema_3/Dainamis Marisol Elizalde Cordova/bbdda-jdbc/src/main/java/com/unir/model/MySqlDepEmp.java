package com.unir.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@AllArgsConstructor
@Getter
@Setter
public class MySqlDepEmp {
    private int emp_no;
    private String dept_no;
    private Date from_date;
    private Date to_date;
}
