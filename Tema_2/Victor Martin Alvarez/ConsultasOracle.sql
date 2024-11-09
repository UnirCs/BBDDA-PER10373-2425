-- Ejercicio 1
SELECT XMLElement(EVALNAME 'empleados',
            XMLATTRIBUTES (
                e.FIRST_NAME AS EVALNAME 'nombre',
                e.LAST_NAME AS EVALNAME 'apellidos',
                d.DEPARTMENT_NAME AS EVALNAME 'departamento'
            ))
FROM EMPLOYEES e
JOIN DEPARTMENTS d on d.DEPARTMENT_ID = e.DEPARTMENT_ID;

-- Ejercicio 2
SELECT
    XMLELEMENT(EVALNAME 'managers',
        XMLAGG(
            XMLELEMENT(EVALNAME 'manager',
                XMLCONCAT(
                    XMLELEMENT(EVALNAME 'nombreCompleto',
                        XMLCONCAT(
                            XMLELEMENT(EVALNAME 'nombre', m.FIRST_NAME),
                            XMLELEMENT(EVALNAME 'apellido', m.LAST_NAME)
                        )
                    ),
                    XMLCONCAT(
                        XMLELEMENT(EVALNAME 'department', d.DEPARTMENT_NAME),
                        XMLELEMENT(EVALNAME 'city', l.CITY),
                        XMLELEMENT(EVALNAME 'country', c.COUNTRY_NAME)
                    )
                )
            )
        )
    )
FROM EMPLOYEES m
JOIN EMPLOYEES e ON m.EMPLOYEE_ID = e.MANAGER_ID
JOIN DEPARTMENTS d ON m.DEPARTMENT_ID = d.DEPARTMENT_ID
JOIN LOCATIONS l ON l.LOCATION_ID = d.LOCATION_ID
JOIN COUNTRIES c on l.COUNTRY_ID = c.COUNTRY_ID;


