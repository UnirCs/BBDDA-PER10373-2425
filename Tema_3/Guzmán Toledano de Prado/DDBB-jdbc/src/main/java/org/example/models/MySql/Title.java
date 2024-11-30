package org.example.models.MySql;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class Title {
    private int emp_no;
    private String title;
    private Date from_date;
    private Date to_date;
}
