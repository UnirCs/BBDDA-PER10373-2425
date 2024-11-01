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

            selectEmployeesWithDepartment(connection);
            selectManagersWithDetails(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento
     * @param connection
     * @throws SQLException
     */
    private static void selectEmployeesWithDepartment(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("SELECT \n" +
                "    XMLELEMENT(\n" +
                "        NAME \"empleados\",\n" +
                "        XMLATTRIBUTES(\n" +
                "            e.first_name AS \"nombre\",\n" +
                "            e.last_name AS \"apellidos\",\n" +
                "            d.department_name AS \"departamento\"\n" +
                "        )\n" +
                "    ) AS empleado_xml\n" +
                "FROM \n" +
                "    employees e\n" +
                "JOIN \n" +
                "    departments d ON e.department_id = d.department_id");

        while (employees.next()) {
            log.debug("Employee: {}", employees.getString("empleado_xml"));
        }
    }


    /**
     * Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
     * @param connection
     * @throws SQLException
     */
    private static void selectManagersWithDetails(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet managers = selectEmployees.executeQuery("SELECT\n" +
                "    XMLELEMENT(\n" +
                "        NAME \"managers\",\n" +
                "        XMLAGG(\n" +
                "            XMLELEMENT(\n" +
                "                NAME \"manager\",\n" +
                "                XMLELEMENT(\n" +
                "                    NAME \"nombreCompleto\",\n" +
                "                    XMLFOREST(e.first_name AS \"nombre\", e.last_name AS \"apellido\")\n" +
                "                ),\n" +
                "                XMLFOREST(\n" +
                "                    d.department_name AS \"department\",\n" +
                "                    l.city AS \"city\",\n" +
                "                    c.country_name AS \"country\"\n" +
                "                )\n" +
                "            )\n" +
                "        )\n" +
                "    ) AS managers_xml\n" +
                "FROM\n" +
                "    employees e\n" +
                "JOIN\n" +
                "    employees m ON e.employee_id = m.manager_id\n" +
                "JOIN\n" +
                "    departments d ON e.department_id = d.department_id\n" +
                "JOIN\n" +
                "    locations l ON d.location_id = l.location_id\n" +
                "JOIN\n" +
                "    countries c ON l.country_id = c.country_id\n" +
                "WHERE\n" +
                "    e.manager_id IS NOT NULL");

        while (managers.next()) {
            log.debug("Managers: {}", managers.getString("managers_xml"));
        }
    }
}
