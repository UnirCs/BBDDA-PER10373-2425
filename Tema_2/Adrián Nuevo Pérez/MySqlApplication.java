
package com.unir.app.read;

import com.unir.config.MySqlConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class MySqlApplication {

    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        try (Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.info("Conexión establecida con la base de datos MySQL");

            // Probar las consultas
            countGenderDistribution(connection);
            getHighestPaidEmployee(connection, "d001");
            getSecondHighestPaidEmployee(connection, "d001");
            countEmployeesHiredInMonth(connection, 4);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Consulta 1: Número de hombres y mujeres
     */
    private static void countGenderDistribution(Connection connection) throws SQLException {
        String query = "SELECT gender, COUNT(*) AS cantidad FROM employees GROUP BY gender ORDER BY cantidad DESC";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            log.info("Distribución de género:");
            while (resultSet.next()) {
                log.info("Género: {}, Cantidad: {}", resultSet.getString("gender"), resultSet.getInt("cantidad"));
            }
        }
    }

    /**
     * Consulta 2: Persona mejor pagada de un departamento específico
     */
    private static void getHighestPaidEmployee(Connection connection, String department) throws SQLException {
        String query = "SELECT e.first_name, e.last_name, s.salary, d.dept_no " +
                       "FROM employees e " +
                       "JOIN salaries s ON e.emp_no = s.emp_no " +
                       "JOIN dept_emp d ON e.emp_no = d.emp_no " +
                       "WHERE d.dept_no = ? " +
                       "ORDER BY s.salary DESC " +
                       "LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, department);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.info("Empleado mejor pagado del departamento {}: {} {} con salario {}",
                        department, resultSet.getString("first_name"),
                        resultSet.getString("last_name"), resultSet.getDouble("salary"));
            }
        }
    }

    /**
     * Consulta 3: Segunda persona mejor pagada de un departamento específico
     */
    private static void getSecondHighestPaidEmployee(Connection connection, String department) throws SQLException {
        String query = "SELECT e.first_name, e.last_name, s.salary, d.dept_no " +
                       "FROM employees e " +
                       "JOIN salaries s ON e.emp_no = s.emp_no " +
                       "JOIN dept_emp d ON e.emp_no = d.emp_no " +
                       "WHERE d.dept_no = ? " +
                       "ORDER BY s.salary DESC " +
                       "LIMIT 1 OFFSET 1";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, department);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.info("Segundo empleado mejor pagado del departamento {}: {} {} con salario {}",
                        department, resultSet.getString("first_name"),
                        resultSet.getString("last_name"), resultSet.getDouble("salary"));
            }
        }
    }

    /**
     * Consulta 4: Número de empleados contratados en un mes específico
     */
    private static void countEmployeesHiredInMonth(Connection connection, int month) throws SQLException {
        String query = "SELECT COUNT(*) AS total_empleados FROM employees WHERE MONTH(hire_date) = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, month);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.info("Número de empleados contratados en el mes {}: {}",
                        month, resultSet.getInt("total_empleados"));
            }
        }
    }
}
