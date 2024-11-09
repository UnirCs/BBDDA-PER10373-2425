/*Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente*/
Select count(emp_no) as cantidad, gender
from employees.employees
group by gender
order by cantidad desc;

/*Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable)*/
Select employees.first_name, employees.last_name, salaries.salary
from employees.employees
    inner join employees.salaries on employees.emp_no=salaries.emp_no
    inner join employees.dept_emp on employees.emp_no = dept_emp.emp_no
    inner join employees.departments on departments.dept_no = dept_emp.dept_no
where salaries.salary = (select max(salaries.salary)
                         from employees.salaries
                            inner join employees.dept_emp on salaries.emp_no = dept_emp.emp_no
                            inner join employees.departments on dept_emp.dept_no = departments.dept_no
                         where departments.dept_name like "Development")
    and departments.dept_name like "Development";#nos aseguramos que no hay alguien en otro departemento con el mismo sueldo

/*Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).*/
Select employees.first_name, employees.last_name, salaries.salary
from employees.employees
    inner join employees.salaries on employees.emp_no=salaries.emp_no
    inner join employees.dept_emp on employees.emp_no = dept_emp.emp_no
    inner join employees.departments on departments.dept_no = dept_emp.dept_no
where salaries.salary = (select max(salaries.salary)
                         from employees.salaries
                            inner join employees.dept_emp on salaries.emp_no = dept_emp.emp_no
                            inner join employees.departments on dept_emp.dept_no = departments.dept_no
                         where salaries.salary < (select max(salaries.salary)
                                                  from employees.salaries
                                                  inner join employees.dept_emp on salaries.emp_no = dept_emp.emp_no
                                                  inner join employees.departments on dept_emp.dept_no = departments.dept_no
                                                  where departments.dept_name like "Development")
                           and departments.dept_name like "Development")
    and departments.dept_name like "Development";

/*Mostrar el número de empleados contratados en un mes concreto (parámetro variable).*/
Select count(distinct employees.emp_no) as "empleados", month(employees.hire_date) "mes"
from employees.employees
where month(employees.hire_date)=1
group by month(employees.hire_date)


