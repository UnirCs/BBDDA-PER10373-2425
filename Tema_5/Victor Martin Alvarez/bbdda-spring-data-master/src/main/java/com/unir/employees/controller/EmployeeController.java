package com.unir.employees.controller;

import com.unir.employees.data.EmployeeRepository;
import com.unir.employees.model.db.Employee;
import com.unir.employees.model.db.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

	private final EmployeeRepository employeeRepository;

	/**
	 * Obtener empleados por apellido.
	 * Si no se especifica apellido, se devuelven los primeros 20 empleados.
	 *
	 * @param lastName - apellido.
	 * @return lista de empleados.
	 */
	@GetMapping
	public List<Employee> getEmployees(
			@RequestParam(value = "lastName", required = false) String lastName,
			@RequestParam(value = "firstName", required = false) String firstName,
			@RequestParam(value = "hireDate", required = false) String hireDate,
			@RequestParam(value = "hiredAfter", required = false) String hiredAfter,
			@RequestParam(value = "hiredBefore", required = false) String hiredBefore) {

		List<String> top3DistinctFirstNameBy = employeeRepository.findTop3DistinctFirstNameBy();
		top3DistinctFirstNameBy.forEach( employee -> log.info("Top Name: {}", employee));

		if(StringUtils.hasText(firstName)) {
			int countByFirstName = employeeRepository.countByFirstName(firstName);
			log.info("Count by firstName: {}", countByFirstName);
		}

		if(StringUtils.hasText(hireDate)) {
			return employeeRepository.findByHireDate(java.sql.Date.valueOf(hireDate));
		} else if(StringUtils.hasText(hiredAfter) && StringUtils.hasText(hiredBefore)) {
			return employeeRepository.findByHireDateBetween(java.sql.Date.valueOf(hiredAfter), java.sql.Date.valueOf(hiredBefore));
		} else if (StringUtils.hasText(firstName) && StringUtils.hasText(lastName)) {
			return employeeRepository.findFirst5ByFirstNameContainingAndLastNameContaining(firstName, lastName);
		} else if (StringUtils.hasText(lastName)) {
			return employeeRepository.findByLastName(lastName);
		} else if (StringUtils.hasText(firstName)) {
			return employeeRepository.findByFirstNameContaining(firstName);
		} else {
			return employeeRepository.findAll().subList(0, 20);
		}
	}

	@GetMapping("/gender/count")
	public List<Integer> countEmployeesByGender() {
		List<Integer> counts = employeeRepository.countByGender();
		return counts;
	}
	@GetMapping("/gender")
	public List<Employee> getEmployeesByGender(
			@RequestParam(value = "gender", required = true) String gender) {
		Gender male = Gender.valueOf(gender.substring(0,1).toUpperCase());
		List<Employee> employees = employeeRepository.findByGender(male);
		return employees;
	}

	@GetMapping("/hiringmonth")
	public Integer countEmployeesByHiringMonth(
			@RequestParam(value = "month", required = true) Integer hiringMonth) {
		Integer countEmployees = employeeRepository.employeesByHiringMonth(hiringMonth);
		return countEmployees;
	}

	@GetMapping("/bestpaid")
	public Employee bestPaidByDepartment(
			@RequestParam(value = "department", required = true) String department) {
		Employee bestPaidEmployee = employeeRepository.bestPaidEmployeeByDepartment(department);
		return bestPaidEmployee;
	}

	@GetMapping("/secondBestpaid")
	public Employee secondBestPaidByDepartment(
			@RequestParam(value = "department", required = true) String department) {
		Employee secondBestPaidEmployee = employeeRepository.bestSecondPaidEmployeeByDepartment(department);
		return secondBestPaidEmployee;
	}
}

