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
        try (Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.info("Conexión establecida con la base de datos MySQL");

            selectAllEmployeesOfDepartment(connection, "d001");
            selectAllEmployeesOfDepartment(connection, "d002");

            // 1.
            selectGenderByDESC(connection);
            // 2.
            selectHighestSalary(connection, "d003");
            // 3.
            selectSecondHighestSalary(connection, "d009");
            // 4.
            selectEmployeesHiredInMonth(connection, 6);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Ejemplo de consulta a la base de datos usando Statement.
     * Statement es la forma más básica de ejecutar consultas a la base de datos.
     * Es la más insegura, ya que no se protege de ataques de inyección SQL.
     * No obstante es útil para sentencias DDL.
     *
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
     *
     * @param connection
     * @throws SQLException
     */
    private static void selectAllEmployeesOfDepartment(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT count(*) AS 'Total'\n" +
                "FROM employees emp\n" +
                "INNER JOIN dept_emp dep_rel on emp.emp_no = dep_rel.emp_no\n" +
                "INNER JOIN departments dep on dep_rel.dept_no = dep.dept_no\n" +
                "WHERE dep_rel.dept_no = ?;\n");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Empleados del departamento {}: {}",
                    department,
                    employees.getString("Total"));
        }
    }

    // 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
    private static void selectGenderByDESC(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT gender, COUNT(*) AS 'cantidad' " +
                "FROM employees.employees " +
                "GROUP BY gender " +
                "ORDER BY cantidad DESC;");

        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Cantidad de {}: {}",
                    employees.getString("gender").equals("M") ? "hombres" : "mujeres",
                    employees.getString("cantidad"));
        }
    }

    // 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
    private static void selectHighestSalary(Connection connection, String deparment) throws SQLException {
        PreparedStatement selectAverageSalary = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary " +
                "FROM employees.employees e " +
                "JOIN employees.salaries s ON e.emp_no = s.emp_no " +
                "JOIN employees.dept_emp d ON e.emp_no = d.emp_no " +
                "WHERE d.dept_no = ? " +
                "ORDER BY s.salary DESC " +
                "LIMIT 1;");
        selectAverageSalary.setString(1, deparment);
        ResultSet result = selectAverageSalary.executeQuery();

        while (result.next()) {
            log.debug("Empleado con mayor salario en el departamento {}: {} {}, Salario: {}",
                    "d001",
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("salary"));
        }
    }

    // # 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
    private static void selectSecondHighestSalary(Connection connection, String department) throws SQLException {
        PreparedStatement selectSecondHighestSalary = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary " +
                "FROM employees.employees e " +
                "JOIN employees.salaries s ON e.emp_no = s.emp_no " +
                "JOIN employees.dept_emp d ON e.emp_no = d.emp_no " +
                "WHERE d.dept_no = ? " +
                "ORDER BY s.salary DESC " +
                "LIMIT 1 OFFSET 1;");
        selectSecondHighestSalary.setString(1, department);
        ResultSet result = selectSecondHighestSalary.executeQuery();

        while (result.next()) {
            log.debug("Segundo empleado con mayor salario en el departamento {}: {} {}, Salario: {}",
                    department,
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("salary"));
        }
    }

    // 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
    private static void selectEmployeesHiredInMonth(Connection connection, int month) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT COUNT(*) AS cantidad " +
                "FROM employees.employees e " +
                "WHERE MONTH(e.hire_date) = ?;");
        selectEmployees.setInt(1, month);
        ResultSet result = selectEmployees.executeQuery();

        while (result.next()) {
            log.debug("Número de empleados contratados en el mes {}: {}",
                    month,
                    result.getInt("cantidad"));
        }
    }
}
