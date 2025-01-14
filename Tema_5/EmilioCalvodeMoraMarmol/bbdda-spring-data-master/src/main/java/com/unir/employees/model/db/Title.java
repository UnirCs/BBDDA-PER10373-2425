package com.unir.employees.model.db;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "titles")
@IdClass(TitleId.class)

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Data
public class Title {

    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee employee;

    @Id
    @Column(name = "title")
    private String title;

    @Id
    @Column(name = "from_date")
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    @Column(name = "to_date")
    @Temporal(TemporalType.DATE)
    private Date toDate;
}


@Data
class TitleId implements Serializable {

    private Integer employee;

    private String title;

    private java.sql.Date fromDate;

}