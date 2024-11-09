# (Debes usar XMLELEMENT)
# Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
# Cada resultado XML devuelto por la consulta (la consulta debe devolver 1 registro por empleado) debe ser válido frente a este XML Schema:
# https://www.ibm.com/docs/es/psfa/7.1.0?topic=reference-xmlelement-function
SELECT XMLELEMENT(
               "empleados",
               XMLATTRIBUTES(
               E.FIRST_NAME AS "nombre",
               E.LAST_NAME AS "apellidos",
               D.DEPARTMENT_NAME AS "departamento"
               )
       ) AS EMPLEADOS_XML
FROM EMPLOYEES E,
     DEPARTMENTS D
WHERE E.DEPARTMENT_ID = D.DEPARTMENT_ID;

# (Debes usar XMLELEMENT, XMLAGG y XMLFOREST)
# Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
# El XML devuelto por la consulta (debe devolver un único registro, con todos los managers) debe ser válido frente a este XML Schema:
# https://www.ibm.com/docs/es/psfa/7.1.0?topic=reference-xmlagg-aggregate
# https://www.ibm.com/docs/es/db2/11.1?topic=functions-xmlforest
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
                                       D.DEPARTMENT_NAME AS "department",
                                       L.CITY AS "city",
                                       C.COUNTRY_NAME AS "country"
                               )
                       )
               )
       ) AS MANAGERS_XML
FROM EMPLOYEES E,
     DEPARTMENTS D,
     LOCATIONS L,
     COUNTRIES C
WHERE E.DEPARTMENT_ID = D.DEPARTMENT_ID
  AND D.LOCATION_ID = L.LOCATION_ID
  AND L.COUNTRY_ID = C.COUNTRY_ID;