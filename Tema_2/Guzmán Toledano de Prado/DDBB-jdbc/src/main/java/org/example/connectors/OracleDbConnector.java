package org.example.connectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@Slf4j
@Getter
public class OracleDbConnector {
    private final Connection connection;

    /**
     * @param host
     * @param database
     */

    public OracleDbConnector(String host, String database) {
        try {
            this.connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@//" + host + ":1521/" + database,
                    System.getenv("ORACLE_USER"),
                    System.getenv("ORACLE_PASSWORD")
            );
        } catch (SQLException e) {
            log.error("Failed to connect to Oracle database", e);
            throw new RuntimeException(e);
        }
    }

}
