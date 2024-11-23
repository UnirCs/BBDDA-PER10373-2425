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

            log.info("Conexion establecida con la base de datos MySQL");

            //selectAllEmployeesOfDepartment(connection, "d005");
            //selectAllEmployeesOfDepartment(connection, "d005");
            selectGenderCount(connection);
            selectHighestPaidEmployee(connection, "d005");
            selectSecondHighestPaidEmployee(connection, "d005");
            selectEmployeesHiredInMonth(connection, 7);
            
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


    private static void selectHighestPaidEmployee(Connection connection, String department) throws SQLException {
        String sql = "SELECT e.first_name, e.last_name, s.salary " +
                     "FROM employees e " +
                     "JOIN salaries s ON e.emp_no = s.emp_no " +
                     "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                     "JOIN departments d ON de.dept_no = d.dept_no " +
                     "WHERE d.dept_no = ? " +
                     "ORDER BY s.salary DESC LIMIT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, department);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                log.info("Empleado mejor pagado: {} {}, Salario: {}", 
                         resultSet.getString("first_name"), 
                         resultSet.getString("last_name"), 
                         resultSet.getInt("salary"));
            }
        }
    }

    private static void selectSecondHighestPaidEmployee(Connection connection, String department) throws SQLException {
        String sql = "SELECT e.first_name, e.last_name, s.salary " +
                     "FROM employees e " +
                     "JOIN salaries s ON e.emp_no = s.emp_no " +
                     "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                     "JOIN departments d ON de.dept_no = d.dept_no " +
                     "WHERE d.dept_no = ? " +
                     "ORDER BY s.salary DESC LIMIT 1 OFFSET 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, department);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                log.info("Segundo empleado mejor pagado: {} {}, Salario: {}", 
                         resultSet.getString("first_name"), 
                         resultSet.getString("last_name"), 
                         resultSet.getInt("salary"));
            }
        }
    }

    private static void selectEmployeesHiredInMonth(Connection connection, int month) throws SQLException {
        String sql = "SELECT COUNT(*) AS total_employees FROM employees WHERE MONTH(hire_date) = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, month);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                log.info("Total de empleados contratados en el mes {}: {}", month, resultSet.getInt("total_employees"));
            }
        }
    }

    private static void selectGenderCount(Connection connection) throws SQLException {
        String sql = "SELECT gender, COUNT(*) AS count FROM employees GROUP BY gender ORDER BY count DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                log.info("Genero: {}, Cantidad: {}", resultSet.getString("gender"), resultSet.getInt("count"));
            }
        }
    }

}
