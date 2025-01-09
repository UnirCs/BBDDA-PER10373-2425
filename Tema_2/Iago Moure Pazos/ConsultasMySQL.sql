#Tarea 1 - Mostrar los 10 prmieros empleados y el max

SELECT * FROM employees.employees LIMIT 10;
SELECT MAX(emp_no) FROM employees.employees;

#Tarea 1 - Introducirme como empleado

INSERT INTO employees.employees (emp_no, birth_date, first_name, last_name, gender, hire_date)
VALUES (500000, '1981-08-14', 'Iago', 'Moure', 'M', '2024-10-25');
SELECT * FROM employees.employees WHERE emp_no = 500000;


#Tarea 2 - Número de hombres y mujeres ordenados de mayor a menor

SELECT gender, COUNT(*) AS count
FROM employees.employees
GROUP BY gender
ORDER BY count DESC;

#Tarea 2 - Empelado mejor pagado por departamento (parámetro variable)

SELECT e.first_name, e.last_name, s.salary
FROM employees.employees AS e
JOIN employees.dept_emp AS de ON e.emp_no = de.emp_no
JOIN employees.salaries AS s ON e.emp_no = s.emp_no
JOIN employees.departments AS d ON de.dept_no = d.dept_no
WHERE d.dept_name = ?
ORDER BY s.salary DESC
LIMIT 1;


#Tarea 2 - Empelado segundo mejor pagado por departamento (parámetro variable)

SELECT e.first_name, e.last_name, s.salary
FROM employees.employees AS e
JOIN employees.dept_emp AS de ON e.emp_no = de.emp_no
JOIN employees.salaries AS s ON e.emp_no = s.emp_no
JOIN employees.departments AS d ON de.dept_no = d.dept_no
WHERE d.dept_name = ?
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1;


#Tarea 2 - Número de empleados contratados en un mes concreto (parámetro variable)
SELECT COUNT(*) AS count_employees
FROM employees.employees AS e
WHERE MONTH(e.hire_date) = ?;

