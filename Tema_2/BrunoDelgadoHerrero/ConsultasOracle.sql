--1 Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
SELECT
    XMLELEMENT(
            "empleados",
            XMLATTRIBUTES(
                    e.FIRST_NAME AS "nombre",
                    e.LAST_NAME AS "apellidos",
                    d.DEPARTMENT_NAME AS "departamento"
            )
    ).getClobVal() AS empleado_xml
FROM
    employees e
        JOIN
    departments d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID;

--2 Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers
SELECT
    XMLELEMENT(
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
    ).getClobVal() AS managers_xml
FROM
    employees e
        JOIN
    departments d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
        JOIN
    locations l ON d.LOCATION_ID = l.LOCATION_ID
        JOIN
    countries c ON l.COUNTRY_ID = c.COUNTRY_ID
WHERE
    e.EMPLOYEE_ID IN (SELECT DISTINCT MANAGER_ID FROM employees WHERE MANAGER_ID IS NOT NULL);

