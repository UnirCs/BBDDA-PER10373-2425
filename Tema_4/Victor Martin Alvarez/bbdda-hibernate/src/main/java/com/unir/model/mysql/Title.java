package com.unir.model.mysql;

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
    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empNo;

    @Id
    @Column(name = "title", columnDefinition="VARCHAR(50)")
    private String title;

    @Id
    @Column(name = "from_date", columnDefinition="DATE")
    private Date fromDate;

    @Column(name = "to_date", columnDefinition="DATE")
    private Date toDate;
}
