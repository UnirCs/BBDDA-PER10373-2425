/*Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.*/
SELECT gender, COUNT(distinct emp_no) AS cantidad FROM employees.employees GROUP BY gender ORDER BY cantidad DESC;

/*Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).*/
SELECT em.first_name, em.last_name, (s.salary)
FROM employees.employees em
JOIN employees.salaries s ON em.emp_no = s.emp_no
JOIN employees.dept_emp deptem ON deptem.emp_no = em.emp_no
JOIN employees.departments dept ON dept.dept_no = deptem.dept_no
WHERE dept.dept_name = ?
order by s.salary DESC
limit 1;

/*Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).*/
SELECT em.first_name, em.last_name, s.salary
FROM employees.employees em
JOIN employees.salaries s ON em.emp_no = s.emp_no
JOIN employees.dept_emp deptem ON deptem.emp_no = em.emp_no
JOIN employees.departments dept ON dept.dept_no = deptem.dept_no
WHERE dept.dept_name = ?
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1;

/*Mostrar el número de empleados contratados en un mes concreto (parámetro variable).*/
select count(distinct em.emp_no) from employees.employees em
WHERE DATE_FORMAT(em.hire_date, '%Y-%m') = ?;