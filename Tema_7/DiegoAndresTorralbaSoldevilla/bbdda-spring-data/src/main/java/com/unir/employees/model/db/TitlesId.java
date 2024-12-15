package com.unir.employees.model.db;

import java.util.Date;

import lombok.Data;

@Data public class TitlesId implements java.io.Serializable {
    private Employee empNo;
    private String title;
    private Date fromDate;
}
