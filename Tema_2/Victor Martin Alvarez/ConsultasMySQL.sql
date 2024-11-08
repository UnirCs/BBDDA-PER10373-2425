-- 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
SELECT
    COUNT(*) AS 'COUNT',
    CASE
        WHEN gender = 'M' THEN 'Male'
        WHEN gender = 'F' THEN 'Female'
        ELSE 'Unknown'
        END AS 'gender'
FROM employees.employees
GROUP BY gender
ORDER BY 1 DESC;


-- SELECT dept_no, dept_name from employees.departments;

-- 2. Mostrar el nombre, apellido y salario de la persona mejor pagada
-- de un departamento concreto (parámetro variable).
set @`dept_name` :='Sales';
SELECT
    e.first_name, e.last_name, s.salary, d.dept_name
FROM employees.employees e
    JOIN employees.salaries s ON e.emp_no = s.emp_no
    JOIN employees.dept_emp de ON e.emp_no = de.emp_no
    JOIN employees.departments d ON de.dept_no = d.dept_no
WHERE d.dept_name = @`dept_name`
ORDER BY s.salary DESC
LIMIT 1;

-- 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada
-- de un departamento concreto (parámetro variable).
set @`dept_name` :='Finance';
SELECT
    e.first_name, e.last_name, s.salary, d.dept_name
FROM employees.employees e
    JOIN employees.salaries s ON e.emp_no = s.emp_no
    JOIN employees.dept_emp de ON e.emp_no = de.emp_no
    JOIN employees.departments d ON de.dept_no = d.dept_no
WHERE d.dept_name = @`dept_name`
ORDER BY s.salary DESC
LIMIT 1,1;

-- 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
set @`hiring_month` := 8;
SELECT
    count(e.emp_no)
FROM employees.employees e
    JOIN employees.dept_emp de ON e.emp_no = de.emp_no
    JOIN employees.departments d ON de.dept_no = d.dept_no
WHERE MONTH(de.from_date) = @`hiring_month`;