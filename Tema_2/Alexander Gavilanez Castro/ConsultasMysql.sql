SELECT gender,count(*) as total from employees.employees GROUP BY gender ORDER BY total DESC;

/*-----------------------------------------------------------------*/

/*Introducir las comillas simples a mano en el parametro de entrada*/
SELECT e.first_name, e.last_name, MAX(s.salary) AS salary
FROM employees e
         JOIN dept_emp de ON e.emp_no = de.emp_no
         JOIN departments d ON de.dept_no = d.dept_no
         JOIN salaries s ON e.emp_no = s.emp_no
WHERE d.dept_name = ?
GROUP BY e.first_name, e.last_name
ORDER BY salary DESC
    LIMIT 1;

/*-----------------------------------------------------------------*/
/*Introducir las comillas simples a mano en el parametro de entrada*/


SELECT e.first_name, e.last_name, MAX(s.salary) AS salary
FROM employees e
         JOIN dept_emp de ON e.emp_no = de.emp_no
         JOIN departments d ON de.dept_no = d.dept_no
         JOIN salaries s ON e.emp_no = s.emp_no
WHERE d.dept_name = ?
GROUP BY e.first_name, e.last_name
ORDER BY salary DESC
    LIMIT 1
OFFSET 1;

/*-----------------------------------------------------------------*/
SELECT count(*) as total
FROM employees
WHERE MONTH(hire_date) = ?;