#1. Obtener el número de hombres y mujeres y Ordenar de forma descendente.

select gender, count(*) as total from employees.employees
group by gender
order by total desc;

select gender, count(*) as total from employees.employees group by gender order by total desc;


#Total de Hombres: 179973
#Total de Mujeres: 120051

# 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
# ¿Como se relaciona la tabla de empleados con la de Salarios y departamentos?
#La tabla que relaciona los departamentos con los empleados es dept_emp y la tabla que almacena los salarios es salaries

select e.emp_no, d.dept_name, e.first_name, e.last_name
from employees.dept_emp de
join employees.departments d on de.dept_no = d.dept_no #Aquí se muestra a través de que atributo se relacionan las tablas
join employees.employees e on e.emp_no = de.emp_no
join employees.salaries s on s.emp_no = e.emp_no
where d.dept_no = "d008"
order by s.salary desc
limit 1;

select * from employees.departments
order by dept_no desc;

# Emp_no: 43624,Dept_name: Sales, First_name: Tokuyasu, Last_Name: Pesch

# 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).

select * from employees.salaries
order by salary desc;

#Haciendo está consulta el segundo empleado mejor pagado es: 254466
#Repetimos la query anterior:

select e.emp_no, d.dept_name, e.first_name, e.last_name, s.salary
from employees.dept_emp de
join employees.departments d on de.dept_no = d.dept_no
join employees.employees e on e.emp_no = de.emp_no
join employees.salaries s on s.emp_no = e.emp_no
where d.dept_no = "d008"
limit 1 offset 1;

# Emp_no: 254466,Dept_name: Sales, First_name: Honesty, Last_Name: Mukaidono

# 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).

select count(*) as cantidad from employees.employees
where hire_date between '19890101' and '19890131';

#Total de empleados en el mes de Enero del año 1989 es: 2530




