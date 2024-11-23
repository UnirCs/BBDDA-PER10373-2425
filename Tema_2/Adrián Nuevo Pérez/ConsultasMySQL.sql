-- Consulta 1: Contar el número de hombres y mujeres en la base de datos, ordenados de forma descendente
SELECT gender AS genero, COUNT(*) AS cantidad
FROM employees.employees
GROUP BY gender
ORDER BY cantidad DESC;

-- Consulta 2: Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
SELECT
    e.first_name AS nombre,
    e.last_name AS apellido,
    s.salary AS salario,
    d.dept_no AS departamento
FROM employees.employees AS e
JOIN employees.salaries AS s ON e.emp_no = s.emp_no
JOIN employees.dept_emp AS d ON e.emp_no = d.emp_no
WHERE d.dept_no = 'd001'
ORDER BY s.salary DESC
LIMIT 1;

-- Consulta 3: Mostrar el nombre, apellido, salario y número de departamento de la segunda persona mejor pagada de un departamento concreto
SELECT
    e.first_name AS nombre,
    e.last_name AS apellido,
    s.salary AS salario,
    d.dept_no AS departamento
FROM employees.employees AS e
JOIN employees.salaries AS s ON e.emp_no = s.emp_no
JOIN employees.dept_emp AS d ON e.emp_no = d.emp_no
WHERE d.dept_no = 'd001'
ORDER BY s.salary DESC
LIMIT 1
OFFSET 1;

-- Consulta 4: Mostrar el número de empleados contratados en un mes concreto
SELECT COUNT(*) AS total_empleados
FROM employees.employees
WHERE MONTH(hire_date) = 4;