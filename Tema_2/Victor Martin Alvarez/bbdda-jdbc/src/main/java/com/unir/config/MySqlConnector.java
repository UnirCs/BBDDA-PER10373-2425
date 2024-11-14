package com.unir.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@Slf4j
@Getter
public class MySqlConnector {
    private final Connection connection;

    /**
     * Constructor de la clase. Se conecta a la base de datos.
     * @param host
     * @param database
     */
    public MySqlConnector(String host, String database) {

        final String connection_string = "jdbc:mysql://" + host + ":" + 3306 + "/" + database;

        log.debug("connection_string: {}", connection_string);

        try {
            //Creamos la conexión a la base de datos
            this.connection = DriverManager.getConnection(
                    connection_string,
                    System.getenv("MYSQL_USER"),
                    System.getenv("MYSQL_PASSWORD"));

        } catch (SQLException e) {
            log.error("Error al conectar con la base de datos", e);
            throw new RuntimeException(e);
        }
    }
}
