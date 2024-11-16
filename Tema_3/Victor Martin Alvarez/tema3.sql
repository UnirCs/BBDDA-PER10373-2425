-- ----------- --
-- Departments --
-- ----------- --

desc employees.departments;

-- Result before inserts: 9
-- Result before inserts: 33
select count(*) from employees.departments;

select * from employees.departments d
ORDER BY d.dept_no DESC;

-- --------- --
-- Employees --
-- --------- --

desc employees.employees;

-- Result before inserts: 300024 / 300124
select count(*) from employees.employees;

select * from employees.employees e
order by e.emp_no desc;



-- ----------------------- --
-- Departments - Employees --
-- ----------------------- --

desc employees.dept_emp;

-- Result before inserts: 331603 / 331703
select count(*) from employees.dept_emp;

select * from employees.dept_emp
ORDER BY emp_no DESC, dept_no DESC;

select * from employees.dept_emp
WHERE emp_no > 500001
ORDER BY emp_no DESC, dept_no DESC;