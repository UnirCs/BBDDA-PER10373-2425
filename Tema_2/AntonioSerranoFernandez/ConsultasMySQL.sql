-- EJERCICIOS TEMA 2
-- Consultas sobre MySQL

-- 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente

-- Primero muestra el número total de empleados
SELECT COUNT(*) AS total_empleados
FROM employees;

-- Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente
SELECT gender, COUNT(*) AS gender_count
FROM employees
GROUP BY gender
ORDER BY gender_count DESC;


-- 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).

-- Primero mostramos los posibles departamentos
SELECT dept_name
FROM departments;

-- Estaclecemos el parámetro del departamento p. ej. 'Customer Service'
SET @department_param = 'Customer Service';

-- Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
SELECT e.first_name, e.last_name, s.salary
FROM employees e
JOIN salaries s ON e.emp_no = s.emp_no
JOIN dept_emp de ON e.emp_no = de.emp_no
JOIN departments d ON de.dept_no = d.dept_no
WHERE d.dept_name = @department_param
ORDER BY s.salary DESC
LIMIT 1;

-- También se puede hacer un procedimiento almacenado
-- DROP PROCEDURE IF EXISTS GetHighestPaidEmployee;
DELIMITER //

CREATE PROCEDURE GetHighestPaidEmployee(IN dept_name_param VARCHAR(50))
BEGIN
    SELECT e.first_name, e.last_name, s.salary
    FROM employees e
    JOIN salaries s ON e.emp_no = s.emp_no
    JOIN dept_emp de ON e.emp_no = de.emp_no
    JOIN departments d ON de.dept_no = d.dept_no
    WHERE d.dept_name = @department_param
    ORDER BY s.salary DESC
    LIMIT 1;
END //

DELIMITER ;

-- Llamada al procedimiento
CALL GetHighestPaidEmployee('Sales');


-- 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
SET @department_param = 'Customer Service';

SELECT e.first_name, e.last_name, s.salary
FROM employees e
JOIN salaries s ON e.emp_no = s.emp_no
JOIN dept_emp de ON e.emp_no = de.emp_no
JOIN departments d ON de.dept_no = d.dept_no
WHERE d.dept_name = @department_param
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1;

-- Alternativamente
SELECT first_name, last_name, salary
FROM (
    SELECT e.first_name, e.last_name, s.salary,
           ROW_NUMBER() OVER (PARTITION BY d.dept_name ORDER BY s.salary DESC) AS fila
    FROM employees e
    JOIN salaries s ON e.emp_no = s.emp_no
    JOIN dept_emp de ON e.emp_no = de.emp_no
    JOIN departments d ON de.dept_no = d.dept_no
    WHERE d.dept_name = @department_param
) AS tabla_ordenada
WHERE fila = 2;


-- 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
SET @hire_month_param = 11;
SELECT COUNT(*) AS total_empleados
FROM employees
WHERE MONTH(hire_date) = @hire_month_param;
