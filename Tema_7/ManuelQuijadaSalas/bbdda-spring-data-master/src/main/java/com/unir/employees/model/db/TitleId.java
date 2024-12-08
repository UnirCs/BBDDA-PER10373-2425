package com.unir.employees.model.db;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@RequiredArgsConstructor
@AllArgsConstructor
public class TitleId implements Serializable {
    private Integer employee;
    private String title;
    private Date fromDate;


}
