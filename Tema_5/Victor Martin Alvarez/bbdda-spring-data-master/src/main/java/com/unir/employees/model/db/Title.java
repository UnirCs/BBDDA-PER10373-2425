package com.unir.employees.model.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "titles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Title {

    @Temporal(TemporalType.DATE)
    @Column(name = "to_date", columnDefinition="DATE")
    private Date toDate;

    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empNo;

    @Id
    @Column(name = "title", columnDefinition="VARCHAR(50)")
    private String title;

    @Temporal(TemporalType.DATE)
    @Id
    @Column(name = "from_date", columnDefinition="DATE")
    private Date fromDate;
}
