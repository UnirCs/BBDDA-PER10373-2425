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

    // Ejercicio: Relación @OneToMany de la tabla employees con dept_manager
    @OneToMany(mappedBy = "empNo")
    private Set<DeptManager> deptManagers;

    // Ejercicio: Relación @OneToMany de la tabla employees con titles
    @OneToMany(mappedBy = "empNo")
    private Set<Title> titles;

    // Ejercicio: Relación @OneToMany de la tabla employees con salaries
    @OneToMany(mappedBy = "empNo")
    private Set<Salary> salaries;

    enum Gender {
        M, F
    }

    // Constructor
    public Employee(Integer empNo, Date birthDate, String firstName, String lastName, Gender gender, Date hireDate, Set<DeptEmployee> deptEmployees) {
        this.empNo = empNo;
        this.birthDate = birthDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.hireDate = hireDate;
        this.deptEmployees = deptEmployees;
    }
}
