// Ejercicio Tema 5, Parte 1: Completar el Esquema Employee
package com.unir.employees.model.db;

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

public class Salary {
    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empNo;

    @Id
    @Temporal(TemporalType.DATE)
    @Column(name = "from_date", columnDefinition="DATE")
    private Date fromDate;

    @Column(name = "salary", columnDefinition="INT")
    private Integer salary;

    @Temporal(TemporalType.DATE)
    @Column(name = "to_date", columnDefinition="DATE")
    private Date toDate;
}

@Data
class SalaryId implements java.io.Serializable {
    private Integer empNo;
    private Date fromDate;
}