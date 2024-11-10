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
        try(Connection connection = new OracleDatabaseConnector("localhost", SERVICE_NAME).getConnection()) {

            log.debug("Conexión establecida con la base de datos Oracle");

            //selectAllEmployees(connection);
            //selectAllCountriesAsXml(connection);

            /*Uncomment to test the methods below
            selectEmployeesDepartmentsXML(connection);
            selectManagersXML(connection);
            */

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

    private static void selectEmployeesDepartmentsXML(Connection connection) throws SQLException{
        PreparedStatement selectEmployeesDepartments = connection.prepareStatement("SELECT XMLELEMENT(" +
                "       \"empleados\"," +
                "       XMLATTRIBUTES(" +
                "           EMPLOYEES.FIRST_NAME AS \"nombre\"," +
                "           EMPLOYEES.LAST_NAME AS \"apellidos\"," +
                "           DEPARTMENTS.DEPARTMENT_NAME AS \"departamento\"" +
                "       )" +
                "   ) AS employee_xml " +
                "FROM EMPLOYEES " +
                "JOIN DEPARTMENTS ON EMPLOYEES.department_id = DEPARTMENTS.department_id");

        ResultSet employeesDepartments = selectEmployeesDepartments.executeQuery();
        while (employeesDepartments.next()) {
            log.debug("Employee as XML: {}", employeesDepartments.getString("employee_xml"));
        }
    }

    private static void selectManagersXML(Connection connection) throws SQLException{
        PreparedStatement selectEmployeesDepartments = connection.prepareStatement("SELECT XMLELEMENT(\n" +
                "               \"managers\",\n" +
                "               XMLAGG(\n" +
                "                       XMLELEMENT(\n" +
                "                               \"manager\",\n" +
                "                               XMLELEMENT(\n" +
                "                                       \"nombreCompleto\",\n" +
                "                                       XMLFOREST(\n" +
                "                                               EMPLOYEES.first_name AS \"nombre\",\n" +
                "                                               EMPLOYEES.last_name AS \"apellido\"\n" +
                "                                       )\n" +
                "                               ),\n" +
                "                               XMLFOREST(\n" +
                "                                       DEPARTMENTS.department_name AS \"department\",\n" +
                "                                       LOCATIONS.city AS \"city\",\n" +
                "                                       COUNTRIES.country_name AS \"country\"\n" +
                "                               )\n" +
                "                       )\n" +
                "               )\n" +
                "       ) AS managers_xml\n" +
                "FROM employees\n" +
                "         JOIN DEPARTMENTS ON EMPLOYEES.department_id = DEPARTMENTS.department_id\n" +
                "         JOIN LOCATIONS ON DEPARTMENTS.location_id = LOCATIONS.location_id\n" +
                "         JOIN COUNTRIES ON LOCATIONS.country_id = COUNTRIES.country_id");

        ResultSet managers = selectEmployeesDepartments.executeQuery();
        while (managers.next()) {
            log.debug("Manager as XML: {}", managers.getString("managers_xml"));
        }
    }

}
