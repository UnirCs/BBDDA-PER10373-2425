--Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento
SELECT
    XMLELEMENT(
            "empleados",
            XMLATTRIBUTES(
            e.FIRST_NAME AS "nombre",
            e.LAST_NAME AS "apellidos",
            d.DEPARTMENT_NAME AS "departamento"
        )
    ) AS "Empleados"
FROM
    employees e
        JOIN
    departments d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID;

--Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers
SELECT
    XMLELEMENT(
        "empleados",
        XMLATTRIBUTES(
            e.FIRST_NAME AS "nombre",
            e.LAST_NAME AS "apellidos",
            d.DEPARTMENT_NAME AS "departamento",
            l.CITY AS "ciudad",
            l.COUNTRY_ID AS "pais"
        )
    ) AS "Empleados Managers"
FROM
    employees e
        JOIN
    departments d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
        JOIN
    locations l ON d.LOCATION_ID = l.LOCATION_ID
        JOIN
    JOBS j ON e.JOB_ID = j.JOB_ID
WHERE
    j.JOB_TITLE LIKE '%Manager';

