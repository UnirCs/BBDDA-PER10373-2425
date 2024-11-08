package com.unir.app.read;

import com.unir.config.MySqlConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class MySqlApplication {

    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        // Creamos conexión. No es necesario indicar puerto en host si usamos el default, 1521
        // Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.info("Conexión establecida con la base de datos MySQL");

            selectAllEmployeesOfDepartment(connection, "d001");
            selectAllEmployeesOfDepartment(connection, "d002");

            // Ejercicios 1 al 4
            getTotalNumberOfEmployeesByGender(connection); // Ejercicio 1
            getHighestPaidEmployeeInDepartment(connection, "d001"); // Ejercicio 2
            getSecondHighestPaidEmployeeInDepartment(connection, "d001"); // Ejercicio 3
            getEmployeesHiredInMonth(connection, 5); // Ejercicio 4

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

    // Ejercicio 1: Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
    private static void getTotalNumberOfEmployeesByGender(Connection connection) throws SQLException {
        String query = "SELECT gender, COUNT(emp_no) AS 'total' " +
                       "FROM employees " +
                       "GROUP BY gender " +
                       "ORDER BY total DESC;";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                log.debug("Ejercicio 1 - Género: {}, Cantidad: {}.",
                        resultSet.getString("gender"),
                        resultSet.getInt("total"));
            }
        }
    }

    // Ejercicio 2: Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
    private static void getHighestPaidEmployeeInDepartment(Connection connection, String department) throws SQLException {
        String query = "SELECT e.first_name, e.last_name, s.salary " +
                       "FROM employees e " +
                       "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                       "JOIN salaries s ON e.emp_no = s.emp_no " +
                       "WHERE de.dept_no = ? " +
                       "ORDER BY s.salary DESC " +
                       "LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, department);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.debug("Ejercicio 2 - Empleado mejor pagado en el departamento {}: {} {}, Salario: {}",
                        department,
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getInt("salary"));
            } else {
                log.debug("Ejercicio 2 - No se encontró empleado en el departamento {}", department);
            }
        }
    }

    // Ejercicio 3: Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
    private static void getSecondHighestPaidEmployeeInDepartment(Connection connection, String department) throws SQLException {
        String query = "SELECT e.first_name, e.last_name, s.salary " +
                       "FROM employees e " +
                       "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                       "JOIN salaries s ON e.emp_no = s.emp_no " +
                       "WHERE de.dept_no = ? " +
                       "ORDER BY s.salary DESC " +
                       "LIMIT 1 OFFSET 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, department);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.debug("Ejercicio 3 - Segundo empleado mejor pagado en el departamento {}: {} {}, Salario: {}",
                        department,
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getInt("salary"));
            } else {
                log.debug("Ejercicio 3 - No se encontró un segundo empleado en el departamento {}", department);
            }
        }
    }

    // Ejercicio 4: Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
    private static void getEmployeesHiredInMonth(Connection connection, int hireMonth) throws SQLException {
        String query = "SELECT COUNT(*) AS num_employees " +
                       "FROM employees " +
                       "WHERE MONTH(hire_date) = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, hireMonth);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.debug("Ejercicio 4 - Número de empleados contratados en el mes {}: {}",
                        hireMonth,
                        resultSet.getInt("num_employees"));
            } else {
                log.debug("Ejercicio 4 - No se encontraron empleados contratados en el mes {}", hireMonth);
            }
        }
    }
}
