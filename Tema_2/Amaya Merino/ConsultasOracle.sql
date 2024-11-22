-- 1. Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
SELECT e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME FROM hr.EMPLOYEES e, hr.DEPARTMENTS d
WHERE e.DEPARTMENT_ID = d.DEPARTMENT_ID;
-- 2. Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
SELECT e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, l.CITY, c.COUNTRY_NAME
FROM hr.EMPLOYEES e, hr.DEPARTMENTS d, hr.COUNTRIES c, hr.LOCATIONS l, hr.JOBS j
WHERE e.DEPARTMENT_ID = d.DEPARTMENT_ID AND e.JOB_ID = j.JOB_ID
AND l.COUNTRY_ID = c.COUNTRY_ID AND d.LOCATION_ID = l.LOCATION_ID
AND lower(j.JOB_TITLE) like '%manager%';
