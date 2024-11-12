#Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
SELECT gender, COUNT(*) AS total
FROM  employees
GROUP BY gender
ORDER BY total DESC;

#Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e
JOIN employees.salaries s ON e.emp_no = s.emp_no
JOIN employees.dept_emp de ON e.emp_no = de.emp_no
JOIN employees.departments d ON de.dept_no = d.dept_no
JOIN (
    SELECT de.dept_no, MAX(s.salary) AS max_salary
    FROM employees.employees e
    JOIN employees.salaries s ON e.emp_no = s.emp_no
    JOIN employees.dept_emp de ON e.emp_no = de.emp_no
    GROUP BY de.dept_no
) AS max_salaries ON de.dept_no = max_salaries.dept_no AND s.salary = max_salaries.max_salary
WHERE d.dept_name = 'Marketing';


#Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e
         JOIN employees.salaries s ON e.emp_no = s.emp_no
         JOIN employees.dept_emp de ON e.emp_no = de.emp_no
         JOIN employees.departments d ON de.dept_no = d.dept_no
WHERE d.dept_name = 'Marketing'
  AND s.salary = (
    SELECT DISTINCT s2.salary
    FROM employees.salaries s2
             JOIN employees.dept_emp de2 ON s2.emp_no = de2.emp_no
             JOIN employees.departments d2 ON de2.dept_no = d2.dept_no
    WHERE d2.dept_name = 'Marketing'
    ORDER BY s2.salary DESC
    LIMIT 1 OFFSET 1
);

#Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
SELECT COUNT(*)
FROM employees.employees
WHERE hire_date BETWEEN '1990-01-01' AND '1990-01-31';
