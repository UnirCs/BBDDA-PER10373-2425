--Gabriel Alejandro Pérez Pereira--

-- TAREA 2

-- Primer punto:

SELECT XMLELEMENT(
               "empleados",
               XMLATTRIBUTES(
                       EMPLOYEES.FIRST_NAME AS "nombre",
                       EMPLOYEES.LAST_NAME AS "apellidos",
                       DEPARTMENTS.DEPARTMENT_NAME AS "departamento"
               )
       ) AS employee_xml
FROM EMPLOYEES
         JOIN DEPARTMENTS ON EMPLOYEES.department_id = DEPARTMENTS.department_id;


-- Segundo punto:

SELECT XMLELEMENT(
               "managers",
               XMLAGG(
                       XMLELEMENT(
                               "manager",
                               XMLELEMENT(
                                       "nombreCompleto",
                                       XMLFOREST(
                                               EMPLOYEES.first_name AS "nombre",
                                               EMPLOYEES.last_name AS "apellido"
                                       )
                               ),
                               XMLFOREST(
                                       DEPARTMENTS.department_name AS "department",
                                       LOCATIONS.city AS "city",
                                       COUNTRIES.country_name AS "country"
                               )
                       )
               )
       ) AS managers_xml
FROM employees
         JOIN DEPARTMENTS ON EMPLOYEES.department_id = DEPARTMENTS.department_id
         JOIN LOCATIONS ON DEPARTMENTS.location_id = LOCATIONS.location_id
         JOIN COUNTRIES ON LOCATIONS.country_id = COUNTRIES.country_id;
