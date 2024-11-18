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
        try(Connection connection = new OracleDatabaseConnector
                ("localhost", SERVICE_NAME).getConnection()) {

            log.debug("Conexión establecida con la base de datos Oracle");

            //selectAllEmployees(connection);
            //selectAllCountriesAsXml(connection);

            selectEmployeesByDepartment(connection);
            selectDepartmentsByCity(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Ejemplo de consulta a la base de datos usando Statement.
     * Statement es la forma más básica de ejecutar consultas a la base de datos.
     * Es la más insegura, ya que no se protege de ataques de inyección SQL.
     * No obstante, es útil para sentencias DDL.
     *
     * @param connection
     * @throws SQLException
     */
    private static void selectAllEmployees(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("select * from HR.EMPLOYEES");

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
     *
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
                "FROM  HR.countries c\n" +
                "WHERE c.country_name LIKE ?");
        selectCountries.setString(1, "S%");

        ResultSet countries = selectCountries.executeQuery();
        while (countries.next()) {
            log.debug("Country as XML: {}", countries.getString("CountryXml"));
        }
    }

    /**
     * Consulta 1: Obtener el número de empleados por departamento.
     * Usando PreparedStatement.
     *
     * @param connection : Conexion a la base de datos
     * @throws SQLException : Excepcion SQL
     */
    private static void selectEmployeesByDepartment(Connection connection) throws SQLException {

        PreparedStatement selectEmployees = connection.prepareStatement(
                "SELECT\n" +
                        "  dep.department_name,\n" +
                        "  COUNT(emp.employee_id) AS total\n" +
                        "FROM\n" +
                        "  HR.employees emp\n" +
                        "  JOIN HR.departments dep ON emp.department_id = dep.department_id\n" +
                        "GROUP BY\n" +
                        "  dep.department_name\n" +
                        "ORDER BY\n" +
                        "  total DESC");

        ResultSet employees = selectEmployees.executeQuery();
        while (employees.next()) {
            log.debug("Empleados del departamento {}: {}",
                    employees.getString("department_name"),
                    employees.getString("total"));
        }
    }

    /**
     * Consulta 2: Obtener el número de departamentos por ciudad.
     * Usando PreparedStatement.
     *
     * @param connection : Conexion a la base de datos
     * @throws SQLException : Excepcion SQL
     */
    private static void selectDepartmentsByCity(Connection connection) throws SQLException {

        PreparedStatement selectDepartments = connection.prepareStatement(
                "SELECT\n" +
                        "  loc.city,\n" +
                        "  COUNT(dep.department_id) AS total\n" +
                        "FROM\n" +
                        "  HR.departments dep\n" +
                        "  JOIN HR.locations loc ON dep.location_id = loc.location_id\n" +
                        "GROUP BY\n" +
                        "  loc.city\n" +
                        "ORDER BY\n" +
                        "  total DESC");

        ResultSet departments = selectDepartments.executeQuery();
        while (departments.next()) {
            log.debug("Departamentos en la ciudad {}: {}",
                    departments.getString("city"),
                    departments.getString("total"));
        }
    }
}
