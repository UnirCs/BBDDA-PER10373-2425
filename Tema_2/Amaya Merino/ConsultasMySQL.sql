-- 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
-- Se ha obtenido de toda la base de datos, independientemente si está en activo o no.
SELECT e.gender, count(1) as num_employees  FROM employees.employees e group by e.gender order by num_employees desc;
-- 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
-- Se establece un valor para un departamento
SET @departmentName = 'Customer Service';
-- No se ha tenido en cuenta si están activo o no el empleado ni si el sueldo es el actual
SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e, employees.departments d, employees.dept_emp de, employees.salaries s
WHERE de.emp_no = e.emp_no
AND de.dept_no = d.dept_no
AND s.emp_no = e.emp_no
AND d.dept_name = @departmentName
ORDER BY s.salary desc
LIMIT 1;
-- Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
    -- Se establece un valor para un departamento
SET @departmentName = 'Customer Service';
-- No se ha tenido en cuenta si están activo o no el empleado ni si el sueldo es el actual
SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e, employees.departments d, employees.dept_emp de, employees.salaries s
WHERE de.emp_no = e.emp_no
AND de.dept_no = d.dept_no
AND s.emp_no = e.emp_no
AND d.dept_name = @departmentName
ORDER BY s.salary desc
LIMIT 1, 1;
-- Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
SET @month = 1;
SELECT COUNT(1) FROM employees.employees e where month(e.hire_date) = @month;