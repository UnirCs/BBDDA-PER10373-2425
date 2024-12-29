package com.unir.employees.model.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "dept_emp")
@IdClass(DeptEmpId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeptEmp {

    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee employee;

    @Id
    @ManyToOne
    @JoinColumn(name = "dept_no", columnDefinition = "CHAR(4)")
    private Department department;

    @Temporal(TemporalType.DATE)
    @Column(name = "from_date")
    private Date fromDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "to_date")
    private Date toDate;
}

