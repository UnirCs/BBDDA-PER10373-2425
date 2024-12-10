-- Consulta 1: Mostrar el nombre, apellido y nombre del departamento en XML
SELECT XMLELEMENT("empleados",
                  XMLATTRIBUTES(e.first_name AS "nombre",
                  e.last_name AS "apellidos",
                  d.department_name AS "departamento"))
FROM employees e
         JOIN departments d ON e.department_id = d.department_id;

-- Consulta 2: Mostrar información de los managers en formato XML
SELECT XMLSERIALIZE(
               DOCUMENT XMLELEMENT("managers",
                                   XMLAGG(
                                           XMLELEMENT("manager",
                                                      XMLELEMENT("nombreCompleto",
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
                        )
               INDENT
       )
FROM employees e
         JOIN departments d ON e.department_id = d.department_id
         JOIN locations l ON d.location_id = l.location_id
         JOIN countries c ON l.country_id = c.country_id
WHERE e.job_id LIKE '%MAN%';