package org.example.app.write;

import lombok.extern.slf4j.Slf4j;
import org.example.connectors.OracleDbConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class OracleApp {
    private static final String DATABASE = "orcl";

    public static void main(String[] args) {

        try(Connection connection = new OracleDbConnector("192.168.1.70", DATABASE).getConnection()){
            log.info("Successfully connected to Oracle database");
            selectEmployeesWithDepartment(connection);
            selectEmployeesDetails(connection);
        } catch (Exception e) {
            log.error("Failed to connect to Oracle database", e);
        }

    }

    private static void selectEmployeesWithDepartment (Connection connection) throws SQLException {
         PreparedStatement statement = connection.prepareStatement(
            "SELECT \n" +
            "\tXMLELEMENT(\"empleados\",\n" +
            "\t\tXMLATTRIBUTES(\n" +
            "\t\t\te.FIRST_NAME AS \"nombre\",\n" +
            "\t\t\te.LAST_NAME AS \"apellidos\",\n" +
            "\t\t\td.DEPARTMENT_NAME AS \"departamento\"\n" +
            "\t\t)\n" +
            "\t) AS empleados\n" +
            "FROM HR.EMPLOYEES e\n" +
            "JOIN HR.DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID"
        );

        ResultSet result = statement.executeQuery();
        while (result.next()) {
            log.debug(result.getString("empleados"));
        }
    }

    private static void selectEmployeesDetails (Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
        "SELECT \n" +
            "    XMLELEMENT(\"managers\",\n" +
            "        XMLAGG(\n" +
            "            XMLELEMENT(\"manager\",\n" +
            "\t            XMLELEMENT(\"nombreCompleto\",\n" +
            "\t\t\t        XMLFOREST(\n" +
            "\t\t\t        \te.FIRST_NAME AS \"nombre\",\n" +
            "\t\t\t        \te.LAST_NAME AS \"apellido\"\n" +
            "\t\t\t        ) \n" +
            "\t            ),\n" +
            "               \tXMLFOREST(\n" +
            "               \t\td.DEPARTMENT_NAME AS \"department\",\n" +
            "               \t\tl.CITY AS \"city\",\n" +
            "               \t\tc.COUNTRY_NAME AS \"country\"\n" +
            "               \t)\n" +
            "            )\n" +
            "        )\n" +
            "    ) AS managers\n" +
            "FROM HR.EMPLOYEES e\n" +
            "JOIN HR.DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID\n" +
            "JOIN HR.LOCATIONS l ON d.LOCATION_ID = l.LOCATION_ID\n" +
            "JOIN HR.COUNTRIES c ON l.COUNTRY_ID = c.COUNTRY_ID"
        );

        ResultSet result = statement.executeQuery();
        while (result.next()) {
            log.debug(result.getString("managers"));
        }
    }
}
