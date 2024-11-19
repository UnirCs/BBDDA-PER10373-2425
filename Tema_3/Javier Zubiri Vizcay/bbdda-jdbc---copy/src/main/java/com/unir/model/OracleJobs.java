package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class OracleJobs {
    private String job_id;

    @Setter
    private String job_title;

    @Setter
    private int min_salary;

    @Setter
    private int max_salary;
}
