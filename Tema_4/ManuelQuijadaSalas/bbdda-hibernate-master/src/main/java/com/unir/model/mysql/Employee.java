package com.unir.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "employees")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @Column(name = "emp_no")
    private Integer empNo;

    @Column(name = "birth_date", columnDefinition="DATE")
    private Date birthDate;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender", columnDefinition = "ENUM('M', 'F')")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "hire_date", columnDefinition="DATE")
    private Date hireDate;

    @OneToMany(mappedBy = "empNo")
    private Set<DeptEmployee> deptEmployees;

    enum Gender {
        M, F
    }
}
