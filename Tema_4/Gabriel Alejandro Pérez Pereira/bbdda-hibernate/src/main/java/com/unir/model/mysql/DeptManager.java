package com.unir.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
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
    private Employee empNo;

    @Id
    @ManyToOne
    @JoinColumn(name = "dept_no", columnDefinition = "CHAR(4)")
    private Department deptNo;

    @Column(name = "from_date", columnDefinition="DATE")
    private Date fromDate;

    @Column(name = "to_date", columnDefinition="DATE")
    private Date toDate;
}
