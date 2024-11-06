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

            // Ejercicios TEMA 2
            log.info("Ejercicios TEMA 2");
            getEmployeeNameAndDepartment(connection);
            getManagersDetails(connection);

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

    private static void getEmployeeNameAndDepartment(Connection connection) throws SQLException {
        PreparedStatement getEmployeeNameAndDepartment = connection.prepareStatement("SELECT XMLELEMENT(\"empleados\",\n"
                + "                  XMLATTRIBUTES(\n"
                + "                  e.FIRST_NAME AS \"nombre\",\n"
                + "                  e.LAST_NAME AS \"apellidos\",\n"
                + "                  d.DEPARTMENT_NAME AS \"departamento\"\n"
                + "           )\n"
                + "       ) AS empleados\n"
                + "FROM EMPLOYEES e\n"
                + "         JOIN HR.DEPARTMENTS d on e.DEPARTMENT_ID = d.DEPARTMENT_ID");

        ResultSet res = getEmployeeNameAndDepartment.executeQuery();

        while (res.next()) {
            log.debug("Empleados as XML: {}", res.getString("empleados"));
        }
    }

    private static void getManagersDetails(Connection connection) throws SQLException {
        PreparedStatement getManagersDetails = connection.prepareStatement("SELECT XMLELEMENT(\"managers\",\n"
                + "                  XMLAGG(\n"
                + "                          XMLELEMENT(\"manager\",\n"
                + "                                     XMLELEMENT(\"nombreCompleto\",\n"
                + "                                                XMLFOREST(FIRST_NAME AS \"nombre\", LAST_NAME AS \"apellido\")),\n"
                + "                                     XMLFOREST(DEPARTMENT_NAME AS \"department\", CITY AS \"city\", COUNTRY_NAME AS \"country\")\n"
                + "                          )\n"
                + "                  )\n"
                + "       ) as managers\n"
                + "FROM EMPLOYEES e\n"
                + "         JOIN HR.DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID\n"
                + "         JOIN HR.LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID\n"
                + "         JOIN HR.COUNTRIES c ON c.COUNTRY_ID = l.COUNTRY_ID\n"
                + "WHERE JOB_ID LIKE '%MAN'");

        ResultSet res = getManagersDetails.executeQuery();
        while (res.next()) {
            log.debug("Managers as XML: {}", res.getString("managers"));
        }
    }
}
