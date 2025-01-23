package com.unir.employees.data;

import com.unir.employees.model.db.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

	// Metodo para obtener el empleado mejor pagado de un departamento en particular
	@Query(value = "select e.* from employees e\n" +
			"    join salaries s  on e.emp_no = s.emp_no\n" +
			"    join dept_emp de on e.emp_no = de.emp_no\n" +
			"    join departments d on de.dept_no = d.dept_no\n" +
			"where d.dept_name = :departmentName\n" +
			"order by s.salary desc\n" +
			"limit 1;", nativeQuery = true)
	Employee findHighestPaidInDepartment(String departmentName);

	// Método para obtener el número de personas contratadas en un intervalo de tiempo establecido
	int countByHireDateBetween(Date fromDate, Date toDate);

	// Método que devuelve 5 empleados por su apellido completo
	List<Employee> findFirst5ByLastName(String lastName);

	// Metodo para buscar empleados contratados después de una fecha concreta
	List<Employee> findEmployeesByHireDateAfter(Date hiredAfter);

	// Metodo para buscar empleados con fecha de nacimiento entre dos fechas concretas
	List<Employee> findEmployeesByBirthDateAfterAndBirthDateBefore(Date birthDateAfter, Date birthDateBefore);


}
