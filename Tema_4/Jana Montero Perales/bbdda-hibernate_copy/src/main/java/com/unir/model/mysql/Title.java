package com.unir.model.mysql;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Table(name = "titles")
@IdClass(TitleId.class)
@Data
public class Title {

    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empNo;

    @Id
    @Column
    private String title;

    @Id
    @Column(name = "from_date", columnDefinition = "DATE")
    private Date fromDate;

    @Column(name = "to_date", columnDefinition = "DATE")
    private Date toDate;
}
