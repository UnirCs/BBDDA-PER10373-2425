package com.unir.app.read;

import com.unir.config.OracleDatabaseConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class OracleApplication {

    private static final String SERVICE_NAME = "orcl";

    public static void main(String[] args) {

        //Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try (Connection connection = new OracleDatabaseConnector("localhost", SERVICE_NAME).getConnection()) {

            log.debug("Conexión establecida con la base de datos Oracle");

            selectAllEmployees(connection);
            selectAllCountriesAsXml(connection);
            selectEmployeeDepartment(connection);
            selectManagers(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Ejemplo de consulta a la base de datos usando Statement.
     * Statement es la forma más básica de ejecutar consultas a la base de datos.
     * Es la más insegura, ya que no se protege de ataques de inyección SQL.
     * No obstante, es útil para sentencias DDL.
     *
     * @param connection
     * @throws SQLException
     */
    private static void selectAllEmployees(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("select * from EMPLOYEES");

        while (employees.next()) {
            log.debug("Employee: {} {}",
                    employees.getString("FIRST_NAME"),
                    employees.getString("LAST_NAME"));
        }
    }

    /**
     * Ejemplo de consulta a la base de datos usando PreparedStatement y SQL/XML.
     * Para usar SQL/XML, es necesario que la base de datos tenga instalado el módulo XDB.
     * En Oracle 19c, XDB viene instalado por defecto.
     * Ademas, se necesitan las dependencias que se encuentran en el pom.xml.
     *
     * @param connection
     * @throws SQLException
     */
    private static void selectAllCountriesAsXml(Connection connection) throws SQLException {
        PreparedStatement selectCountries = connection.prepareStatement("SELECT\n" +
                " XMLELEMENT(\"countryXml\",\n" +
                "XMLATTRIBUTES(\n" +
                "c.country_name AS \"name\",\n" +
                "c.region_id AS \"code\",\n" +
                "c.country_id AS \"id\"))\n" +
                "AS CountryXml\n" +
                "FROM  countries c\n" +
                "WHERE c.country_name LIKE ?");
        selectCountries.setString(1, "S%");

        ResultSet countries = selectCountries.executeQuery();
        while (countries.next()) {
            log.debug("Country as XML: {}", countries.getString("CountryXml"));
        }
    }

    // 1. Consulta que devuelve los empleados y su departamento como XML
    public static void selectEmployeeDepartment(Connection connection) throws SQLException {
        PreparedStatement selectEmployeeDepartment = connection.prepareStatement("SELECT\n" +
                "XMLELEMENT(\"empleados\",\n" +
                "XMLATTRIBUTES(\n" +
                "e.first_name AS \"nombre\",\n" +
                "e.last_name AS \"apellidos\",\n" +
                "d.department_name AS \"departamento\"))\n" +
                "AS employee_xml\n" +
                "FROM  employees e\n" +
                "JOIN departments d ON e.department_id = d.department_id");
        ResultSet employeeDepartment = selectEmployeeDepartment.executeQuery();
        while (employeeDepartment.next()) {
            log.debug("Empleado y departamento como XML: {}", employeeDepartment.getString("employee_xml"));
        }
    }

    // 2. Consulta que devuelve los managers de cada departamento como XML
    public static void selectManagers(Connection connection) throws SQLException {
        PreparedStatement selectManagers = connection.prepareStatement("SELECT XMLELEMENT(\n" +
                "               \"managers\",\n" +
                "               XMLAGG(\n" +
                "                       XMLELEMENT(\n" +
                "                               \"manager\",\n" +
                "                               XMLELEMENT(\n" +
                "                                       \"nombreCompleto\",\n" +
                "                                       XMLFOREST(\n" +
                "                                               e.first_name AS \"nombre\",\n" +
                "                                               e.last_name AS \"apellido\"\n" +
                "                                       )\n" +
                "                               ),\n" +
                "                               XMLFOREST(\n" +
                "                                       d.department_name AS \"department\",\n" +
                "                                       l.city AS \"city\",\n" +
                "                                       c.country_name AS \"country\"\n" +
                "                               )\n" +
                "                       )\n" +
                "               )\n" +
                "       ) AS managers_xml\n" +
                "FROM employees e\n" +
                "         JOIN departments d ON e.department_id = d.department_id\n" +
                "         JOIN locations l ON d.location_id = l.location_id\n" +
                "         JOIN countries c ON l.country_id = c.country_id");
        ResultSet managers = selectManagers.executeQuery();
        while (managers.next()) {
            log.debug("Managers como XML: {}", managers.getString("managers_xml"));
        }
    }
}
