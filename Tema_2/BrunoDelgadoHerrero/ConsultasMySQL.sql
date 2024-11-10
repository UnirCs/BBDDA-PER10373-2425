#1 Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
SELECT
    COUNT(CASE WHEN gender = 'M' THEN 1 END) AS "HOMBRES",
    COUNT(CASE WHEN gender = 'F' THEN 1 END) AS "MUJERES"
FROM employees.employees;

#2 Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
SELECT e.first_name, e.last_name, s.salary, d.dept_name
FROM employees.employees e
         JOIN employees.salaries s ON e.emp_no = s.emp_no
         JOIN employees.dept_emp de ON e.emp_no = de.emp_no
         JOIN employees.departments d ON de.dept_no = d.dept_no
         JOIN (
    SELECT de2.dept_no, MAX(s2.salary) AS max_salary
    FROM employees.salaries s2
             JOIN employees.dept_emp de2 ON s2.emp_no = de2.emp_no
    GROUP BY de2.dept_no
) AS max_salaries ON de.dept_no = max_salaries.dept_no AND s.salary = max_salaries.max_salary
WHERE d.dept_name = 'Customer Service';

#3 Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
SELECT first_name, last_name, salary
FROM (
         SELECT e.first_name, e.last_name, s.salary, d.dept_name,
                ROW_NUMBER() OVER (PARTITION BY d.dept_no ORDER BY s.salary DESC) AS salary_rank
         FROM employees.employees e
                  JOIN employees.salaries s ON e.emp_no = s.emp_no
                  JOIN employees.dept_emp de ON e.emp_no = de.emp_no
                  JOIN employees.departments d ON de.dept_no = d.dept_no
         WHERE d.dept_name = 'Customer Service'  -- Reemplaza con el nombre del departamento
     ) AS RankedSalaries
WHERE salary_rank = 2;

#4 Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
SELECT COUNT(*)
FROM employees.employees
WHERE hire_date >= '1999-01-01' AND hire_date < '1999-02-01';