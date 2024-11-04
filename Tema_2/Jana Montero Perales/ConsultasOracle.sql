-- 1. Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
SELECT
    XMLELEMENT("empleados",
        XMLATTRIBUTES (
           e.FIRST_NAME as "nombre",
           e.LAST_NAME as "apellidos",
           d.DEPARTMENT_NAME as "departamento"
        )
    )
AS empleados
FROM EMPLOYEES e JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID;

-- 2. Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
SELECT
    XMLELEMENT("managers",
       XMLAGG(
           XMLELEMENT("manager",
                XMLFOREST (
                    XMLFOREST(e.FIRST_NAME as "nombre", e.LAST_NAME as "apellido") as "nombreCompleto",
                    d.DEPARTMENT_NAME as "department",
                    l.CITY as "city",
                    c.COUNTRY_NAME as "country"
                )
           )
       )
    ) AS managersXml
FROM EMPLOYEES e
         JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
         JOIN LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID
         JOIN COUNTRIES c ON l.COUNTRY_ID = c.COUNTRY_ID
WHERE e.EMPLOYEE_ID IN (SELECT DISTINCT m.MANAGER_ID FROM EMPLOYEES m);