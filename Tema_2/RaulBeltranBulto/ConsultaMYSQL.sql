-- 1. Consultas sobre MySQL
-- Utilizando DataGrip, ejecuta las siguientes consultas SQL en la base de datos employees de MySQL:

-- Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
select gender, COUNT(*) as num_total
from employees.employees
group by gender
order by num_total desc;

-- Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
select e.emp_no,e.first_name,e.last_name,s.salary, d.dept_name
from employees.salaries s
join employees.employees e
on s.emp_no = e.emp_no
join employees.dept_emp de
on e.emp_no = de.emp_no
join employees.departments d
on de.dept_no = d.dept_no
where de.dept_no = ? -- Between 'd001' a 'd009'
order by  s.salary desc
limit 1;

-- Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
 select e.emp_no,e.first_name,e.last_name,s.salary, d.dept_name
from employees.salaries s
join employees.employees e
on s.emp_no = e.emp_no
join employees.dept_emp de
on e.emp_no = de.emp_no
join employees.departments d
on de.dept_no = d.dept_no
where de.dept_no = ? -- Between 'd001' a 'd009'
order by  s.salary desc
limit 1
offset 1;

-- Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
select month(hire_date),count(*) as num_contratos
from employees.employees
where month(hire_date) =? -- Variable número del mes
group by month(hire_date);


-- Una vez realizadas las consultas, utiliza el código de apoyo para ejecutarlas a través de JDBC.
-- Deberás entregar un archivo ConsultasMySQL.sql con las consultas realizadas y el proyecto Java.