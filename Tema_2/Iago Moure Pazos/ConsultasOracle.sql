/*Tarea 1 - Mostrar los empleados y max*/

SELECT * FROM EMPLOYEES;
SELECT MAX(EMPLOYEE_ID) FROM EMPLOYEES;

/*Tarea 1 - Introducirme como empleado*/

INSERT INTO EMPLOYEES (employee_id, first_name, last_name, email, phone_number, hire_date, job_id, salary, commission_pct, manager_id, department_id)
VALUES (207, 'Iago', 'Moure', 'IMOURE', '666.777.8899', '25-OCT-2024', 'AD_VP', 23000, 0.40, 100, 90);
SELECT * FROM employees WHERE employee_id = 207;


/*Tarea 2 - Datos empleado (nombre y apellido) y departamento al que pertenece*/

SELECT
    XMLELEMENT("empleados",
           XMLATTRIBUTES(
               e.FIRST_NAME AS "nombre",
               e.LAST_NAME AS "apellidos",
               d.DEPARTMENT_NAME AS "departamento"))
        AS empleados_xml
FROM hr.EMPLOYEES e
JOIN hr.DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID;

/*Tarea 2 - Datos empleado (nombre y apellido) y departamento al que pertenece. Junto con ciudad y país de los empelados que son manager*/

SELECT XMLELEMENT("managers",
           XMLAGG(
               XMLELEMENT("manager",
                   XMLELEMENT("nombreCompleto",
                       XMLFOREST(e.FIRST_NAME AS "nombre", e.LAST_NAME AS "apellido")),
                   XMLFOREST(
                       d.DEPARTMENT_NAME AS "department",
                       l.CITY AS "city",
                       c.COUNTRY_NAME AS "country"))))
    AS managers_xml
FROM EMPLOYEES e
JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
JOIN LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID
JOIN COUNTRIES c ON l.COUNTRY_ID = c.COUNTRY_ID
WHERE e.EMPLOYEE_ID IN (SELECT DISTINCT MANAGER_ID FROM EMPLOYEES)