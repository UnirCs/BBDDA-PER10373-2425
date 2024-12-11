package com.unir.employees.data;

import com.unir.employees.model.db.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import com.unir.employees.model.db.Gender;

@Repository
// Ejercicio Tema 5, Parte 2: Ampliación de Repositorios
// Se habilita JpaSpecificationExecutor para construir consultas dinámicas según
// los parámetros presentes. Esto permite sustituir los condicionales en el controlador
// (EmployeeController) y hacer la lógica más práctica y mantenible.
// Se crea además una clase estática (com.unir.employees/specifications/EmployeeSpecifications)
// para poder construir dinámicamente la consulta.
public interface EmployeeRepository extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

	//Documentacion sobre Derivacion de consultas: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	//Documentacion sobre consultas nativas: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.at-query

	// Metodo para buscar empleados por apellido
	List<Employee> findByLastName(String lastName);

	// Metodo para buscar empleados por nombre incompleto
	List<Employee> findByFirstNameContaining(String firstName);

	// Metodo para buscar empleados por nombre incompleto y apellido incompleto y como máximo 5 registros
	List<Employee> findFirst5ByFirstNameContainingAndLastNameContaining(String firstName, String lastName);

	// Metodo para buscar empleados contratados en una fecha concreta
	List<Employee> findByHireDate(Date hireDate);

	// Metodo para buscar empleados contratados en un rango de fechas
	List<Employee> findByHireDateBetween(Date hireDate, Date hireDate2);

	// Metodo para obtener el número de personas que tienen un nombre concreto
	int countByFirstName(String firstName);

	// Metodo para obtener los diferentes nombres de los empleados, pero solo los 3 nombres más REPETIDOS
	@Query(value = "SELECT employees.first_name, COUNT(employees.first_name) AS \"empleados\" FROM employees GROUP BY employees.first_name ORDER BY empleados DESC LIMIT 3", nativeQuery = true)
	List<String> findTop3DistinctFirstNameBy();

	// Nuevo método: Empleados con un nombre y género determinados
	List<Employee> findByFirstNameAndGender(String firstName, Gender gender);

	// Nuevo método: Mujeres contratadas antes de una fecha
	List<Employee> findByGenderAndHireDateBefore(Gender gender, Date hireDate);

	// Nuevo método: Empleados con un apellido específico contratados en un rango de fechas
	List<Employee> findByLastNameAndHireDateBetween(String lastName, Date startDate, Date endDate);
}
