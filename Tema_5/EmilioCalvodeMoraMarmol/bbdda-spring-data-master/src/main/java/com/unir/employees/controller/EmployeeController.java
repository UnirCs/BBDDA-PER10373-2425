package com.unir.employees.controller;

import com.unir.employees.data.EmployeeRepository;
import com.unir.employees.model.db.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
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

	/**
	 * Obtener el empleado mejor pagado de un departamento
	 *
	 *
	 * @param departmentName
	 * @return
	 */
	@GetMapping("/highest-paid")
	public Employee getHighestPaidInDepartment(@RequestParam String departmentName){
		return employeeRepository.findHighestPaidInDepartment("Finance");
	}

	/**
	 * Obtener el número de empleados que fueron contratados entre dos fechas concretas
	 *
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	@GetMapping("/count")
	public int countEmployees(
			@RequestParam(name = "fromDate", required = false) Date fromDate,
			@RequestParam(name = "toDate", required = false) Date toDate
	) {
		// se usan las fechas de los parametros, si no se especifican se usan las fechas por defecto
		Date date1 = fromDate != null ? fromDate : Date.valueOf(LocalDate.now().minusDays(30));
		Date date2 = toDate != null ? toDate : (new Date(System.currentTimeMillis()));

		return employeeRepository.countByHireDateBetween(date1, date2);
	}

	/**
	 * Obtener los cinco primeros empleados por su apellido
	 *
	 *
	 * @param lastName
	 * @return
	 */
	@GetMapping("/firstFiveLastName")
	public List<Employee> getFirst5ByLastName(@RequestParam String lastName){
		return employeeRepository.findFirst5ByLastName(lastName);
	}

	/**
	 * Obtener los empleados que fueron contratados a partir de una fecha concreta
	 *
	 * @param fromDate
	 * @return
	 */
	@GetMapping("/employeesHiredAfter")
	public List<Employee> employeesHiredAfter(@RequestParam(name = "fromDate", required = false) Date fromDate){
		return employeeRepository.findEmployeesByHireDateAfter(fromDate);
	}

	/**
	 * Obtener los empleados que nacieron entre dos fechas concretas
	 *
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	@GetMapping("/employeesBornBetween")
	public List<Employee> employeesBornBetween(
			@RequestParam(name = "fromDate", required = false) Date fromDate,
			@RequestParam(name = "toDate", required = false) Date toDate
	) {
		return employeeRepository.findEmployeesByBirthDateAfterAndBirthDateBefore(fromDate, toDate);
	}
}

