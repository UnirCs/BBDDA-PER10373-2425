// Ejercicio: Mapear tabla salaries y sus relaciones
package com.unir.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "salaries")
@IdClass(SalaryEmployeeId.class)
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
    @Column(name = "from_date", columnDefinition="DATE")
    private Date fromDate;

    @Column(name = "salary", columnDefinition="INT")
    private Integer salary;

    @Column(name = "to_date", columnDefinition="DATE")
    private Date toDate;
}
