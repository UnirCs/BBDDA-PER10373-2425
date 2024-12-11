package org.example.app.read;

import lombok.extern.slf4j.Slf4j;
import org.example.connectors.MySqlConnector;

import java.sql.*;

@Slf4j
public class MySqlApp {
    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        try(Connection connection = new MySqlConnector(DATABASE).getConnection()) {
            log.info("Successfully connected to MySQL database");
            selectNumberOfGendersDesc(connection);
            selectBestDepartmentPaidEmployee(connection, "d009");
            selectSecondBestDepartmentPaidEmployee(connection, "d009");
            selectNumberOfHiredEmployeesByMonth(connection, 1);

        } catch (Exception e) {
            log.error("Failed to connect to MySQL database", e);
        }
    }

    private static void selectNumberOfGendersDesc (Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(
            "SELECT e.gender, COUNT(e.emp_no) as Cantidad  FROM employees e \n" +
                "GROUP BY e.gender \n" +
                "ORDER BY Cantidad DESC;"
        );
        ResultSet result = statement.getResultSet();
        while (result.next()) {
            log.debug(
                    result.getString("gender") + " " +
                    result.getInt("Cantidad")
            );
        }
    }

    private static void selectBestDepartmentPaidEmployee (Connection connection, String department) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
         "SELECT e.first_name, e.last_name, MAX(s.salary) FROM employees e \n" +
             "JOIN dept_emp de on e.emp_no = de.emp_no\n" +
             "JOIN dept_manager dm on e.emp_no = dm.emp_no \n" +
             "JOIN departments d on de.dept_no = d.dept_no and d.dept_no = dm.dept_no\n" +
             "JOIN salaries s on e.emp_no = s.emp_no \n" +
             "WHERE d.dept_no = ?\n" +
             "GROUP BY e.first_name, e.last_name \n" +
             "LIMIT 1"
        );
        statement.setString(1, department);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            log.debug(
                result.getString("first_name") + " " +
                result.getString("last_name") + " " +
                result.getInt("MAX(s.salary)")
            );
        }
    }

    private static void selectSecondBestDepartmentPaidEmployee (Connection connection, String department) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
        "SELECT e.first_name, e.last_name, MAX(s.salary) FROM employees e \n" +
            "JOIN dept_emp de on e.emp_no = de.emp_no\n" +
            "JOIN dept_manager dm on e.emp_no = dm.emp_no \n" +
            "JOIN departments d on de.dept_no = d.dept_no and d.dept_no = dm.dept_no\n" +
            "JOIN salaries s on e.emp_no = s.emp_no \n" +
            "WHERE d.dept_no = ?\n" +
            "GROUP BY e.first_name, e.last_name \n" +
            "LIMIT 1 \n" +
            "OFFSET 1;"
        );
        statement.setString(1, department);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            log.debug(
                result.getString("first_name") + " " +
                result.getString("last_name") + " " +
                result.getInt("MAX(s.salary)")
            );
        }
    }

    private static void selectNumberOfHiredEmployeesByMonth (Connection connection, Integer month) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
        "SELECT COUNT(e.emp_no) as \"Number of employees hired\" FROM employees e\n" +
            "WHERE MONTH(e.hire_date) = ?\n"
        );
        statement.setInt(1, month);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            log.debug(
                    String.valueOf(result.getInt("Number of employees hired"))
            );
        }
    }
}
