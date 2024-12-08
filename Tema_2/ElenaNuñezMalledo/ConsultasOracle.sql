--(Debes usar XMLELEMENT) Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
-- Cada resultado XML devuelto por la consulta (la consulta debe devolver 1 registro por empleado) debe ser válido frente a este XML Schema:

select XMLELEMENT("empleados", XMLATTRIBUTES (e.first_name as "nombre",
                                                e.last_name as "apellidos",
                                                d.department_name as departamento)) as EmpleadosXml
from EMPLOYEES e
join DEPARTMENTS d on e.DEPARTMENT_ID = d.DEPARTMENT_ID;

--(Debes usar XMLELEMENT, XMLAGG y XMLFOREST) Mostrar el nombre, apellido, nombre de departamento, ciudad y pais
-- de los empleados que son Managers. El XML devuelto por la consulta (debe devolver un único registro, con todos los managers)
-- debe ser válido frente a este XML Schema:

select XMLELEMENT(
               "managers",
                  XMLAGG(XMLELEMENT(
                                    "manager",
                                    XMLELEMENT(
                                            "nombreCompleto",
                                               XMLFOREST(e.FIRST_NAME as "nombre", e.LAST_NAME as "apellido")
                                    ),
                                    xmlelement("department", d.department_name),
                                    xmlelement("city", l.city),
                                    xmlelement("country", c.country_name)
                         )
                  )
       ) as managersXML
from EMPLOYEES e
join DEPARTMENTS d on e.DEPARTMENT_ID = d.DEPARTMENT_ID
join LOCATIONS l on l.LOCATION_ID=d.LOCATION_ID
join JOBS j on e.JOB_ID = j.JOB_ID
join COUNTRIES c on c.COUNTRY_ID = l.COUNTRY_ID
where j.JOB_TITLE like '%Manager%';

