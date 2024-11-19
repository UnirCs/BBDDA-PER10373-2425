select FIRST_NAME, LAST_NAME, DEPARTMENT_NAME
from HR.EMPLOYEES
join HR.DEPARTMENTS ON EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID;




SELECT
XMLELEMENT("empleados",
    XMLATTRIBUTES(
        FIRST_NAME AS "nombre",
        LAST_NAME AS "apellidos",
        DEPARTMENT_NAME AS "departamento"))
    AS empleados
FROM HR.EMPLOYEES
JOIN HR.DEPARTMENTS ON EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID;





SELECT
    XMLELEMENT("managers",
        XMLAGG(
            XMLELEMENT("manager",
                XMLELEMENT("nombreCompleto",
                    XMLELEMENT("nombre", FIRST_NAME),
                    XMLELEMENT("apellido", LAST_NAME)
                ),
                XMLELEMENT("department", DEPARTMENT_NAME),
                XMLELEMENT("city", CITY),
                XMLELEMENT("country", COUNTRY_NAME)
            )
        )
    ) AS MANAGERS_XML
FROM HR.EMPLOYEES
JOIN HR.DEPARTMENTS ON EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID
JOIN HR.LOCATIONS ON DEPARTMENTS.LOCATION_ID = LOCATIONS.LOCATION_ID
JOIN HR.COUNTRIES ON LOCATIONS.COUNTRY_ID = COUNTRIES.COUNTRY_ID
JOIN HR.JOBS ON EMPLOYEES.JOB_ID = JOBS.JOB_ID
WHERE JOB_TITLE LIKE '%Manager%';




select FIRST_NAME, LAST_NAME, DEPARTMENT_NAME, CITY, COUNTRY_NAME, JOB_TITLE
FROM HR.EMPLOYEES
JOIN HR.DEPARTMENTS ON EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID
JOIN HR.LOCATIONS ON DEPARTMENTS.LOCATION_ID = LOCATIONS.LOCATION_ID
JOIN HR.COUNTRIES ON LOCATIONS.COUNTRY_ID = COUNTRIES.COUNTRY_ID
JOIN HR.JOBS ON EMPLOYEES.JOB_ID = JOBS.JOB_ID
WHERE JOB_TITLE LIKE '%Manager%';