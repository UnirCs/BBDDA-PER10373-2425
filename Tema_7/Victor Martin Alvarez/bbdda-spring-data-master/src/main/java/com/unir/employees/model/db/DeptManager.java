package com.unir.employees.model.db;

import com.unir.employees.data.DeptManagerId;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "dept_manager")
@IdClass(DeptManagerId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeptManager {

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
