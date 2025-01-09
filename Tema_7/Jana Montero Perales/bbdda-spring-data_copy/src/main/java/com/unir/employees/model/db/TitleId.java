package com.unir.employees.model.db;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
public class TitleId implements Serializable {
    private Integer employee;
    private String title;
    private Date fromDate;
}
