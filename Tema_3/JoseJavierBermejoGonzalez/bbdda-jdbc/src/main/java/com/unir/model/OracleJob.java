package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class OracleJob {
    private String jobId;

    @Setter
    private String jobTitle;

    @Setter
    private int minSalary;

    @Setter
    private int maxSalary;
}
