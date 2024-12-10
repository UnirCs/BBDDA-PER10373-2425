package com.unir.employees.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	@Temporal(TemporalType.DATE)
	@Column(name = "birth_date")
	private Date birthDate;

	@Column(name = "first_name", length = 14)
	private String firstName;

	@Column(name = "last_name", length = 16)
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "ENUM('M', 'F')")
	private Gender gender;

	@Temporal(TemporalType.DATE)
	@Column(name = "hire_date")
	private Date hireDate;

	@JsonIgnore
	@OneToMany(mappedBy = "employee")
	private Set<DeptEmp> deptEmps;

	@JsonIgnore
	@OneToMany(mappedBy = "employee")
	private Set<DeptManager> deptManagers;
}

enum Gender {
	M, F
}
