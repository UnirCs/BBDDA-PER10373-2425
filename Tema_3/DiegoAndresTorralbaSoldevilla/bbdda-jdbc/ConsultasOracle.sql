/*
Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
*/
SELECT XMLELEMENT("empleados",
                  XMLATTRIBUTES(
                  e.FIRST_NAME AS "nombre",
                  e.LAST_NAME AS "apellidos",
                  d.DEPARTMENT_NAME AS "departamento"
           )
       ) AS empleados
FROM EMPLOYEES e
         JOIN HR.DEPARTMENTS d on e.DEPARTMENT_ID = d.DEPARTMENT_ID;


/*
Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers
*/
SELECT XMLELEMENT("managers",
                  XMLAGG(
                          XMLELEMENT("manager",
                                     XMLELEMENT("nombreCompleto",
                                                XMLFOREST(FIRST_NAME AS "nombre", LAST_NAME AS "apellido")),
                                     XMLFOREST(DEPARTMENT_NAME AS "department", CITY AS "city", COUNTRY_NAME AS "country")
                          )
                  )
       )
FROM EMPLOYEES e
         JOIN HR.DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
         JOIN HR.LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID
         JOIN HR.COUNTRIES c ON c.COUNTRY_ID = l.COUNTRY_ID
WHERE JOB_ID LIKE '%MAN';
