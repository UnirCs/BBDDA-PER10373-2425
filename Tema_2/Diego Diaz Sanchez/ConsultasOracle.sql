'Consulta 1'
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

'Consulta 2'
SELECT XMLELEMENT(
           "managers",
           XMLAGG(
               XMLELEMENT(
                   "manager",
                   XMLELEMENT(
                       "nombreCompleto",
                       XMLFOREST(
                           e.first_name AS "nombre",
                           e.last_name AS "apellido"
                       )
                   ),
                   XMLFOREST(
                       d.department_name AS "department",
                       l.city AS "city",
                       c.country_name AS "country"
                   )
               )
           )
       ) AS managers_xml
FROM employees e
JOIN departments d ON e.department_id = d.department_id
JOIN locations l ON d.location_id = l.location_id
JOIN countries c ON l.country_id = c.country_id
WHERE e.employee_id IN (SELECT manager_id FROM employees)

