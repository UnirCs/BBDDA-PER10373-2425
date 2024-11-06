package com.unir.app.read;

import com.unir.config.OracleDatabaseConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class OracleApplication {

    private static final String SERVICE_NAME = "orcl";

    public static void main(String[] args) {

        // Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        // Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try (Connection connection = new OracleDatabaseConnector("localhost", SERVICE_NAME).getConnection()) {

            log.debug("Conexión establecida con la base de datos Oracle");

            selectAllEmployees(connection);
            selectAllCountriesAsXml(connection);
            selectAllEmployeesAsXml(connection);
            selectManagersAsXml(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    private static void selectAllEmployees(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("select * from EMPLOYEES");

        while (employees.next()) {
            log.debug("Employee: {} {}",
                    employees.getString("FIRST_NAME"),
                    employees.getString("LAST_NAME"));
        }
    }

    private static void selectAllCountriesAsXml(Connection connection) throws SQLException {
        PreparedStatement selectCountries = connection.prepareStatement("SELECT\n" +
                "  XMLELEMENT(\"countryXml\",\n" +
                "       XMLATTRIBUTES(\n" +
                "         c.country_name AS \"name\",\n" +
                "         c.region_id AS \"code\",\n" +
                "         c.country_id AS \"id\"))\n" +
                "  AS CountryXml\n" +
                "FROM  countries c\n" +
                "WHERE c.country_name LIKE ?");
        selectCountries.setString(1, "S%");

        ResultSet countries = selectCountries.executeQuery();
        while (countries.next()) {
            log.debug("Country as XML: {}", countries.getString("CountryXml"));
        }
    }

    private static void selectAllEmployeesAsXml(Connection connection) throws SQLException {
        String sql = """
                SELECT 
                    XMLELEMENT(
                        "empleados", 
                        XMLATTRIBUTES(
                            e.FIRST_NAME AS "nombre", 
                            e.LAST_NAME AS "apellidos", 
                            d.DEPARTMENT_NAME AS "departamento"
                        )
                    ).getClobVal() AS empleado_xml
                FROM 
                    employees e
                JOIN 
                    departments d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID;
                """;

        PreparedStatement selectEmployeesAsXml = connection.prepareStatement(sql);
        ResultSet resultSet = selectEmployeesAsXml.executeQuery();

        while (resultSet.next()) {
            log.debug("Employee as XML: {}", resultSet.getString("empleado_xml"));
        }
    }

    private static void selectManagersAsXml(Connection connection) throws SQLException {
        String sql = """
                SELECT 
                    XMLELEMENT(
                        "managers", 
                        XMLAGG(
                            XMLELEMENT(
                                "manager",
                                XMLELEMENT(
                                    "nombreCompleto",
                                    XMLFOREST(
                                        e.FIRST_NAME AS "nombre",
                                        e.LAST_NAME AS "apellido"
                                    )
                                ),
                                XMLFOREST(
                                    d.DEPARTMENT_NAME AS "department",
                                    l.CITY AS "city",
                                    c.COUNTRY_NAME AS "country"
                                )
                            )
                        )
                    ).getClobVal() AS managers_xml
                FROM 
                    employees e
                JOIN 
                    departments d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID
                JOIN 
                    locations l ON d.LOCATION_ID = l.LOCATION_ID
                JOIN 
                    countries c ON l.COUNTRY_ID = c.COUNTRY_ID
                WHERE 
                    e.EMPLOYEE_ID IN (SELECT DISTINCT MANAGER_ID FROM employees WHERE MANAGER_ID IS NOT NULL);
                """;

        PreparedStatement selectManagersAsXml = connection.prepareStatement(sql);
        ResultSet resultSet = selectManagersAsXml.executeQuery();

        if (resultSet.next()) {
            log.debug("Managers as XML: {}", resultSet.getString("managers_xml"));
        }
    }
}
