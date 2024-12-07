# 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.

Select COUNT(emp_no) as total, gender
    from employees.employees
    group by gender
    order by total desc;

# 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).

SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e
JOIN employees.dept_emp de ON e.emp_no = de.emp_no
JOIN employees.salaries s ON e.emp_no = s.emp_no
WHERE de.dept_no = 'd009'
ORDER BY s.salary DESC
LIMIT 1;

# 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).

SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e
JOIN employees.dept_emp de ON e.emp_no = de.emp_no
JOIN employees.salaries s ON e.emp_no = s.emp_no
WHERE de.dept_no = 'd009'
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1;

# 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).

SELECT COUNT(*) AS num_employees
FROM employees.employees
WHERE MONTH(hire_date)=9;
