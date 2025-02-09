package com.unir.employees.data;

import com.unir.employees.model.db.Employee;
import com.unir.employees.model.db.Gender;
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

	@Query(value = "SELECT COUNT(*) AS 'COUNT' " +
			"FROM employees " +
			"GROUP BY gender " +
			"ORDER BY 1 DESC", nativeQuery = true)
	List<Integer> countByGender();

	@Query(value = "SELECT count(e.emp_no) " +
			"FROM employees.employees e " +
			"JOIN employees.dept_emp de ON e.emp_no = de.emp_no " +
			"JOIN employees.departments d ON de.dept_no = d.dept_no " +
			"WHERE MONTH(de.from_date) = :hiringMonth", nativeQuery = true)
	Integer employeesByHiringMonth(@Param("hiringMonth") int hiringMonth);

	@Query(value = "SELECT e.* " +
			"FROM employees.employees e " +
			"JOIN employees.salaries s ON e.emp_no = s.emp_no " +
			"JOIN employees.dept_emp de ON e.emp_no = de.emp_no " +
			"JOIN employees.departments d ON de.dept_no = d.dept_no " +
			"WHERE d.dept_name = :deptName " +
			"ORDER BY s.salary DESC " +
			"LIMIT 1", nativeQuery = true)
	Employee bestPaidEmployeeByDepartment(@Param("deptName") String deptName);

	@Query(value = "SELECT e.* " +
			"FROM employees.employees e " +
			"JOIN employees.salaries s ON e.emp_no = s.emp_no " +
			"JOIN employees.dept_emp de ON e.emp_no = de.emp_no " +
			"JOIN employees.departments d ON de.dept_no = d.dept_no " +
			"WHERE d.dept_name = :deptName " +
			"ORDER BY s.salary DESC " +
			"LIMIT 1,1", nativeQuery = true)
	Employee bestSecondPaidEmployeeByDepartment(@Param("deptName") String deptName);

	List<Employee> findByGender(Gender gender);
}
