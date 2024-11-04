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
        try (Connection connection = new OracleDatabaseConnector("localhost", SERVICE_NAME).getConnection()) {

            log.debug("Conexión establecida con la base de datos Oracle");

            log.info("----------------------------------------------------------------------------");
            log.info("Resultados ejercicio 1:");
            selectAllEmployeesAsXml(connection);

            log.info("----------------------------------------------------------------------------");
            log.info("Resultados ejercicio 2:");
            selectAllManagersAsXml(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Implementacion del ejercicio de Oracle 1.
     * Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
     *
     * @param connection
     * @throws SQLException
     */
    private static void selectAllEmployeesAsXml(Connection connection) throws SQLException {
        Statement selectEmployeesAsXml = connection.createStatement();
        ResultSet employeesAsXml = selectEmployeesAsXml.executeQuery("SELECT\n" +
                "    XMLELEMENT(\"empleados\",\n" +
                "        XMLATTRIBUTES (\n" +
                "           e.FIRST_NAME as \"nombre\",\n" +
                "           e.LAST_NAME as \"apellidos\",\n" +
                "           d.DEPARTMENT_NAME as \"departamento\"\n" +
                "        )\n" +
                "    )\n" +
                "AS empleados\n" +
                "FROM EMPLOYEES e JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID");

        while (employeesAsXml.next()) {
            log.debug("Employee: {}", employeesAsXml.getString("empleados"));
        }
    }

    /**
     * Implementacion del ejercicio de Oracle 2.
     * Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
     *
     * @param connection
     * @throws SQLException
     */
    private static void selectAllManagersAsXml(Connection connection) throws SQLException {
        Statement selectManagersAsXml = connection.createStatement();
        ResultSet managersAsXml = selectManagersAsXml.executeQuery("SELECT\n" +
                "    XMLELEMENT(\"managers\",\n" +
                "       XMLAGG(\n" +
                "           XMLELEMENT(\"manager\",\n" +
                "                XMLFOREST (\n" +
                "                    XMLFOREST(e.FIRST_NAME as \"nombre\", e.LAST_NAME as \"apellido\") as \"nombreCompleto\",\n" +
                "                    d.DEPARTMENT_NAME as \"department\",\n" +
                "                    l.CITY as \"city\",\n" +
                "                    c.COUNTRY_NAME as \"country\"\n" +
                "                )\n" +
                "           )\n" +
                "       )\n" +
                "    ) AS managersXml\n" +
                "FROM EMPLOYEES e\n" +
                "         JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID\n" +
                "         JOIN LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID\n" +
                "         JOIN COUNTRIES c ON l.COUNTRY_ID = c.COUNTRY_ID\n" +
                "WHERE e.EMPLOYEE_ID IN (SELECT DISTINCT m.MANAGER_ID FROM EMPLOYEES m)");

        while (managersAsXml.next()) {
            log.debug("Managers: {}", managersAsXml.getString("managersXml"));
        }
    }
}
