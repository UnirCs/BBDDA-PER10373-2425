/* 1. Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento. */

SELECT EM.FIRST_NAME, EM.LAST_NAME, DE.DEPARTMENT_NAME
FROM HR.EMPLOYEES EM
JOIN HR.DEPARTMENTS DE ON EM.DEPARTMENT_ID = DE.DEPARTMENT_ID;



SELECT XMLELEMENT(
            "empleados",
            XMLATTRIBUTES(EM.FIRST_NAME AS "nombre", EM.LAST_NAME AS "apellido", DE.DEPARTMENT_NAME AS "departamento")
       ) AS EmpleadosXml
FROM HR.EMPLOYEES EM
JOIN HR.DEPARTMENTS DE ON EM.DEPARTMENT_ID = DE.DEPARTMENT_ID;

/* 2.Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers. */

SELECT EM.FIRST_NAME, EM.LAST_NAME, DE.DEPARTMENT_NAME, LO.CITY, CO.COUNTRY_NAME
FROM HR.EMPLOYEES EM
JOIN HR.DEPARTMENTS DE ON EM.DEPARTMENT_ID = DE.DEPARTMENT_ID
JOIN HR.LOCATIONS LO ON DE.LOCATION_ID = LO.LOCATION_ID
JOIN HR.COUNTRIES CO ON LO.COUNTRY_ID = CO.COUNTRY_ID
JOIN HR.JOBS JO ON EM.JOB_ID = JO.JOB_ID
WHERE JO.JOB_TITLE LIKE '%Manager%';



SELECT XMLELEMENT(
           "managers",
           XMLAGG(
               XMLELEMENT(
                   "manager",
                   XMLELEMENT(
                       "nombreCompleto",
                       XMLFOREST(EM.FIRST_NAME AS "nombre", EM.LAST_NAME AS "apellido")
                   ),
                   XMLELEMENT("department", DE.DEPARTMENT_NAME),
                   XMLELEMENT("city", LO.CITY),
                   XMLELEMENT("country", CO.COUNTRY_NAME)
               )
           )
       ) AS ManagersXml
FROM HR.EMPLOYEES EM
JOIN HR.DEPARTMENTS DE ON EM.DEPARTMENT_ID = DE.DEPARTMENT_ID
JOIN HR.LOCATIONS LO ON DE.LOCATION_ID = LO.LOCATION_ID
JOIN HR.COUNTRIES CO ON LO.COUNTRY_ID = CO.COUNTRY_ID
JOIN HR.JOBS JO ON EM.JOB_ID = JO.JOB_ID
WHERE JO.JOB_TITLE LIKE '%Manager%';

