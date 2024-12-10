-- 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.

SELECT gender, COUNT(*) AS total
FROM employees
GROUP BY gender
ORDER BY total DESC;


-- 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
SELECT @nombreDep;
SET @nombreDep = 'Customer Service';

SELECT e.first_name AS nombre, e.last_name AS apellido, s.salary AS salario
FROM employees e
JOIN dept_emp de ON e.emp_no = de.emp_no
JOIN salaries s ON e.emp_no = s.emp_no
JOIN departments d ON de.dept_no = d.dept_no
WHERE d.dept_name = @nombreDep
ORDER BY s.salary DESC
LIMIT 1;


-- 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
SELECT @nombreDep;
SET @nombreDep = 'Customer Service';

SELECT e.first_name AS nombre, e.last_name AS apellido, s.salary AS salario
FROM employees e
JOIN dept_emp de ON e.emp_no = de.emp_no
JOIN salaries s ON e.emp_no = s.emp_no
JOIN departments d ON de.dept_no = d.dept_no
WHERE d.dept_name = @nombreDep
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1;


-- 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
SELECT @numMes;
SET @numMes = 4;

SELECT COUNT(*) AS num_Empleados
FROM employees
WHERE MONTH(hire_date) = @numMes