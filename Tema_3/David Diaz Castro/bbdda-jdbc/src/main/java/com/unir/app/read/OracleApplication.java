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

    /**
     * Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
     * @param connection
     * @throws SQLException
     */
    private static void selectEmployeeDepartment(Connection connection) throws SQLException {

        // Preparamos la consulta XML/SQL
        PreparedStatement selectEmployeeDepartment = connection.prepareStatement(
                "SELECT\n" +
                " XMLELEMENT(\"empleados\",\n" +
                " XMLATTRIBUTES(\n" +
                " E.FIRST_NAME AS \"nombre\",\n" +
                " E.LAST_NAME AS \"apellidos\",\n" +
                " D.DEPARTMENT_NAME AS \"departamento\"))\n" +
                " AS EMPLEADOS_XML\n" +
                " FROM EMPLOYEES E,\n" +
                " DEPARTMENTS D\n" +
                " WHERE E.DEPARTMENT_ID = D.DEPARTMENT_ID"
        );

        // Ejecutamos la consulta XML/SQL
        ResultSet employeeDepartment = selectEmployeeDepartment.executeQuery();

        // Mostramos los resultados de la consulta XML/SQL
        while (employeeDepartment.next()) {
            log.debug("Empleado y Departamento como XML: {}", employeeDepartment.getString("EMPLEADOS_XML"));
        }
    }

    /**
     * Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
     * @param connection
     * @throws SQLException
     */
    private static void selectManagers(Connection connection) throws SQLException {

        // Preparamos la consulta XML/SQL
        PreparedStatement selectManagers = connection.prepareStatement(
                "SELECT\n" +
                        " XMLELEMENT(\"managers\",\n" +
                        " XMLAGG(\n" +
                        " XMLELEMENT(\"manager\",\n" +
                        " XMLELEMENT(\"nombreCompleto\",\n" +
                        " XMLFOREST(\n" +
                        " e.first_name AS \"nombre\",\n" +
                        " e.last_name AS \"apellido\")),\n" +
                        " XMLFOREST(\n" +
                        " D.DEPARTMENT_NAME AS \"department\",\n" +
                        " L.CITY AS \"city\",\n" +
                        " C.COUNTRY_NAME AS \"country\"))))\n" +
                        " AS MANAGERS_XML\n" +
                        " FROM EMPLOYEES E,\n" +
                        " DEPARTMENTS D,\n" +
                        " LOCATIONS L,\n" +
                        " COUNTRIES C\n" +
                        " WHERE E.DEPARTMENT_ID = D.DEPARTMENT_ID\n" +
                        " AND D.LOCATION_ID = L.LOCATION_ID\n" +
                        " AND L.COUNTRY_ID = C.COUNTRY_ID\n"
        );

        // Ejecutamos la consulta XML/SQL
        ResultSet managers = selectManagers.executeQuery();

        // Mostramos los resultados de la consulta XML/SQL
        while (managers.next()) {
            log.debug("Managers como XML: {}", managers.getString("MANAGERS_XML"));
        }
    }
}