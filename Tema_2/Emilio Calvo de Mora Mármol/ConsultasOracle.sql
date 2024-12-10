/* author: Emilio Calvo de Mora Mármol */

/*
* Ejercicio 1
* Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento. Cada resultado XML devuelto por la consulta (la consulta debe devolver 1 registro por empleado) debe ser válido frente al XML Schema aportado
*/

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



/*
* Ejercicio 2
* Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers. El XML devuelto por la consulta (debe devolver un único registro, con todos los managers) debe ser válido frente al XML Schema aportado
*/

SELECT
    XMLELEMENT("managers",
        XMLAGG(
           XMLELEMENT("manager",
              XMLFOREST (
                  XMLFOREST(emp.FIRST_NAME as "nombre", emp.LAST_NAME as "apellido") as "nombreCompleto",
                  dept.DEPARTMENT_NAME as "department",
                  loc.CITY as "city",
                  coun.COUNTRY_NAME as "country"
              )
           )
        )
    ) AS managersXml
FROM EMPLOYEES emp
    JOIN DEPARTMENTS dept ON emp.DEPARTMENT_ID = dept.DEPARTMENT_ID
    JOIN LOCATIONS loc ON dept.LOCATION_ID = loc.LOCATION_ID
    JOIN COUNTRIES coun ON loc.COUNTRY_ID = coun.COUNTRY_ID
WHERE emp.EMPLOYEE_ID IN (SELECT DISTINCT man.MANAGER_ID FROM EMPLOYEES man);
