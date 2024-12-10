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
            selectAllCountriesAsXml(connection);
            //selectAllEmployeesAsXml(connection);
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

    private static void selectAllEmployeesAsXml(Connection connection) throws SQLException {
        PreparedStatement selectEmpleados = connection.prepareStatement("SELECT\n" +
                        "           XMLELEMENT(\"empleados\",\n" +
                        "                XMLATTRIBUTES(\n" +
                        "                   e.FIRST_NAME as \"nombre\",\n" +
                        "                   e.LAST_NAME as \"apellidos\",\n" +
                        "                   d.DEPARTMENT_NAME as \"departamento\"))\n" +
                        "                AS empleados\n" +
                        "                FROM EMPLOYEES e JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID");;

        ResultSet empleados = selectEmpleados.executeQuery();
        while (empleados.next()) {
            log.debug("Empleados: {}", empleados.getString("empleados"));
        }
    }

    private static void selectAllManagersAsXml(Connection connection) throws SQLException {
        // 2.2.3. Nombre, apellidos, dept, ciudad y país de todos los managers
        //        (XMLELEMENT en UN solo registro (XMLAGG) y con estructura (XMLFOREST))
        PreparedStatement selectJefes = connection.prepareStatement("SELECT\n" +
                "    XMLELEMENT(\"managers\",\n" +
                "       XMLAGG(\n" +
                "           XMLELEMENT(\"manager\",\n" +
                "                XMLFOREST(\n" +
                "                    XMLFOREST(e.FIRST_NAME as \"nombre\", e.LAST_NAME as \"apellido\") as \"nombreCompleto\",\n" +
                "                    d.DEPARTMENT_NAME as \"department\",\n" +
                "                    l.CITY as \"city\",\n" +
                "                    c.COUNTRY_NAME as \"country\"\n" +
                "                )\n" +
                "           )\n" +
                "       )\n" +
                "    ) AS managers\n" +
                "FROM EMPLOYEES e\n" +
                "         JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID\n" +
                "         JOIN LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID\n" +
                "         JOIN COUNTRIES c ON l.COUNTRY_ID = c.COUNTRY_ID");

        ResultSet jefesXml = selectJefes.executeQuery();
        while (jefesXml.next()) {
            log.debug("Managers: {}", jefesXml.getString("managers"));
        }
    }
}
