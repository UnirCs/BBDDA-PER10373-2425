# Gabriel Alejandro Pérez Pereira

# TAREA 2

# 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.

SELECT gender,count(*) AS total FROM employees.employees GROUP BY gender ORDER BY total DESC;

# 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).

SELECT first_name,last_name,salary,dept_no FROM employees.employees
JOIN employees.salaries ON employees.employees.emp_no = employees.salaries.emp_no
JOIN employees.dept_emp ON employees.employees.emp_no = employees.dept_emp.emp_no
WHERE dept_no = ? ORDER BY salary DESC LIMIT 1;

# 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).

SELECT first_name,last_name,salary,dept_no FROM employees.employees
JOIN employees.salaries ON employees.employees.emp_no = employees.salaries.emp_no
JOIN employees.dept_emp ON employees.employees.emp_no = employees.dept_emp.emp_no
WHERE dept_no = ? ORDER BY salary DESC LIMIT 1 OFFSET 1;

# 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).

SELECT count(*) AS total FROM employees.employees WHERE MONTH(hire_date) = ?;

