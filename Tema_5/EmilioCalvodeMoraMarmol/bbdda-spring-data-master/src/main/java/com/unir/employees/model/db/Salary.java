package com.unir.employees.model.db;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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
    private Employee employee;

    @Id
    @Column(name = "from_date")
    @Temporal(TemporalType.DATE)
    private java.time.LocalDate fromDate;

    @Column(name = "salary")
    private Integer salary;


    @Column(name = "to_date")
    @Temporal(TemporalType.DATE)
    private Date toDate;
}

@Data
class SalaryId implements Serializable {

    private Integer employee;
    private java.time.LocalDate fromDate;

}
