-- 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
select gender, count(*) as "cantidad" from employees.employees
group by gender
order by cantidad desc;

-- 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
select @departmentName;
set @departmentName = 'Customer Service';
select e.first_name, e.last_name, s.salary
from employees.employees e
    join employees.salaries s  on e.emp_no = s.emp_no
    join employees.dept_emp de on e.emp_no = de.emp_no
    join employees.departments d on de.dept_no = d.dept_no
where d.dept_name = @departmentName
order by s.salary desc
limit 1;

-- 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
select @departmentCode; -- obs: se utiliza codigo de departamento en vez de nombre por mostrar una alternativa distinta a la ofrecida en el ejercicio anterior
set @departmentCode = 'd001';
select e.first_name, e.last_name, s.salary
from employees.employees e
         join employees.salaries s  on e.emp_no = s.emp_no
         join employees.dept_emp de on e.emp_no = de.emp_no
where de.dept_no = @departmentCode
order by s.salary desc
limit 1
offset 1;

-- 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
select @monthNumber;
set @monthNumber = 1;
select count(*) as 'num_empleados' from employees.employees e
where MONTH(e.hire_date) = @monthNumber;