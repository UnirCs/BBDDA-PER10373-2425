package com.unir.app.read;

import com.unir.config.OracleDatabaseConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class OracleApplication {

    private static final String SERVICE_NAME = "orcl";

    public static void main(String[] args) {

        try(Connection connection = new OracleDatabaseConnector("localhost", SERVICE_NAME).getConnection()) {

            log.debug("Conexión establecida con la base de datos Oracle");

            log.info("Consulta 1");
            selectAllEmployees(connection);

            log.info("Consulta 2");
            selectAllManagers(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
     * Cada resultado XML devuelto por la consulta
     * Debes usar XMLELEMENT
     * (la consulta debe devolver 1 registro por empleado) debe ser válido.
     * @param connection
     * @throws SQLException
     */
    private static void selectAllEmployees(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("SELECT XMLELEMENT(\n" +
                "         \"empleados\",\n" +
                "         XMLATTRIBUTES(\n" +
                "             e.FIRST_NAME AS \"nombre\",\n" +
                "             e.LAST_NAME AS \"apellidos\",\n" +
                "             d.DEPARTMENT_NAME AS \"departamento\"\n" +
                "         )\n" +
                "       ) AS \"EmpleadoXML\"\n" +
                "FROM EMPLOYEES e\n" +
                "JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID");

        while (employees.next()) {
            log.debug("Empleado: {}",
                    employees.getString("EmpleadoXML")
            );
        }
    }

    /**
     * Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
     * El XML devuelto por la consulta (debe devolver un único registro, con todos los managers)
     * Debe ser válido frente al XML Schema dado.
     * Debes usar XMLELEMENT, XMLAGG y XMLFOREST
     * @param connection
     * @throws SQLException
     */
    private static void selectAllManagers(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("SELECT XMLELEMENT(\"managers\",\n" +
                "    XMLAGG(\n" +
                "        XMLELEMENT(\"manager\",\n" +
                "            XMLELEMENT(\"nombreCompleto\",\n" +
                "                XMLFOREST(e.FIRST_NAME as \"nombre\", e.LAST_NAME as \"apellido\")\n" +
                "            ),\n" +
                "            XMLELEMENT(\"department\", d.DEPARTMENT_NAME),\n" +
                "            XMLELEMENT(\"city\", l.CITY),\n" +
                "            XMLELEMENT(\"country\", c.COUNTRY_NAME)\n" +
                "        )\n" +
                "    )\n" +
                ")AS ManagersXML\n" +
                "FROM EMPLOYEES e\n" +
                "INNER JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID\n" +
                "INNER JOIN LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID\n" +
                "INNER JOIN COUNTRIES c ON l.COUNTRY_ID = c.COUNTRY_ID\n" +
                "WHERE e.EMPLOYEE_ID IN (SELECT DISTINCT manager_id FROM employees)");

        while (employees.next()) {
            log.debug("Managers: {}",
                    employees.getString("ManagersXML")
            );
        }
    }
}
