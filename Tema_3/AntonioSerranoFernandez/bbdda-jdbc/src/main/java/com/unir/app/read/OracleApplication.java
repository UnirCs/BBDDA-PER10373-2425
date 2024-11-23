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

            selectAllEmployees(connection);
            selectAllCountriesAsXml(connection);
            selectEmployeesDepartments(connection);
            selectEmployeesDepartmentsCitiesCountries(connection);

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

    // 1. Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
    private static void selectEmployeesDepartments(Connection connection) throws SQLException {
        String query = "SELECT XMLELEMENT(\"empleados\",\n" +
                "XMLATTRIBUTES(\n" +
                "E.FIRST_NAME AS \"nombre\",\n" +
                "E.LAST_NAME AS \"apellidos\",\n" +
                "D.DEPARTMENT_NAME AS \"departamento\"))\n" +
                "AS empleados\n" +
                "FROM EMPLOYEES E\n" +
                "JOIN DEPARTMENTS D ON D.DEPARTMENT_ID = E.DEPARTMENT_ID";

        Statement statement = connection.createStatement();
        ResultSet statement_res = statement.executeQuery(query);

        while (statement_res.next()) {
            log.debug("XML Empleado: {}", statement_res.getString("empleados"));
        }
    }

    // 2. Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
    private static void selectEmployeesDepartmentsCitiesCountries(Connection connection) throws SQLException {
        String query = "SELECT XMLELEMENT(\"managers\", " +
                "XMLAGG( " +
                "XMLELEMENT(\"manager\", " +
                "XMLELEMENT(\"nombreCompleto\", " +
                "XMLFOREST( " +
                "E.FIRST_NAME AS \"nombre\", " +
                "E.LAST_NAME AS \"apellido\" " +
                ") " +
                "), " +
                "XMLFOREST( " +
                "D.DEPARTMENT_NAME AS \"department\", " +
                "L.CITY AS \"city\", " +
                "C.COUNTRY_NAME AS \"country\" " +
                ") " +
                ") " +
                ") " +
                ") AS managers " +
                "FROM EMPLOYEES E " +
                "JOIN DEPARTMENTS D ON D.MANAGER_ID = E.EMPLOYEE_ID " +
                "JOIN LOCATIONS L ON D.LOCATION_ID = L.LOCATION_ID " +
                "JOIN COUNTRIES C ON L.COUNTRY_ID = C.COUNTRY_ID";

        Statement statement = connection.createStatement();
        ResultSet statement_res = statement.executeQuery(query);

        while (statement_res.next()) {
            log.debug("XML Managers: {}", statement_res.getString("managers"));
        }
    }
}
