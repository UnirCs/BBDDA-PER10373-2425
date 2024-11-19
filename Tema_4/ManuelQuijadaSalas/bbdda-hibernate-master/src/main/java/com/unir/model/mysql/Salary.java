package com.unir.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "salaries")
@IdClass(SalaryId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Salary {

    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empoNo;

    @Id
    @Column(name = "from_date", columnDefinition="DATE")
    private Date fromDate;

    @Column(name = "salary")
    private int salary;

    @Column(name = "to_date", columnDefinition="DATE")
    private Date toDate;


}
