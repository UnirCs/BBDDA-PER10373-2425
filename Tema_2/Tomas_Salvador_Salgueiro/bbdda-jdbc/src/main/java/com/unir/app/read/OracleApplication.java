package com.unir.app.read;

import com.unir.config.OracleDatabaseConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class OracleApplication {

    private static final String SERVICE_NAME = "orcl";

    public static void main(String[] args) {

        // Creamos conexión. No es necesario indicar puerto en host si usamos el default, 1521
        // Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new OracleDatabaseConnector("localhost", SERVICE_NAME).getConnection()) {

            log.debug("Conexión establecida con la base de datos Oracle");

            selectAllEmployees(connection);
            selectAllCountriesAsXml(connection);

            // Llamadas a los nuevos ejercicios
            showEmployee(connection); // Ejercicio 1
            showManagers(connection); // Ejercicio 2

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Ejemplo de consulta a la base de datos usando Statement.
     * Statement es la forma más básica de ejecutar consultas a la base de datos.
     * Es la más insegura, ya que no se protege de ataques de inyección SQL.
     * No obstante, es útil para sentencias DDL.
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
     * @param connection
     * @throws SQLException
     */
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

    // Ejercicio 1: Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento en XML
    private static void showEmployee(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement(
            "SELECT XMLELEMENT(" +
            "       \"empleado\", " +
            "       XMLATTRIBUTES(" +
            "           e.first_name AS \"nombre\", " +
            "           e.last_name AS \"apellidos\", " +
            "           d.department_name AS \"departamento\"" +
            "       )" +
            "   ) AS empleado_xml " +
            "FROM hr.employees e " +
            "JOIN hr.departments d ON e.department_id = d.department_id"
        );
    
        ResultSet employees = selectEmployees.executeQuery();
        log.debug("Ejercicio 1: Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.");
        while (employees.next()) {
            log.debug("Empleado XML: {}", employees.getString("empleado_xml"));
        }
    }

    // Ejercicio 2: Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
    private static void showManagers(Connection connection) throws SQLException {
        PreparedStatement selectManagers = connection.prepareStatement(
            "SELECT XMLSERIALIZE(CONTENT XMLELEMENT(" +
            "       \"manager\", " +
            "       XMLELEMENT(\"nombreCompleto\", " +
            "           XMLELEMENT(\"nombre\", e.first_name), " +
            "           XMLELEMENT(\"apellido\", e.last_name)" +
            "       ), " +
            "       XMLELEMENT(\"department\", d.department_name), " +
            "       XMLELEMENT(\"city\", l.city), " +
            "       XMLELEMENT(\"country\", c.country_name)" +
            "   ) AS CLOB) AS manager_xml " +
            "FROM hr.employees e " +
            "JOIN hr.departments d ON e.department_id = d.department_id " +
            "JOIN hr.locations l ON d.location_id = l.location_id " +
            "JOIN hr.countries c ON l.country_id = c.country_id " +
            "WHERE e.employee_id IN (SELECT manager_id FROM hr.departments)"
        );
    
        ResultSet managers = selectManagers.executeQuery();
        log.debug("Ejercicio 2: Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.");
        while (managers.next()) {
            log.debug("Manager XML: {}", managers.getString("manager_xml"));
        }
    }
}
