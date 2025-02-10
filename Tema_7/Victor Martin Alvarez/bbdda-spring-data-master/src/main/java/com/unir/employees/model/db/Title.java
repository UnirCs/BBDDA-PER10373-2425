package com.unir.employees.model.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "titles")
@IdClass(TitleId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Title {

    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empNo;

    @Id
    @Temporal(TemporalType.DATE)
    @Column(name = "from_date", columnDefinition="DATE")
    private Date fromDate;

    @Id
    @Column(name = "title", columnDefinition="VARCHAR(50)")
    private String title;

    @Temporal(TemporalType.DATE)
    @Column(name = "to_date", columnDefinition="DATE")
    private Date toDate;
}
