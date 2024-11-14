-- 1. Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.

SELECT
    XMLELEMENT(
        "empleados", 
        XMLATTRIBUTES(
            e.first_name AS "nombre", 
            e.last_name AS "apellidos", 
            d.department_name AS "departamento"
        )
    ) AS empleado_xml
FROM 
    hr.employees e
JOIN 
    hr.departments d ON e.department_id = d.department_id;

-- 2. Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.

SELECT
    XMLELEMENT(
        "managers",
        XMLAGG(
            XMLELEMENT(
                "manager",
                XMLELEMENT(
                    "nombreCompleto",
                    XMLELEMENT("nombre", e.first_name),
                    XMLELEMENT("apellido", e.last_name)
                ),
                XMLELEMENT("department", d.department_name),
                XMLELEMENT("city", l.city),
                XMLELEMENT("country", c.country_name)
            )
        )
    ) AS managers_xml
FROM
    hr.employees e
JOIN
    hr.departments d ON e.department_id = d.department_id
JOIN
    hr.locations l ON d.location_id = l.location_id
JOIN
    hr.countries c ON l.country_id = c.country_id
WHERE
    e.employee_id IN (SELECT manager_id FROM hr.departments);
