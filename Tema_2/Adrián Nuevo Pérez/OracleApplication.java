
package com.unir.app.read;

import com.unir.config.OracleDatabaseConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class OracleApplication {

    private static final String SERVICE_NAME = "orcl";

    public static void main(String[] args) {

        try (Connection connection = new OracleDatabaseConnector("localhost", SERVICE_NAME).getConnection()) {

            log.info("Conexión establecida con la base de datos Oracle");

            // Probar las consultas
            selectEmployeesAsXml(connection);
            selectManagersAsXml(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Consulta 1: Generar XML con nombre, apellido y departamento
     */
    private static void selectEmployeesAsXml(Connection connection) throws SQLException {
        String query = "SELECT XMLELEMENT("empleados", " +
                       "XMLATTRIBUTES(e.first_name AS "nombre", e.last_name AS "apellidos", d.department_name AS "departamento")) " +
                       "AS empleados_xml " +
                       "FROM employees e " +
                       "JOIN departments d ON e.department_id = d.department_id";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            log.info("XML de empleados:");
            while (resultSet.next()) {
                log.info(resultSet.getString("empleados_xml"));
            }
        }
    }

    /**
     * Consulta 2: Generar XML con información de todos los managers
     */
    private static void selectManagersAsXml(Connection connection) throws SQLException {
        String query = "SELECT XMLELEMENT("managers", " +
                       "XMLAGG( " +
                       "XMLELEMENT("manager", " +
                       "XMLELEMENT("nombreCompleto", " +
                       "XMLELEMENT("nombre", e.first_name), " +
                       "XMLELEMENT("apellido", e.last_name)), " +
                       "XMLELEMENT("department", d.department_name), " +
                       "XMLELEMENT("city", l.city), " +
                       "XMLELEMENT("country", c.country_name)))) " +
                       "AS managers_xml " +
                       "FROM employees e " +
                       "JOIN departments d ON e.department_id = d.department_id " +
                       "JOIN locations l ON d.location_id = l.location_id " +
                       "JOIN countries c ON l.country_id = c.country_id " +
                       "WHERE e.job_id LIKE '%MAN%'";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                log.info("XML de managers:");
                log.info(resultSet.getString("managers_xml"));
            }
        }
    }
}
