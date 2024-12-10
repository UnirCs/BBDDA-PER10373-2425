package com.unir.employees.model.db;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "salaries")
@IdClass(SalariesId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Salaries {

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

