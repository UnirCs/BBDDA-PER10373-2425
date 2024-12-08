SELECT XMLELEMENT(
               "empleados",
               XMLATTRIBUTES(
                       e.FIRST_NAME AS "nombre",
                       e.LAST_NAME AS "apellidos",
                       d.DEPARTMENT_NAME AS "departamento"
               )
       ) AS employee_xml
FROM EMPLOYEES e
         JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID;

/*-----------------------------------------------------------------*/


SELECT XMLELEMENT(
               "managers",
               XMLAGG(
                       XMLELEMENT(
                               "manager",
                               XMLELEMENT(
                                       "nombreCompleto",
                                       XMLFOREST(
                                               e.FIRST_NAME AS "nombre",
                                               e.LAST_NAME AS "apellido"
                                       )
                               ),
                               XMLFOREST(
                                       d.DEPARTMENT_NAME AS "department",
                                       l.CITY AS "city",
                                       c.COUNTRY_NAME AS "country"
                               )
                       )
               )
       ) AS manager_xml
FROM EMPLOYEES e
         JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
         JOIN LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID
         JOIN COUNTRIES c ON l.COUNTRY_ID = c.COUNTRY_ID
         JOIN JOBS j ON e.JOB_ID = j.JOB_ID
WHERE j.JOB_TITLE LIKE '%Manager';
