package com.unir.employees.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "departments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Department implements Serializable {

    @Id
    @Column(name = "dept_no", columnDefinition = "CHAR(4)")
    private String deptNo;

    @Column(name = "dept_name", length = 40)
    private String deptName;

    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private Set<DeptEmp> deptEmps;

    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private Set<DeptManager> deptManagers;
}
