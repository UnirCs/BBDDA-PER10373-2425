/*
Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
*/
select count(*) as cantidad, gender from employees
group by gender
order by cantidad desc;

/*
Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
*/
select salaries.emp_no, first_name, last_name, salary from employees join salaries on employees.emp_no = salaries.emp_no
join employees.dept_emp de on employees.emp_no = de.emp_no join departments on de.dept_no = departments.dept_no
                                                                                               where dept_no = 'd001'
order by salary desc
LIMIT 1;

/*
Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
*/
select salaries.emp_no, first_name, last_name, salary from employees join salaries on employees.emp_no = salaries.emp_no
                                                                     join employees.dept_emp de on employees.emp_no = de.emp_no join departments on de.dept_no = departments.dept_no
where dept_name = 'Marketing'
order by salary desc
LIMIT 1 OFFSET 1;

/*
Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
*/
select count(*) as cantidad_contratada, DATE_FORMAT(de.from_date, '%M') as mes from employees join employees.dept_emp de on employees.emp_no = de.emp_no
where DATE_FORMAT(de.from_date, '%M') = 'October'
                                                                    group by mes;