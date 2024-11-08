SELECT * FROM EMPLOYEES;


SELECT  XMLELEMENT(
                "empleados",
                XMLFOREST(
                    e.FIRST_NAME AS "nombre",
                    e.LAST_NAME AS "apellidos",
                    d.DEPARTMENT_NAME AS "departamento"
                )
    ) AS empleado_xml
FROM HR.EMPLOYEES e
JOIN HR.DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID;

SELECT XMLELEMENT("managers",
           XMLAGG(
               XMLELEMENT("manager",
                   XMLELEMENT("nombreCompleto",
                       XMLFOREST(
                           e.FIRST_NAME AS "nombre",
                           e.LAST_NAME AS "apellido"
                       )),
                       XMLELEMENT("department", d.DEPARTMENT_NAME),
                       XMLELEMENT("city", l.CITY),
                       XMLELEMENT("country", c.COUNTRY_NAME)
               )
           )
       ) AS managers_xml
FROM HR.EMPLOYEES e
JOIN HR.DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
JOIN HR.LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID
JOIN HR.COUNTRIES c ON l.COUNTRY_ID = c.COUNTRY_ID
WHERE e.JOB_ID LIKE '%_MAN';