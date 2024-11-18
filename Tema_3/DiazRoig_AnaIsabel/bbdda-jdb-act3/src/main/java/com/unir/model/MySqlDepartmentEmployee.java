package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.sql.Date;

@AllArgsConstructor
@Getter

/*
 * Clase que representa la tabla dept_emp de la base de datos employees
 *
 * @Author: Ana Isabel Díaz
 * @Version: 1.0 - 2024/11/05
 */
public class MySqlDepartmentEmployee {

    private int emp_no;
    private String dept_no;
    private Date from_date;
    private Date to_date;

}
