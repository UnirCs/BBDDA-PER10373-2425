# 1.Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.

SELECT gender, COUNT(*) as Total
FROM employees.employees
GROUP BY gender
ORDER BY Total DESC ;

# 2.Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).

SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e
JOIN employees.salaries s ON e.emp_no = s.emp_no
JOIN employees.dept_emp d ON e.emp_no = d.emp_no
WHERE d.dept_no = ?
ORDER BY s.salary DESC
LIMIT 1;

# 3.Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).

SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e
JOIN employees.salaries s ON e.emp_no = s.emp_no
JOIN employees.dept_emp d ON e.emp_no = d.emp_no
WHERE d.dept_no = ?
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1;

# 4.Mostrar el número de empleados contratados en un mes concreto (parámetro variable).

SELECT COUNT(*) as total_contratados
FROM employees.employees e
WHERE MONTH(e.hire_date) = ?;

