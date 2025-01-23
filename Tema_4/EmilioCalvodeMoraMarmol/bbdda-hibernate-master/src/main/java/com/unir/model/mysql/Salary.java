package com.unir.model.mysql;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity

@Table(name = "salaries")
@IdClass(SalaryId.class)

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Data
public class Salary {

    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empNo;

    @Id
    @Column(name = "from_date", columnDefinition = "DATE")
    private java.time.LocalDate fromDate;

    @Column(name = "salary")
    private Integer salary;


    @Column(name = "to_date", columnDefinition = "DATE")
    private Date toDate;
}
