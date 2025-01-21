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

            selectEmployeesXML(connection);
            selectManagerXML (connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Consulta 1: (Debes usar XMLELEMENT) Mostrar el nombre y apellido de un empleado junto con el nombre de su departamento
     */
    private static void selectEmployeesXML(Connection connection) throws SQLException {
        String query = "SELECT XMLELEMENT(\"empleados\", " +
                        "XMLATTRIBUTES (e.first_name as \"nombre\",\n" +
                        "e.last_name as \"apellidos\",\n" +
                        "d.department_name as departamento)) as empleadosXml\n" +
                "from EMPLOYEES e\n" +
                "join DEPARTMENTS d on e.DEPARTMENT_ID = d.DEPARTMENT_ID";
    try (PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery()){

            log.info ("XML de empleados");
            while (resultSet.next()) {
                log.info (resultSet.getString("empleadosXml"));
            }
        }
    }
    /**
     * Consulta 2: (Debes usar XMLELEMENT, XMLAGG y XMLFOREST) Mostrar el nombre, apellido, nombre de departamento, ciudad y pais
     */

    private static void selectManagerXML(Connection connection) throws SQLException {
        String query = "select XMLELEMENT(\n" +
                "               \"managers\",\n" +
                "                  XMLAGG(XMLELEMENT(\n" +
                "                                    \"manager\",\n" +
                "                                    XMLELEMENT(\n" +
                "                                            \"nombreCompleto\",\n" +
                "                                               XMLFOREST(e.FIRST_NAME as \"nombre\", e.LAST_NAME as \"apellido\")\n" +
                "                                    ),\n" +
                "                                    xmlelement(\"department\", d.department_name),\n" +
                "                                    xmlelement(\"city\", l.city),\n" +
                "                                    xmlelement(\"country\", c.country_name)\n" +
                "                         )\n" +
                "                  )\n" +
                "       ) as managersXML\n" +
                "from EMPLOYEES e\n" +
                "join DEPARTMENTS d on e.DEPARTMENT_ID = d.DEPARTMENT_ID\n" +
                "join LOCATIONS l on l.LOCATION_ID=d.LOCATION_ID\n" +
                "join JOBS j on e.JOB_ID = j.JOB_ID\n" +
                "join COUNTRIES c on c.COUNTRY_ID = l.COUNTRY_ID\n" +
                "where j.JOB_TITLE like '%Manager%'";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()){

            log.info ("Managers XML");
            while (resultSet.next()) {
                log.info (resultSet.getString("managersXML"));
            }
        }
    }
}
