SELECT gender AS 'Gender', count(*) AS 'GenderCount'
FROM employees.employees
GROUP BY gender
ORDER BY gender DESC;

SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e
INNER JOIN employees.dept_emp de ON e.emp_no = de.emp_no
INNER JOIN employees.departments d ON de.dept_no = d.dept_no
INNER JOIN employees.salaries s ON e.emp_no = s.emp_no
WHERE d.dept_name = 'Development'
ORDER BY s.salary DESC
LIMIT 1;

SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e
INNER JOIN employees.dept_emp de ON e.emp_no = de.emp_no
INNER JOIN employees.departments d ON de.dept_no = d.dept_no
INNER JOIN employees.salaries s ON e.emp_no = s.emp_no
WHERE d.dept_name = 'Development'
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1;

SELECT MONTH(e.hire_date) AS 'MesContratacion', COUNT(*) AS 'NumeroEmpleadosContratados'
FROM employees.employees e
WHERE MONTH(e.hire_date) = '08'
GROUP BY MesContratacion;

