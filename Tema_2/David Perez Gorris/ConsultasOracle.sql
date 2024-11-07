SELECT
    XMLELEMENT("empleados",
    XMLATTRIBUTES(
        e.FIRST_NAME AS "nombre",
        e.LAST_NAME AS "apellidos",
        e.DEPARTMENT_ID AS "departamento"))
    AS empleados
FROM EMPLOYEES e;

SELECT e.first_name AS "nombre", e.last_name AS "apellido", d.department_name AS "department", l.city AS "city", c.country_name AS "country"
FROM EMPLOYEES e
INNER JOIN JOBS j ON e.JOB_ID = j.JOB_ID
INNER JOIN DEPARTMENTS d ON e.department_id = d.department_id
INNER JOIN LOCATIONS l ON d.location_id = l.location_id
INNER JOIN COUNTRIES c ON l.country_id = c.country_id
WHERE j.job_title LIKE '%Manager%';

SELECT XMLELEMENT("managers",
    XMLAGG(
        XMLELEMENT("manager",
            XMLELEMENT("nombreCompleto",
                XMLFOREST(
                    e.first_name AS "nombre",
                    e.last_name AS "apellido")
            ),
            XMLFOREST(
                d.department_name AS "department",
                l.city AS "city",
                c.country_name AS "country"
            )
        )
    )
) AS managers
FROM EMPLOYEES e
INNER JOIN JOBS j ON e.JOB_ID = j.JOB_ID
INNER JOIN DEPARTMENTS d ON e.department_id = d.department_id
INNER JOIN LOCATIONS l ON d.location_id = l.location_id
INNER JOIN COUNTRIES c ON l.country_id = c.country_id
WHERE j.job_title LIKE '%Manager%';
