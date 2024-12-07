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

            //Ejercicio 1
            selectAllEmployeesAsXml(connection);

            //Ejercicio 2
            selectAllManagersAsXml(connection);

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

    // 1. Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento
    private static void selectAllEmployeesAsXml(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT\n" +
                "  XMLELEMENT(\"empleados\",\n" +
                "       XMLATTRIBUTES(\n" +
                "         EM.FIRST_NAME AS \"nombre\",\n" +
                "         EM.LAST_NAME AS \"apellido\",\n" +
                "         DE.DEPARTMENT_NAME AS \"departamento\"))\n" +
                "  AS EmpleadosXml\n" +
                "FROM  HR.EMPLOYEES EM\n" +
                "JOIN HR.DEPARTMENTS DE ON EM.DEPARTMENT_ID = DE.DEPARTMENT_ID");

        ResultSet employees = selectEmployees.executeQuery();
        while (employees.next()) {
            log.debug("Empleados XML: {}", employees.getString("EmpleadosXML"));
        }
    }

    // 2. Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers
    private static void selectAllManagersAsXml(Connection connection) throws SQLException {
        PreparedStatement selectManagers = connection.prepareStatement("SELECT\n" +
                "  XMLELEMENT(\"managers\",\n" +
                "       XMLAGG(\n" +
                "           XMLELEMENT(\"manager\", \n" +
                "               XMLELEMENT(\"nombreCompleto\", \n" +
                "                   XMLFOREST(EM.FIRST_NAME AS \"nombre\", EM.LAST_NAME AS \"apellido\") \n" +
                "               ), \n" +
                "               XMLELEMENT(\"department\", DE.DEPARTMENT_NAME), \n" +
                "               XMLELEMENT(\"city\", LO.CITY), \n" +
                "               XMLELEMENT(\"country\", CO.COUNTRY_NAME) \n" +
                "           ) \n" +
                "       ) \n" +
                "  ) AS ManagersXml\n" +
                "FROM HR.EMPLOYEES EM\n" +
                "JOIN HR.DEPARTMENTS DE ON EM.DEPARTMENT_ID = DE.DEPARTMENT_ID\n" +
                "JOIN HR.LOCATIONS LO ON DE.LOCATION_ID = LO.LOCATION_ID\n" +
                "JOIN HR.COUNTRIES CO ON LO.COUNTRY_ID = CO.COUNTRY_ID\n" +
                "JOIN HR.JOBS JO ON EM.JOB_ID = JO.JOB_ID\n" +
                "WHERE JO.JOB_TITLE LIKE '%Manager%'");

        ResultSet managers = selectManagers.executeQuery();
        while (managers.next()) {
            log.debug("Managers XML: {}", managers.getString("ManagersXml"));
        }
    }
}
