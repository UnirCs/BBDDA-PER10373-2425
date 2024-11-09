# Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
SELECT e.gender as Generos,
       COUNT(*) as Cantidad
FROM employees.employees e
GROUP BY e.gender
ORDER BY Cantidad DESC;

# Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
SELECT e.first_name as Nombre,
       e.last_name as Apellido,
       s.salary as Salario
FROM employees.employees e,
     employees.salaries s,
     employees.dept_emp d
WHERE e.emp_no = s.emp_no
  AND e.emp_no = d.emp_no
  AND d.dept_no = ?
ORDER BY s.salary DESC
LIMIT 1;


# Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
SELECT e.first_name as Nombre,
       e.last_name as Apellido,
       s.salary as Salario
FROM employees.employees e,
     employees.salaries s,
     employees.dept_emp d
WHERE e.emp_no = s.emp_no
  AND e.emp_no = d.emp_no
  AND d.dept_no = ?
ORDER BY s.salary DESC
LIMIT 1 OFFSET 1;

# Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
SELECT MONTH(e.hire_date) as Mes,
       COUNT(*) as Cantidad
FROM employees.employees e
WHERE MONTH(e.hire_date) = ?
GROUP BY MONTH(e.hire_date);