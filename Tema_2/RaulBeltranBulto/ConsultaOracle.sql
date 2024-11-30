-- (Debes usar XMLELEMENT) Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
-- Cada resultado XML devuelto por la consulta (la consulta debe devolver 1 registro por empleado) debe ser válido frente a este XML Schema:
/*
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xdb="http://xmlns.oracle.com/xdb">
	<xs:element name="empleados">
		<xs:complexType>
			<xs:attribute name="nombre" type="xs:string" />
			<xs:attribute name="apellidos" type="xs:string" />
			<xs:attribute name="departamento" type="xs:string"/>
		</xs:complexType>
	</xs:element>
</xs:schema>
 */
select xmlelement(
               "empleados",
               xmlattributes(e.first_name as "nombre",
               e.last_name as "apellido",
               d.DEPARTMENT_NAME as "departamento"
           )
       )as empleados
       from EMPLOYEES e
join DEPARTMENTS d
on e.DEPARTMENT_ID = d.DEPARTMENT_ID;

-- (Debes usar XMLELEMENT, XMLAGG y XMLFOREST) Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
-- El XML devuelto por la consulta (debe devolver un único registro, con todos los managers) debe ser válido frente a este XML Schema:

/*
 <?xml version = "1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xdb="http://xmlns.oracle.com/xdb" elementFormDefault="qualified">
    <xs:element name="managers">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="manager" minOccurs="0" maxOccurs="unbounded">
                    <xs:complexType>
                        <xs:sequence>
                            <xs:element name ="nombreCompleto">
                                <xs:complexType>
                                    <xs:sequence>
                                        <xs:element name = "nombre" type="xs:string"/>
                                        <xs:element name = "apellido" type="xs:string"/>
                                    </xs:sequence>
                                </xs:complexType>
                            </xs:element>
                            <xs:element name = "department" type="xs:string"/>
                            <xs:element name = "city" type="xs:string"/>
                           <xs:element name = "country" type="xs:string"/>
                        </xs:sequence>
                    </xs:complexType>
                </xs:element>
            </xs:sequence>
        </xs:complexType>
    </xs:element>
</xs:schema>
 */

select xmlelement(
       "managers",
       xmlagg(xmlelement(
              "manager",
              xmlelement(
              "nombreCompleto",
              xmlforest(
              e.first_name as "nombre",
              e.last_name as "apellido"
                         )
                        ),
              xmlforest(
              d.DEPARTMENT_NAME as "department"
                         ),
              xmlforest(
              l.city as "city"
              ),
              xmlforest(
              c.country_name as "country"
              )
                        )
                )
                        ) as managers
from EMPLOYEES e
join DEPARTMENTS d
    on e.DEPARTMENT_ID = d.DEPARTMENT_ID
join LOCATIONS l
    on d.LOCATION_ID = l.LOCATION_ID
join COUNTRIES c
on l.COUNTRY_ID = c.COUNTRY_ID;