package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Getter

public class OracleDepartments {

    private int department_Id;

    @Setter
    private String departmentName;

    @Setter
    private int manager_Id;

    @Setter
    private int location_Id;
}
