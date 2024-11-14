/* Se ha tenido que agregar un usuario nuevo debido a que en el establecimiento de la conexión 
 * la base de datos lanzaba un error al intentar entrar con el usuario SYS sin declarar el rol 
 * 
 * java.lang.RuntimeException: java.sql.SQLException: ORA-28009: connection as SYS should be as SYSDBA or SYSOPER*/

CREATE USER gtoledano IDENTIFIED BY oracle;

GRANT CONNECT TO gtoledano;
GRANT RESOURCE TO gtoledano;

GRANT DBA TO gtoledano;

/* Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento. */
SELECT 
	XMLELEMENT("empleados",
		XMLATTRIBUTES(
			e.FIRST_NAME AS "nombre",
			e.LAST_NAME AS "apellidos",
			d.DEPARTMENT_NAME AS "departamento"
		)
	) AS empleados
FROM EMPLOYEES e
JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID ;


/* Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers. */
SELECT 
	XMLELEMENT("managers",
		XMLAGG(
			XMLELEMENT("manager",
				XMLELEMENT("nombreCompleto", 
					XMLELEMENT("nombre", e.FIRST_NAME),
					XMLELEMENT("apellido", e.LAST_NAME)
				),
				XMLELEMENT("department", d.DEPARTMENT_NAME),
	            XMLELEMENT("city", l.CITY),
	            XMLELEMENT("country", r.REGION_NAME)
			),
		) 
	)
FROM EMPLOYEES e
JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
JOIN LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID
JOIN REGIONS r ON l.COUNTRY_ID = r.REGION_ID;

         
        
        