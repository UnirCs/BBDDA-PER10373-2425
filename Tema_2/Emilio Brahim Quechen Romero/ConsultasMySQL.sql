
/*  1.  Obtener el número de hombres y mujeres de la base
        de datos. Ordenar de forma descendente.*/
SELECT gender, COUNT(*) AS num_empleados
FROM employees.employees
GROUP BY gender
ORDER BY num_empleados DESC;


/*  2.  Mostrar el nombre, apellido y salario de la persona mejor
        pagada de un departamento concreto (parámetro variable).*/
SELECT employees.first_name, employees.last_name, salaries.salary
FROM employees.employees
JOIN employees.salaries     ON employees.emp_no = salaries.emp_no
JOIN employees.dept_emp     ON employees.emp_no = dept_emp.emp_no
JOIN employees.departments  ON dept_emp.dept_no = departments.dept_no
WHERE departments.dept_no = 'D004'
ORDER BY salaries.salary DESC
LIMIT 1;


/*  3.  Mostrar el nombre, apellido y salario de la segunda persona
        mejor pagada de un departamento concreto (parámetro variable).*/
SELECT employees.first_name, employees.last_name, salaries.salary
FROM employees.employees
JOIN employees.salaries     ON employees.emp_no = salaries.emp_no
JOIN employees.dept_emp     ON employees.emp_no = dept_emp.emp_no
JOIN employees.departments  ON dept_emp.dept_no = departments.dept_no
WHERE departments.dept_no = 'D004'
ORDER BY salaries.salary DESC
LIMIT 1 OFFSET 1;


/*  4.  Mostrar el número de empleados contratados
        en un mes concreto (parámetro variable).*/
SELECT COUNT(*) AS contratados_mayo_1985
FROM employees.employees
WHERE hire_date >= '1985-05-01' AND hire_date <= '1985-05-31';
