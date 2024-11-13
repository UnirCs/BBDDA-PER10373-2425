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
            highestPaidInDepartment(connection, "d001");
            secondHighestPaidInDepartment(connection, "d001");
            countEmployeesHiredInMonth(connection, 2020, 12); // Ejemplo para mayo de 2023
            listHiringMonths(connection);

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
    private static void countByGender(Connection connection) throws SQLException {
        String query = "SELECT gender, COUNT(*) AS cantidad FROM employees GROUP BY gender ORDER BY cantidad DESC";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                int count = resultSet.getInt("cantidad");
                log.info("Género: {}, Cantidad: {}", gender, count);
            }
        }
    }
    private static void highestPaidInDepartment(Connection connection, String department) throws SQLException {
        String query = "SELECT e.first_name, e.last_name, s.salary " +
                "FROM employees e " +
                "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                "JOIN salaries s ON e.emp_no = s.emp_no " +
                "WHERE de.dept_no = ? AND s.to_date = '9999-01-01' " +
                "ORDER BY s.salary DESC LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, department);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                int salary = resultSet.getInt("salary");
                log.info("Empleado mejor pagado en departamento {}: {} {}, Salario: {}", department, firstName, lastName, salary);
            }
        }
    }
    private static void secondHighestPaidInDepartment(Connection connection, String department) throws SQLException {
        String query = "SELECT e.first_name, e.last_name, s.salary " +
                "FROM employees e " +
                "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                "JOIN salaries s ON e.emp_no = s.emp_no " +
                "WHERE de.dept_no = ? AND s.to_date = '9999-01-01' " +
                "ORDER BY s.salary DESC LIMIT 1 OFFSET 1";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, department);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                int salary = resultSet.getInt("salary");
                log.info("Segundo empleado mejor pagado en departamento {}: {} {}, Salario: {}", department, firstName, lastName, salary);
            }
        }
    }
    private static void countEmployeesHiredInMonth(Connection connection, int year, int month) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM employees WHERE YEAR(hire_date) = ? AND MONTH(hire_date) = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, year);
            statement.setInt(2, month);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int total = resultSet.getInt("total");
                log.info("Empleados contratados en {}/{}: {}", month, year, total);
            }
        }
    }
    private static void listHiringMonths(Connection connection) throws SQLException {
        String query = "SELECT YEAR(hire_date) AS anio, MONTH(hire_date) AS mes, COUNT(*) AS num_contrataciones " +
                "FROM employees GROUP BY anio, mes ORDER BY anio, mes";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int year = resultSet.getInt("anio");
                int month = resultSet.getInt("mes");
                int count = resultSet.getInt("num_contrataciones");
                log.info("Año: {}, Mes: {}, Contrataciones: {}", year, month, count);
            }
        }
    }

}
