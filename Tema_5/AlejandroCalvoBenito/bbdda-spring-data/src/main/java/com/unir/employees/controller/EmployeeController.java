package com.unir.employees.controller;

import com.unir.employees.data.EmployeeRepository;
import com.unir.employees.model.db.Employee;
import com.unir.employees.model.db.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
	 * Obtener empleados por fecha de nacimiento.
	 *
	 * @param birthDate - apellido.
	 * @return lista de empleados nacidos en esa fecha.
	 */
	@GetMapping("/search/birthDate")
	public List<Employee> getEmployeesByBirthDate(@RequestParam (value = "birthDate")String birthDate) {
		return employeeRepository.findByBirthDate(java.sql.Date.valueOf(birthDate));
	}

	/**
	 * Obtener el numero de empleados por genero.
	 *
	 * @param gender - genero de los empleados.
	 * @return numero de empleados por genero.
	 */
	@GetMapping("/countByGender")
	public int getCountByGender(@RequestParam String gender) {
		return employeeRepository.countByGender(Gender.valueOf(gender));
	}

	/**
	 * Buscar empleados por genero y rango de fechas de contratacion.
	 *
	 * @param gender - genero de los empleados.
	 * @param hiredAfter - fecha de inicio del rango.
	 * @param hiredBefore - fecha de fin del rango.
	 * @return lista de empleados que cumplen con los criterios.
	 */
	@GetMapping("/search/byGenderAndHireDate")
	public List<Employee> getEmployeesByGenderAndHireDate(
			@RequestParam String gender,
			@RequestParam String hiredAfter,
			@RequestParam String hiredBefore) {


		return employeeRepository.findByGenderAndHireDateBetween(Gender.valueOf(gender), java.sql.Date.valueOf(hiredAfter), java.sql.Date.valueOf(hiredBefore));
	}


}

