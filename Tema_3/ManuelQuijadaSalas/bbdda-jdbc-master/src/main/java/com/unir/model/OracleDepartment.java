package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class OracleDepartment {
    
    private int departmentId;

    @Setter
    private String departmentName;

    @Setter
    private int managerId;

    @Setter
    private int locationId;

}
