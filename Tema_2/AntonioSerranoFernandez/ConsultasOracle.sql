-- EJERCICIOS TEMA 2
-- Consultas sobre Oracle Database

-- 1. Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
-- Consulta SQL normal
SELECT E.FIRST_NAME, E.LAST_NAME, D.DEPARTMENT_NAME
FROM EMPLOYEES E
JOIN DEPARTMENTS D ON D.DEPARTMENT_ID = E.DEPARTMENT_ID;

-- Consulta con SQLX
SELECT XMLELEMENT("empleados",
XMLATTRIBUTES(
    E.FIRST_NAME AS "nombre",
    E.LAST_NAME AS "apellidos",
    D.DEPARTMENT_NAME AS "departamento"))
AS empleados
FROM EMPLOYEES E
JOIN DEPARTMENTS D ON D.DEPARTMENT_ID = E.DEPARTMENT_ID;

-- 2. Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
-- Consulta SQL normal
SELECT E.FIRST_NAME, E.LAST_NAME, D.DEPARTMENT_NAME, L.CITY, C.COUNTRY_NAME
FROM EMPLOYEES E
JOIN DEPARTMENTS D ON D.MANAGER_ID = E.EMPLOYEE_ID
JOIN LOCATIONS L ON D.LOCATION_ID = L.LOCATION_ID
JOIN COUNTRIES C ON L.COUNTRY_ID = C.COUNTRY_ID;

-- Consulta con SQLX
/*
El esquema de validación indica que debe haber un solo elemento "managers" que contenga múltiples elementos
"manager". Es por esto por lo que se usa XMLAGG, para asegurarse de que múltiples filas de resultados se
agrupen bajo un único elemento managers, en lugar de crear un elemento managers separado para cada fila.

XMLFOREST, por su parte, facilita la creación de múltiples subelementos de una fila sin tener que declarar
XMLELEMENT para cada uno, mejorando la legibilidad y eficiencia del código.
*/
SELECT XMLELEMENT("managers",
    XMLAGG(
        XMLELEMENT("manager",
            XMLELEMENT("nombreCompleto",
                XMLFOREST(
                    E.FIRST_NAME AS "nombre",
                    E.LAST_NAME AS "apellido"
                )
            ),
            XMLFOREST(
                D.DEPARTMENT_NAME AS "department",
                L.CITY AS "city",
                C.COUNTRY_NAME AS "country"
            )
        )
    )
) AS managers
FROM EMPLOYEES E
JOIN DEPARTMENTS D ON D.MANAGER_ID = E.EMPLOYEE_ID
JOIN LOCATIONS L ON D.LOCATION_ID = L.LOCATION_ID
JOIN COUNTRIES C ON L.COUNTRY_ID = C.COUNTRY_ID;

