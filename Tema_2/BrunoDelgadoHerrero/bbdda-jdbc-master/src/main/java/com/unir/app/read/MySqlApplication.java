package com.unir.app.read;

import com.unir.config.MySqlConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class MySqlApplication {

    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        //Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.info("Conexión establecida con la base de datos MySQL");

            selectAllEmployeesOfDepartment(connection, "d001");
            selectAllEmployeesOfDepartment(connection, "d002");

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Ejemplo de consulta a la base de datos usando Statement.
     * Statement es la forma más básica de ejecutar consultas a la base de datos.
     * Es la más insegura, ya que no se protege de ataques de inyección SQL.
     * No obstante es útil para sentencias DDL.
     * @param connection
     * @throws SQLException
     */
    private static void selectAllEmployees(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("select * from employees");

        while (employees.next()) {
            log.debug("Employee: {} {}",
                    employees.getString("first_name"),
                    employees.getString("last_name"));
        }
    }

    /**
     * Ejemplo de consulta a la base de datos usando PreparedStatement.
     * PreparedStatement es la forma más segura de ejecutar consultas a la base de datos.
     * Se protege de ataques de inyección SQL.
     * Es útil para sentencias DML.
     * @param connection
     * @throws SQLException
     */
    private static void selectAllEmployeesOfDepartment(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select count(*) as 'Total'\n" +
                "from employees emp\n" +
                "inner join dept_emp dep_rel on emp.emp_no = dep_rel.emp_no\n" +
                "inner join departments dep on dep_rel.dept_no = dep.dept_no\n" +
                "where dep_rel.dept_no = ?;\n");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Empleados del departamento {}: {}",
                    department,
                    employees.getString("Total"));
        }
    }

    private static void countEmployeesByGender(Connection connection) throws SQLException {
        String query = """
            SELECT
                COUNT(CASE WHEN gender = 'M' THEN 1 END) AS HOMBRES,
                COUNT(CASE WHEN gender = 'F' THEN 1 END) AS MUJERES
            FROM employees
            """;
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                log.debug("Hombres: {}, Mujeres: {}", resultSet.getInt("HOMBRES"), resultSet.getInt("MUJERES"));
            }
        }
    }

    private static void highestPaidEmployeeInDepartment(Connection connection, String departmentName) throws SQLException {
        String query = """
            SELECT e.first_name, e.last_name, s.salary
            FROM employees e
                     JOIN salaries s ON e.emp_no = s.emp_no
                     JOIN dept_emp de ON e.emp_no = de.emp_no
                     JOIN departments d ON de.dept_no = d.dept_no
            WHERE d.dept_name = ?
            ORDER BY s.salary DESC
            LIMIT 1;
            """;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, departmentName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                log.debug("Mejor pagado en {}: {} {}, Salario: {}",
                        departmentName, resultSet.getString("first_name"), resultSet.getString("last_name"), resultSet.getDouble("salary"));
            }
        }
    }

    private static void secondHighestPaidEmployeeInDepartment(Connection connection, String departmentName) throws SQLException {
        String query = """
            SELECT first_name, last_name, salary
            FROM (
                SELECT e.first_name, e.last_name, s.salary,
                       ROW_NUMBER() OVER (PARTITION BY d.dept_no ORDER BY s.salary DESC) AS salary_rank
                FROM employees e
                         JOIN salaries s ON e.emp_no = s.emp_no
                         JOIN dept_emp de ON e.emp_no = de.emp_no
                         JOIN departments d ON de.dept_no = d.dept_no
                WHERE d.dept_name = ?
            ) AS RankedSalaries
            WHERE salary_rank = 2;
            """;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, departmentName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                log.debug("Segundo mejor pagado en {}: {} {}, Salario: {}",
                        departmentName, resultSet.getString("first_name"), resultSet.getString("last_name"), resultSet.getDouble("salary"));
            }
        }
    }

    private static void countEmployeesHiredInMonth(Connection connection, String year, String month) throws SQLException {
        String query = """
            SELECT COUNT(*)
            FROM employees
            WHERE hire_date >= ? AND hire_date < ?;
            """;
        String startDate = year + "-" + month + "-01";
        String endDate = year + "-" + (Integer.parseInt(month) + 1) + "-01";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, startDate);
            statement.setString(2, endDate);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                log.debug("Número de empleados contratados en {}/{}: {}", month, year, resultSet.getInt(1));
            }
        }
    }

}
