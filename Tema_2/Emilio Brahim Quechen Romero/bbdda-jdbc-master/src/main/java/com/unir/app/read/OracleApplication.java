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
            selectEmployeesNameAndDepartment(connection);
            selectManagersDetails(connection);

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
     * 1. Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
     * Cada resultado XML devuelto por la consulta (la consulta debe devolver 1 registro por empleado).
     */
    private static void selectEmployeesNameAndDepartment(Connection connection) throws SQLException {
        String query = "SELECT XMLELEMENT(\n" +
                "            \"empleados\",\n" +
                "            XMLELEMENT(\"nombre\", EMPLOYEES.FIRST_NAME),\n" +
                "            XMLELEMENT(\"apellidos\", EMPLOYEES.LAST_NAME),\n" +
                "            XMLELEMENT(\"departamento\", DEPARTMENTS.DEPARTMENT_NAME)\n" +
                "       ) AS empleados_xml\n" +
                "FROM EMPLOYEES\n" +
                "JOIN DEPARTMENTS ON EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID";

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet employees = statement.executeQuery();

        while (employees.next()) {
            log.debug("Ejercicio 1: {}", employees.getString("empleados_xml"));
        }
    }

    /**
     * 2. Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
     * El XML devuelto por la consulta (debe devolver un único registro, con todos los managers).
     */
    private static void selectManagersDetails(Connection connection) throws SQLException {
        String query = "SELECT XMLELEMENT(\n" +
                "            \"managers\",\n" +
                "            XMLAGG(\n" +
                "                XMLELEMENT(\n" +
                "                    \"manager\",\n" +
                "                    XMLELEMENT(\n" +
                "                        \"nombreCompleto\",\n" +
                "                        XMLFOREST(\n" +
                "                            EMPLOYEES.FIRST_NAME AS \"nombre\",\n" +
                "                            EMPLOYEES.LAST_NAME AS \"apellido\"\n" +
                "                        )\n" +
                "                    ),\n" +
                "                    XMLFOREST(\n" +
                "                        DEPARTMENTS.DEPARTMENT_NAME AS \"department\",\n" +
                "                        LOCATIONS.CITY AS \"city\",\n" +
                "                        COUNTRIES.COUNTRY_NAME AS \"country\"\n" +
                "                    )\n" +
                "                )\n" +
                "            )\n" +
                "       ) AS managers\n" +
                "FROM EMPLOYEES\n" +
                "JOIN DEPARTMENTS ON EMPLOYEES.EMPLOYEE_ID = DEPARTMENTS.MANAGER_ID\n" +
                "JOIN LOCATIONS ON DEPARTMENTS.LOCATION_ID = LOCATIONS.LOCATION_ID\n" +
                "JOIN COUNTRIES ON LOCATIONS.COUNTRY_ID = COUNTRIES.COUNTRY_ID";

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet managers = statement.executeQuery();

        while (managers.next()) {
            log.debug("Ejercicio 2: {}", managers.getString("managers"));
        }
    }
}
