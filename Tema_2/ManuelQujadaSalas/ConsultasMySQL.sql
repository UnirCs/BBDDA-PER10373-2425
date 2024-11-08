

SELECT gender, COUNT(*) AS num_empleados
FROM employees.employees
GROUP BY gender
ORDER BY gender ASC;



SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e
JOIN employees.salaries s ON e.emp_no = s.emp_no
JOIN employees.dept_emp d ON e.emp_no = d.emp_no
JOIN employees.departments ds ON d.dept_no = ds.dept_no
WHERE ds.dept_name = 'marketing'
AND s.salary = (
    SELECT MAX(sal.salary)
    FROM employees.salaries sal
    JOIN employees.dept_emp de ON sal.emp_no = de.emp_no
    JOIN employees.departments dep ON de.dept_no = dep.dept_no
    WHERE dep.dept_name = 'marketing'
);



SELECT e.first_name, e.last_name, s.salary
FROM employees.employees e
JOIN employees.salaries s ON e.emp_no = s.emp_no
JOIN employees.dept_emp d ON e.emp_no = d.emp_no
JOIN employees.departments ds ON d.dept_no = ds.dept_no
WHERE ds.dept_name = 'marketing'
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1;



SELECT COUNT(*) AS n_empleados
FROM employees.employees
WHERE MONTH(hire_date) = 2;

