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
            selectAllEmployees(connection);
            selectAllManagers(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
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
    private static void selectAllEmployees(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT\n" +
                "  XMLELEMENT(\"empleados\",\n" +
                "       XMLATTRIBUTES(\n" +
                "         e.FIRST_NAME AS \"nombre\",\n" +
                "         e.LAST_NAME AS \"apellidos\",\n" +
                "         d.DEPARTMENT_NAME AS \"departamento\"))\n" +
                "  AS empleados\n" +
                "FROM  hr.EMPLOYEES e, hr.DEPARTMENTS d\n" +
                "WHERE e.DEPARTMENT_ID = d.DEPARTMENT_ID");
        ResultSet employees = selectEmployees.executeQuery();
        while (employees.next()) {
            log.debug("Employees as XML: {}", employees.getString("empleados"));
        }
    }

    /**
     * Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers
     * @param connection
     * @throws SQLException
     */
    private static void selectAllManagers(Connection connection) throws SQLException {

        PreparedStatement selectManagers = connection.prepareStatement("SELECT\n" +
                "  XMLELEMENT(\"managers\",\n" +
                "   XMLAGG(\n" +
                "    XMLELEMENT(\"manager\", \n" +
                "     XMLELEMENT(\"nombreCompleto\",\n" +
                "       XMLFOREST(" +
                "         e.FIRST_NAME AS \"nombre\",\n" +
                "         e.LAST_NAME AS \"apellido\")),\n" +
                "     XMLELEMENT( \"department\", d.DEPARTMENT_NAME ), \n" +
                "     XMLELEMENT( \"city\", l.CITY  ),\n" +
                "     XMLELEMENT( \"country\", c.COUNTRY_NAME))))\n" +
                "  AS managers\n" +
                " FROM hr.EMPLOYEES e, hr.DEPARTMENTS d, hr.COUNTRIES c, hr.LOCATIONS l, hr.JOBS j \n" +
                " WHERE e.DEPARTMENT_ID = d.DEPARTMENT_ID AND e.JOB_ID = j.JOB_ID \n" +
                " AND l.COUNTRY_ID = c.COUNTRY_ID AND d.LOCATION_ID = l.LOCATION_ID \n" +
                " AND lower(j.JOB_TITLE) like '%manager%'");



        ResultSet managers = selectManagers.executeQuery();
        while (managers.next()) {
            log.debug("Managers as XML: {}", managers.getString("managers"));
        }
    }

}
