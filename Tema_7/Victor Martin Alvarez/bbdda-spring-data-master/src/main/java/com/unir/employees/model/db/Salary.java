package com.unir.employees.model.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "salaries")
@IdClass(SalariesId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Salary {
    private Integer salary;

    @Temporal(TemporalType.DATE)
    @Column(name = "to_date", columnDefinition="DATE")
    private Date toDate;

    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empNo;

    @Temporal(TemporalType.DATE)
    @Id
    @Column(name = "from_date", columnDefinition="DATE")
    private Date fromDate;
}
