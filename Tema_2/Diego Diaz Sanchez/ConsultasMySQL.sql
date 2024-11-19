USE employees;

'Consulta 1'
SELECT gender, COUNT(*) AS cantidad
FROM employees
GROUP BY gender
ORDER BY cantidad DESC;

'Consulta 2'
SELECT dept_no, dept_name 'Saber que departamentos hay'
FROM departments;

SELECT e.first_name, e.last_name, s.salary
FROM employees e
JOIN dept_emp de ON e.emp_no = de.emp_no
JOIN salaries s ON e.emp_no = s.emp_no
WHERE de.dept_no = 'd004' -- Reemplaza 'd001' con el código del departamento deseado
AND s.to_date = '9999-01-01' -- Para seleccionar solo el salario actual
ORDER BY s.salary DESC
LIMIT 1; -- Obtiene solo la persona mejor pagada

'Consulta 3'
SELECT e.first_name, e.last_name, s.salary
FROM employees e
JOIN dept_emp de ON e.emp_no = de.emp_no
JOIN salaries s ON e.emp_no = s.emp_no
WHERE de.dept_no = 'd001' -- Reemplaza 'd001' con el código del departamento deseado
AND s.to_date = '9999-01-01' -- Para seleccionar solo el salario actual
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1; -- Obtiene el segundo resultado (el segundo mejor salario)

'Consulta 4'
'En esta primera consulta veos en que mes hay algun contrato'
SELECT YEAR(hire_date) AS anio, MONTH(hire_date) AS mes, COUNT(*) AS num_contrataciones
FROM employees
GROUP BY anio, mes
ORDER BY anio, mes;

'Mostrar el número de empleados contratados en un mes concreto '
SELECT COUNT(*) AS num_empleados
FROM employees
WHERE MONTH(hire_date) = 1 -- Reemplaza 1 con el número del mes deseado (1 para enero, 2 para febrero, etc.)
AND YEAR(hire_date) = 2000; -- Reemplaza 2000 con el año deseado
