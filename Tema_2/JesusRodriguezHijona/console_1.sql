
SELECT gender, COUNT(*) AS count
FROM employees
GROUP BY gender
ORDER BY count DESC;

SELECT e.first_name, e.last_name, s.salary
FROM employees e
JOIN salaries s ON e.emp_no = s.emp_no
JOIN dept_emp de ON e.emp_no = de.emp_no
JOIN departments d ON de.dept_no = d.dept_no
WHERE d.dept_no = 'd005'
ORDER BY s.salary DESC
LIMIT 1 ;

SELECT e.first_name, e.last_name, s.salary
FROM employees e
JOIN salaries s ON e.emp_no = s.emp_no
JOIN dept_emp de ON e.emp_no = de.emp_no
JOIN departments d ON de.dept_no = d.dept_no
WHERE d.dept_no = 'd005'
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1;

SELECT first_name, last_name, hire_date
FROM employees
WHERE MONTH(hire_date) = 07
ORDER BY hire_date;

SELECT COUNT(*) AS total_employees
FROM employees;