SELECT XMLELEMENT(
    "empleados",
    XMLATTRIBUTES(
        e.first_name AS "nombre",
        e.last_name AS "apellidos",
        d.department_name AS "departamento"
    )
) AS empleado_xml
FROM employees e
JOIN departments d ON e.department_id = d.department_id;

SELECT
    XMLELEMENT("managers",
       XMLAGG(
           XMLELEMENT("manager",
                XMLELEMENT("nombreCompleto",
                    XMLELEMENT("nombre", e.FIRST_NAME),
                    XMLELEMENT("apellido", e.LAST_NAME)
                ),
                XMLELEMENT("department", d.DEPARTMENT_NAME),
                XMLELEMENT("city", l.CITY),
                XMLELEMENT("country", c.COUNTRY_NAME)
           )
       )
    ) AS managersXml
FROM EMPLOYEES e
JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
JOIN LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID
JOIN COUNTRIES c ON l.COUNTRY_ID = c.COUNTRY_ID
WHERE e.EMPLOYEE_ID IN (SELECT DISTINCT MANAGER_ID FROM EMPLOYEES WHERE MANAGER_ID IS NOT NULL);




