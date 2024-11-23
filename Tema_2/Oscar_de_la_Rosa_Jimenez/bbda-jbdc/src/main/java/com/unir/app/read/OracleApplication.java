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
            employeesAndDepartment(connection);
            employeesManager(connection);


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

    private static void employeesAndDepartment(Connection connection) throws SQLException {
        PreparedStatement employeesAndDepartment = connection.prepareStatement("SELECT \n" +
                " XMLELEMENT(\"empleados\", \n" +
                "       XMLATTRIBUTES( \n" +
                "           EMPLOYEES.FIRST_NAME AS \"nombre\", \n" +
                "           EMPLOYEES.LAST_NAME AS \"apellidos\", \n" +
                "           DEPARTMENTS.DEPARTMENT_NAME AS \"departamento\"))\n" +
                "   AS empleados\n" +
                "FROM HR.EMPLOYEES \n" +
                "JOIN HR.DEPARTMENTS ON EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID");

        ResultSet employeesDept = employeesAndDepartment.executeQuery();
        while (employeesDept.next()) {
            log.debug("empleados as XML: {}", employeesDept.getString("empleados"));
        }
    }


    private static void employeesManager(Connection connection) throws SQLException {
        PreparedStatement employeesManager = connection.prepareStatement(
                "SELECT \n" +
                " XMLELEMENT(\"managers\", \n" +
                "   XMLAGG( \n" +
                "       XMLELEMENT(\"manager\", \n" +
                "           XMLELEMENT(\"nombreCompleto\", \n" +
                "               XMLELEMENT(\"nombre\", FIRST_NAME), \n" +
                "               XMLELEMENT(\"apellido\", LAST_NAME)), \n" +
                "       XMLELEMENT(\"department\", DEPARTMENT_NAME), \n" +
                "       XMLELEMENT(\"city\", CITY), \n " +
                "       XMLELEMENT(\"country\", COUNTRY_NAME))) \n " +
                " ) AS MANAGERS_XML \n" +
                "FROM HR.EMPLOYEES \n" +
                "JOIN HR.DEPARTMENTS ON EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID \n"+
                "JOIN HR.LOCATIONS ON DEPARTMENTS.LOCATION_ID = LOCATIONS.LOCATION_ID \n" +
                "JOIN HR.COUNTRIES ON LOCATIONS.COUNTRY_ID = COUNTRIES.COUNTRY_ID \n" +
                "JOIN HR.JOBS ON EMPLOYEES.JOB_ID = JOBS.JOB_ID \n" +
                "WHERE JOB_TITLE LIKE '%Manager%'");

        ResultSet employeesMgr = employeesManager.executeQuery();
        while (employeesMgr.next()) {
            log.debug("managers as XML: {}", employeesMgr.getString("MANAGERS_XML"));
        }
    }
}
