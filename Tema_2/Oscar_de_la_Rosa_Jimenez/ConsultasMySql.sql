select employees.employees.gender, count(employees.employees.gender)  as total
from employees.employees
group by employees.employees.gender
order by count(employees.employees.gender) DESC;

select * from employees.employees;



SELECT employees.first_name, employees.last_name, salaries.salary
FROM employees.employees INNER JOIN employees.salaries ON employees.emp_no = salaries.emp_no
                         INNER JOIN employees.dept_emp ON employees.emp_no = dept_emp.emp_no
WHERE dept_emp.dept_no= "d002"
AND salaries.to_date > DATE(NOW())
AND salaries.salary = (
        SELECT MAX(salaries.salary)
        FROM employees.salaries
        INNER JOIN employees.dept_emp ON salaries.emp_no = dept_emp.emp_no
        WHERE dept_emp.dept_no = "d002" AND salaries.to_date > DATE(NOW()));



SELECT employees.first_name, employees.last_name, salaries.salary
FROM employees.employees INNER JOIN employees.salaries ON employees.emp_no = salaries.emp_no
                         INNER JOIN employees.dept_emp ON employees.emp_no = dept_emp.emp_no
WHERE dept_emp.dept_no= "d002"
AND salaries.to_date > DATE(NOW())
ORDER BY  salaries.salary DESC
LIMIT 1 OFFSET 1;



SELECT first_name, last_name, hire_date, MONTH(hire_date), YEAR(hire_date)
from employees.employees
WHERE MONTH(hire_date) = 5 AND YEAR(hire_date) = 1985;



SELECT COUNT(*) AS total
from employees.employees
WHERE MONTH(hire_date) = 5 AND YEAR(hire_date) = 1985;
