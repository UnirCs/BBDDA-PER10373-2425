package com.unir.employees.data;

import com.unir.employees.model.db.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

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

	//Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
	@Query(value = "select count(*) as cantidad, gender from employees group by gender order by cantidad desc", nativeQuery = true)
	List<Integer> selectTheNumberOfMenAndWomenOrderDesc();

	//Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
	@Query(value = "select salaries.emp_no, first_name, last_name, salary from employees join salaries on employees.emp_no = salaries.emp_no\n"
			+ "join employees.dept_emp de on employees.emp_no = de.emp_no join departments on de.dept_no = departments.dept_no\n"
			+ "where departments.dept_no  = :name "
			+ "order by salary desc\n"
			+ "LIMIT 1", nativeQuery = true)
	List<Object[]> getHighestPaidEmployeeByDepartment(@Param("name") String name);

	List<Object[]> findByFirstNameLike(@Param("name") String name);
}
