package com.unir.employees.model.db;

import lombok.Data;

import java.util.Date;

@Data
public class TitleId implements java.io.Serializable {
    private Integer empNo;
    private String title;
    private Date fromDate;
}
