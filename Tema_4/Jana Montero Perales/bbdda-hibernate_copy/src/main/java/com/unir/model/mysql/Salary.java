package com.unir.model.mysql;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Table(name = "salaries")
@IdClass(SalaryId.class)
@Data
public class Salary {

    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empNo;

    @Id
    @Column(name = "from_date", columnDefinition = "DATE")
    private Date fromDate;

    @Column
    private Integer salary;

    @Column(name = "to_date", columnDefinition = "DATE")
    private Date toDate;
}
