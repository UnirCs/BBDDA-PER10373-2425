package com.unir.employees.model.db;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "title", columnDefinition = "CHAR(50)")
    private String title;

    @Id
    @Temporal(TemporalType.DATE)
    @Column(name = "from_date", columnDefinition="DATE")
    private Date fromDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "to_date", columnDefinition="DATE")
    private Date toDate;
}

@Data
class TitleId implements java.io.Serializable {
    private Integer empNo;
    private String title;
    private Date fromDate;
}