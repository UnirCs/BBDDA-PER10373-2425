
SET @dep = "d009";
SET @month = 6;

/* 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente. */
SELECT e.gender, COUNT(e.emp_no) as Cantidad  FROM employees e 
GROUP BY e.gender 
ORDER BY Cantidad DESC; 

/* 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable). */
SELECT e.first_name, e.last_name, MAX(s.salary) FROM employees e 
JOIN dept_emp de on e.emp_no = de.emp_no
JOIN dept_manager dm on e.emp_no = dm.emp_no 
JOIN departments d on de.dept_no = d.dept_no and d.dept_no = dm.dept_no
JOIN salaries s on e.emp_no = s.emp_no 
WHERE d.dept_no = @dep
GROUP BY e.first_name, e.last_name 
LIMIT 1


/* 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable). */

SELECT e.first_name, e.last_name, MAX(s.salary) FROM employees e 
JOIN dept_emp de on e.emp_no = de.emp_no
JOIN dept_manager dm on e.emp_no = dm.emp_no 
JOIN departments d on de.dept_no = d.dept_no and d.dept_no = dm.dept_no
JOIN salaries s on e.emp_no = s.emp_no 
WHERE d.dept_no = @dep
GROUP BY e.first_name, e.last_name 
LIMIT 1
OFFSET 1;


/* 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).*/
SELECT COUNT(e.emp_no) as "Number of employees hired" FROM employees e
WHERE MONTH(e.hire_date) = @month



