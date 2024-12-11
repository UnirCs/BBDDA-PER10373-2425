package com.unir.employees.controller;

import com.unir.employees.data.EmployeeRepository;
import com.unir.employees.model.db.Employee;
import com.unir.employees.model.db.Gender;
import com.unir.employees.specifications.EmployeeSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

	private final EmployeeRepository employeeRepository;

	/**
	 * Obtener empleados con parámetros dinámicos.
	 * Si no se especifican parámetros, se devuelve una lista vacía.
	 *
	 * @param lastName   - apellido.
	 * @param firstName  - nombre de pila.
	 * @param hireDate   - fecha de contratación exacta.
	 * @param hiredAfter - fecha desde la cual se busca empleados contratados.
	 * @param hiredBefore - fecha hasta la cual se busca empleados contratados.
	 * @param gender     - género del empleado.
	 * @param birthDate  - fecha de nacimiento.
	 * @return lista de empleados que coincidan con los parámetros.
	 */
	@GetMapping
	public List<Employee> getEmployees(
			@RequestParam(value = "lastName", required = false) String lastName,
			@RequestParam(value = "firstName", required = false) String firstName,
			@RequestParam(value = "hireDate", required = false) String hireDate,
			@RequestParam(value = "hiredAfter", required = false) String hiredAfter,
			@RequestParam(value = "hiredBefore", required = false) String hiredBefore,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "birthDate", required = false) String birthDate) {

		// Ejercicio Tema 5, Parte 2: Ampliación de Repositorios
		// Construcción dinámica de la consulta con Specification
		Specification<Employee> specification = Specification.where(
						EmployeeSpecifications.withFirstName(firstName))
				.and(EmployeeSpecifications.withLastName(lastName))
				.and(EmployeeSpecifications.withGender(gender))
				.and(EmployeeSpecifications.withHireDate(hireDate != null ? Date.valueOf(hireDate) : null))
				.and(EmployeeSpecifications.withHireDateBetween(
						hiredAfter != null ? Date.valueOf(hiredAfter) : null,
						hiredBefore != null ? Date.valueOf(hiredBefore) : null))
				.and(EmployeeSpecifications.withBirthDate(birthDate != null ? Date.valueOf(birthDate) : null));

		// Ejecuta la consulta dinámica
		List<Employee> employees = employeeRepository.findAll(specification);

		// Manejo de resultados vacíos
		if (employees.isEmpty()) {
			log.info("No se encontraron empleados. Devolviendo los primeros 20 registros.");
			return employeeRepository.findAll().subList(0, Math.min(20, employeeRepository.findAll().size()));
		}

		// Loguea la consulta y los parámetros utilizados
		log.info("Consulta ejecutada con los siguientes parámetros:");
		log.info("firstName: {}, lastName: {}, hireDate: {}, hiredAfter: {}, hiredBefore: {}, gender: {}, birthDate: {}",
				firstName, lastName, hireDate, hiredAfter, hiredBefore, gender, birthDate);
		log.info("Empleados encontrados: {}", employees.size());

		// Métrica adicional: Top 3 nombres de pila más repetidos
		List<String> top3DistinctFirstNameBy = employeeRepository.findTop3DistinctFirstNameBy();
		top3DistinctFirstNameBy.forEach(name -> log.info("Nombre más repetido: {}", name));

		// Métrica adicional: Cantidad de empleados con el nombre especificado
		if (firstName != null) {
			int countByFirstName = employeeRepository.countByFirstName(firstName);
			log.info("Número de empleados con el nombre {}: {}", firstName, countByFirstName);
		}

		// Devuelve los empleados encontrados o una lista vacía si no hay coincidencias
		return employees;
	}

	/**
	 * Obtener empleados con un nombre y género determinados.
	 *
	 * @param firstName - nombre de pila del empleado.
	 * @param gender    - género del empleado.
	 * @return lista de empleados que coincidan con el nombre y género.
	 */
	@GetMapping("/filter/by-name-and-gender")
	public List<Employee> getEmployeesByNameAndGender(
			@RequestParam String firstName,
			@RequestParam String gender) {
		try {
			Gender genderEnum = Gender.valueOf(gender); // Convierte String a Gender
			return employeeRepository.findByFirstNameAndGender(firstName, genderEnum);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Valor de género inválido: " + gender);
		}
	}

	/**
	 * Obtener mujeres contratadas antes de una fecha determinada.
	 *
	 * @param hireDate - fecha límite para la contratación.
	 * @return lista de mujeres contratadas antes de la fecha.
	 */
	@GetMapping("/filter/women-hired-before")
	public List<Employee> getWomenHiredBefore(@RequestParam String hireDate) {
		try {
			// Convertimos el parámetro de género "F" a Gender.F
			Gender genderEnum = Gender.F;

			// Convertimos la fecha de contratación a un objeto Date
			Date hireDateObj = Date.valueOf(hireDate);

			// Llamamos al método del repositorio
			return employeeRepository.findByGenderAndHireDateBefore(genderEnum, hireDateObj);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Fecha inválida: " + hireDate, e);
		}
	}

	/**
	 * Obtener empleados con un apellido específico contratados en un rango de fechas.
	 *
	 * @param lastName  - apellido del empleado.
	 * @param startDate - fecha de inicio del rango.
	 * @param endDate   - fecha de fin del rango.
	 * @return lista de empleados que coincidan con los criterios.
	 */
	@GetMapping("/filter/by-lastname-and-hiredate")
	public List<Employee> getEmployeesByLastNameAndHireDateRange(
			@RequestParam String lastName,
			@RequestParam String startDate,
			@RequestParam String endDate) {
		return employeeRepository.findByLastNameAndHireDateBetween(
				lastName, Date.valueOf(startDate), Date.valueOf(endDate));
	}
}
