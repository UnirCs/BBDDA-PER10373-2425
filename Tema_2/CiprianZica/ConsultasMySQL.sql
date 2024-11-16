#Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
SELECT gender, COUNT(*) AS total
FROM employees
GROUP BY gender
ORDER BY total DESC;

#Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
SELECT e.first_name, e.last_name, MAX(s.salary) as 'salary'
FROM employees e
         JOIN dept_emp de on e.emp_no = de.emp_no
         JOIN dept_manager dm on e.emp_no = dm.emp_no
         JOIN departments d on de.dept_no = d.dept_no and d.dept_no = dm.dept_no
         JOIN salaries s on e.emp_no = s.emp_no
WHERE d.dept_name = 'Marketing'
GROUP BY e.first_name, e.last_name
LIMIT 1;


#Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
SELECT e.first_name, e.last_name, MAX(s.salary)
FROM employees e
         JOIN dept_emp de on e.emp_no = de.emp_no
         JOIN dept_manager dm on e.emp_no = dm.emp_no
         JOIN departments d on de.dept_no = d.dept_no and d.dept_no = dm.dept_no
         JOIN salaries s on e.emp_no = s.emp_no
WHERE d.dept_name = 'Marketing'
GROUP BY e.first_name, e.last_name
LIMIT 1 OFFSET 1;

#Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
SELECT COUNT(*)
FROM employees.employees
WHERE hire_date BETWEEN '1990-01-01' AND '1990-01-31';
