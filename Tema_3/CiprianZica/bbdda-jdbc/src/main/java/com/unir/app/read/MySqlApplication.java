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
            selectCountGenders(connection);
            getHighestPaidEmployee(connection, "Marketing");
            getSecondHighestPaidEmployee(connection, "Marketing");
            getEmployeesHiredInMonth(connection, "1990-01-01", "1990-01-31");


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

    private static void selectCountGenders(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT gender, COUNT(*) AS total\n" +
                "FROM  employees\n" +
                "GROUP BY gender\n" +
                "ORDER BY total DESC;");
        ResultSet genders = selectEmployees.executeQuery();
        while(genders.next()) {
            log.debug(genders.getString("gender"));;
            log.debug(genders.getString("total"));;
        }
    }

    //Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
    private static void getHighestPaidEmployee(Connection connection, String departmentName) throws SQLException {
        String query = "SELECT e.first_name, e.last_name, MAX(s.salary) as 'salary'\n" +
                "FROM employees e\n" +
                "         JOIN dept_emp de on e.emp_no = de.emp_no\n" +
                "         JOIN dept_manager dm on e.emp_no = dm.emp_no\n" +
                "         JOIN departments d on de.dept_no = d.dept_no and d.dept_no = dm.dept_no\n" +
                "         JOIN salaries s on e.emp_no = s.emp_no\n" +
                "WHERE d.dept_name = ?\n" +
                "GROUP BY e.first_name, e.last_name\n" +
                "LIMIT 1;\n";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, departmentName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                log.debug("First Name: " + resultSet.getString("first_name"));
                log.debug("Last Name: " + resultSet.getString("last_name"));
                log.debug("Salary: " + resultSet.getDouble("salary"));
            }
        }
    }

    //Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
    private static void getSecondHighestPaidEmployee(Connection connection, String departmentName) throws SQLException {
        String query = "SELECT e.first_name, e.last_name, MAX(s.salary) as 'salary'\n" +
                "FROM employees e\n" +
                "         JOIN dept_emp de on e.emp_no = de.emp_no\n" +
                "         JOIN dept_manager dm on e.emp_no = dm.emp_no\n" +
                "         JOIN departments d on de.dept_no = d.dept_no and d.dept_no = dm.dept_no\n" +
                "         JOIN salaries s on e.emp_no = s.emp_no\n" +
                "WHERE d.dept_name = ?\n" +
                "GROUP BY e.first_name, e.last_name\n" +
                "LIMIT 1\n"+
                "OFFSET 1;\n";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, departmentName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                log.debug("First Name: " + resultSet.getString("first_name"));
                log.debug("Last Name: " + resultSet.getString("last_name"));
                log.debug("Salary: " + resultSet.getDouble("salary"));
            }
        }
    }

    //Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
    private static void getEmployeesHiredInMonth(Connection connection, String startDate, String endDate) throws SQLException {
        String query = "SELECT COUNT(*) " +
                "FROM employees.employees " +
                "WHERE hire_date BETWEEN ? AND ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, startDate);
            statement.setString(2, endDate);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                log.debug("Number of employees hired: " + resultSet.getInt(1));
            }
        }
    }


}
