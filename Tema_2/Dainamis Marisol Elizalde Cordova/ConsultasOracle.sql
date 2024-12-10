-- 1. Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento
SELECT XMLELEMENT(
            "empleados",
            XMLATTRIBUTES(
            e.FIRST_NAME AS "nombre",
            e.LAST_NAME AS "apellido",
            d.DEPARTMENT_NAME AS "departamento")
       ) AS EmpleadosXml
FROM HR.EMPLOYEES e
JOIN HR.DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID;

-- 2. Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers
SELECT XMLELEMENT(
           "managers",
           XMLAGG(
               XMLELEMENT(
                   "manager",
                   XMLELEMENT(
                       "nombreCompleto",
                       XMLFOREST(e.FIRST_NAME AS "nombre", e.LAST_NAME AS "apellido")
                   ),
                   XMLELEMENT("department", d.DEPARTMENT_NAME),
                   XMLELEMENT("city", l.CITY),
                   XMLELEMENT("country", c.COUNTRY_NAME)
               )
           )
       ) AS ManagersXml
FROM EMPLOYEES e
JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
JOIN LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID
JOIN COUNTRIES c ON l.COUNTRY_ID = c.COUNTRY_ID
JOIN JOBS j ON e.JOB_ID = j.JOB_ID
WHERE j.JOB_TITLE LIKE '%Manager%';

