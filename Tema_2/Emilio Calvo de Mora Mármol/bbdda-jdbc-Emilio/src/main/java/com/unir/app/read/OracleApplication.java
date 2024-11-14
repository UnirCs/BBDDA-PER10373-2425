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

            selectNamesDepartmentsAsXml(connection);
            selectManagersInfo(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Ejercicio 1
     *  Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
     *  Cada resultado XML devuelto por la consulta (la consulta debe devolver 1 registro por empleado)
     *  debe ser válido frente al XML Schema aportado
     *
     * @param connection
     * @throws SQLException
     */
    private static void selectNamesDepartmentsAsXml(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("SELECT\n" +
                "    XMLELEMENT(\"empleados\",\n" +
                "        XMLATTRIBUTES (\n" +
                "           e.FIRST_NAME as \"nombre\",\n" +
                "           e.LAST_NAME as \"apellidos\",\n" +
                "           d.DEPARTMENT_NAME as \"departamento\"\n" +
                "        )\n" +
                "    )\n" +
                "AS empleados\n" +
                "FROM EMPLOYEES e JOIN DEPARTMENTS d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID");

        while (employees.next()) {
            log.debug("Employee: {}", employees.getString("empleados"));
        }
    }

    /**
     * Ejercicio 2
     * Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
     * El XML devuelto por la consulta (debe devolver un único registro, con todos los managers)
     * debe ser válido frente al XML Schema aportado
     * @param connection
     * @throws SQLException
     */
    private static void selectManagersInfo(Connection connection) throws SQLException {
        Statement selectManagers = connection.createStatement();
        ResultSet managers = selectManagers.executeQuery("SELECT\n" +
                "    XMLELEMENT(\"managers\",\n" +
                "       XMLAGG(\n" +
                "           XMLELEMENT(\"manager\",\n" +
                "                XMLFOREST (\n" +
                "                    XMLFOREST(emp.FIRST_NAME as \"nombre\", emp.LAST_NAME as \"apellido\") as \"nombreCompleto\",\n" +
                "                    dept.DEPARTMENT_NAME as \"department\",\n" +
                "                    loc.CITY as \"city\",\n" +
                "                    coun.COUNTRY_NAME as \"country\"\n" +
                "                )\n" +
                "           )\n" +
                "       )\n" +
                "    ) AS managersXml\n" +
                "FROM EMPLOYEES emp\n" +
                "    JOIN DEPARTMENTS dept ON emp.DEPARTMENT_ID = dept.DEPARTMENT_ID\n" +
                "    JOIN LOCATIONS loc ON dept.LOCATION_ID = loc.LOCATION_ID\n" +
                "    JOIN COUNTRIES coun ON loc.COUNTRY_ID = coun.COUNTRY_ID\n" +
                "WHERE emp.EMPLOYEE_ID IN (SELECT DISTINCT man.MANAGER_ID FROM EMPLOYEES man)");

        while (managers.next()) {
            log.debug("Managers: {}", managers.getString("managersXml"));
        }
    }
}
