/*Primera parte.*/
SELECT XMLELEMENT(
            "empleados",
            XMLELEMENT("nombre", EMPLOYEES.FIRST_NAME),
            XMLELEMENT("apellidos", EMPLOYEES.LAST_NAME),
            XMLELEMENT("departamento", DEPARTMENTS.DEPARTMENT_NAME)
       ) AS empleados_xml
FROM EMPLOYEES
JOIN DEPARTMENTS ON EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID;



/*Segunda parte.*/
SELECT XMLELEMENT(
            "managers",
            XMLAGG(
                XMLELEMENT(
                    "manager",
                    XMLELEMENT(
                        "nombreCompleto",
                        XMLFOREST(
                            EMPLOYEES.FIRST_NAME AS "nombre",
                            EMPLOYEES.LAST_NAME AS "apellido"
                        )
                    ),
                    XMLFOREST(
                        DEPARTMENTS.DEPARTMENT_NAME AS "department",
                        LOCATIONS.CITY AS "city",
                        COUNTRIES.COUNTRY_NAME AS "country"
                    )
                )
            )
       ) AS managers
FROM EMPLOYEES
JOIN DEPARTMENTS ON EMPLOYEES.EMPLOYEE_ID = DEPARTMENTS.MANAGER_ID
JOIN LOCATIONS ON DEPARTMENTS.LOCATION_ID = LOCATIONS.LOCATION_ID
JOIN COUNTRIES ON LOCATIONS.COUNTRY_ID = COUNTRIES.COUNTRY_ID;