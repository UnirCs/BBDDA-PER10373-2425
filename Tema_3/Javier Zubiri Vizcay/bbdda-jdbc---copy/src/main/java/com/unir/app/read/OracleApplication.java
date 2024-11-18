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

            selectAllEmployeesWithDepartment(connection);
            selectAllManagers(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /*
    Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento.
     */
    private static void selectAllEmployeesWithDepartment(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("Select Xmlelement(\"empleados\",\n" +
                "       XMLATTRIBUTES (\n" +
                "           FIRST_NAME as \"nombre\",\n" +
                "           LAST_NAME as \"apellidos\",\n" +
                "           DEPARTMENT_NAME as \"departamento\"\n" +
                "       ))\n" +
                "as empleados\n" +
                "From EMPLOYEES\n" +
                "    inner join DEPARTMENTS on EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID");
        log.info("Nombres de empleados con su departamento en XML");
        while (employees.next()) {
            log.debug("{}",
                    employees.getString("empleados"));
        }
    }

    /*
    Mostrar el nombre, apellido, nombre de departamento, ciudad y pais de los empleados que son Managers.
     */
    private static void selectAllManagers(Connection connection) throws SQLException{
        Statement selectManagers = connection.createStatement();
        ResultSet managers = selectManagers.executeQuery("Select XMLELEMENT(\"managers\",\n" +
                "            XMLAGG(\n" +
                "                XMLELEMENT(\"manager\",\n" +
                "                    XMLELEMENT(\"nombreCompleto\",\n" +
                "                        XMLFOREST(\n" +
                "                            FIRST_NAME as \"nombre\",\n" +
                "                            LAST_NAME as \"apellido\"\n" +
                "                        )\n" +
                "                    ),\n" +
                "                    XMLFOREST(\n" +
                "                        DEPARTMENT_NAME as \"department\",\n" +
                "                        CITY as \"city\",\n" +
                "                        COUNTRY_NAME as \"country\"\n" +
                "                    )\n" +
                "                )\n" +
                "            )\n" +
                "       )\n" +
                "as managers\n" +
                "From EMPLOYEES\n" +
                "    inner join DEPARTMENTS on EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID\n" +
                "    inner join LOCATIONS on DEPARTMENTS.LOCATION_ID = LOCATIONS.LOCATION_ID\n" +
                "    inner join COUNTRIES on LOCATIONS.COUNTRY_ID = COUNTRIES.COUNTRY_ID\n" +
                "    inner join JOBS on EMPLOYEES.JOB_ID = JOBS.JOB_ID\n" +
                "Where UPPER(JOBS.JOB_TITLE) like '%MANAGER'");
        log.info("Datos de los managers en XML");
        while (managers.next())    {
        log.debug("{}",
                managers.getString("managers"));
        }
    }
}
