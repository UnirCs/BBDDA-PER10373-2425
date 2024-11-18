package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

/*
* Clase que representa la tabla departments de la base de datos employees
*
* @Author: Ana Isabel Díaz
* @Version: 1.0 - 2024/11/05
*/
public class MySqlDepartment {

    private String dept_no;
    private String dept_name;

}
