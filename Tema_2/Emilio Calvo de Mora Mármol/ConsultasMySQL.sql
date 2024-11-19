/* author: Emilio Calvo de Mora Mármol */

/*
* Ejercicio 1
* Obtención del número de hombres y mujeres de la base de datos, ordenados descendentemente
*/
SELECT emp.gender AS Genero, COUNT(*) AS Numero
FROM employees.employees emp
GROUP BY emp.gender
ORDER BY Numero DESC ;

/*
* Ejercicio 2
* Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable)
*/
SELECT emp.first_name AS Nombre, emp.last_name AS Apellido, sal.salary AS Salario, dept_nam.dept_name as Departamento
FROM employees.employees emp
     JOIN employees.salaries sal ON emp.emp_no = sal.emp_no
     JOIN employees.dept_emp dept ON emp.emp_no = dept.emp_no
     JOIN employees.departments dept_nam ON dept.dept_no=dept_nam.dept_no
/*Hay que poner el nombre del departamente entre comillas dobles*/
WHERE dept_nam.dept_name = ?
ORDER BY sal.salary DESC
LIMIT 1


/*
* Ejercicio 3
* Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable)
*/
SELECT emp.first_name AS Nombre, emp.last_name AS Apellido, sal.salary AS Salario, dept_nam.dept_name as Departamento
FROM employees.employees emp
     JOIN employees.salaries sal ON emp.emp_no = sal.emp_no
     JOIN employees.dept_emp dept ON emp.emp_no = dept.emp_no
     JOIN employees.departments dept_nam ON dept.dept_no=dept_nam.dept_no
/*Hay que poner el nombre del departamente entre comillas dobles*/
WHERE dept_nam.dept_name = ?
ORDER BY sal.salary DESC
LIMIT 1
OFFSET 1


/*
* Ejercicio 4
* Mostrar el número de empleados contratados en un mes concreto (parámetro variable)
*/
SELECT count(*) as NumeroEmpleados
FROM employees.employees emp
WHERE MONTH(emp.hire_date) = ?

