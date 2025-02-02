package com.unir.model.mysql;

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

@Data
public class Title {

    @Id
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empNo;

    @Id
    @Column(name = "title")
    private String title;

    @Id
    @Column(name = "from_date", columnDefinition = "DATE")
    private Date fromDate;

    @Column(name = "to_date", columnDefinition = "DATE")
    private Date toDate;
}
