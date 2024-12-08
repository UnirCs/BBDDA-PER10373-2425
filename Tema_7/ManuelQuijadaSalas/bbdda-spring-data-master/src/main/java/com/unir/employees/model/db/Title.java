package com.unir.employees.model.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="titles")
@IdClass(TitleId.class)
public class Title {

    @Id
    @ManyToOne
    @JoinColumn(name="emp_no")
    private Employee employee;

    @Id
    @Column
    private String title;

    @Id
    @Column(name="from_date")
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    @Column(name="to_date")
    @Temporal(TemporalType.DATE)
    private Date toDate;
}

