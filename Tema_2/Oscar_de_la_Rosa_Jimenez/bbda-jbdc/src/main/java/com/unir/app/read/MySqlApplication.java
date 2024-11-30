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
            countWomenAndMen(connection);
            HighestPaidByDepartment(connection, "d002");
            SecondHighestPaidByDepartment(connection, "d002");
            countEmployeesHired(connection, "5","1985");

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
    private static void countWomenAndMen(Connection connection) throws SQLException {
        Statement selectGender = connection.createStatement();
        ResultSet gender = selectGender.executeQuery ("select employees.gender , count(employees.gender) as 'Total'\n" +
        "from employees.employees\n" +
        "group by employees.employees.gender\n" +
        "order by count(employees.employees.gender) DESC;\n");

        while (gender.next()) {
            log.debug("Género {} {}",
                    gender.getString("gender"),
                    gender.getString("Total"));

        }
    }
    private static void HighestPaidByDepartment(Connection connection, String department) throws SQLException {
        PreparedStatement selectHighestPayed = connection.prepareStatement("SELECT employees.first_name, employees.last_name, salaries.salary \n" +
                "FROM employees.employees INNER JOIN employees.salaries ON employees.emp_no = salaries.emp_no \n" +
                "INNER JOIN employees.dept_emp ON employees.emp_no = dept_emp.emp_no \n" +
                "WHERE dept_emp.dept_no= ? \n" +
                "AND salaries.to_date > DATE(NOW()) \n" +
                "AND salaries.salary = (SELECT MAX(salaries.salary) \n" +
                "FROM employees.salaries \n" +
                "INNER JOIN employees.dept_emp ON salaries.emp_no = dept_emp.emp_no \n" +
                "WHERE dept_emp.dept_no = ? AND salaries.to_date > DATE(NOW())); \n");

        selectHighestPayed.setString(1, department);
        selectHighestPayed.setString(2, department);
        ResultSet employee = selectHighestPayed.executeQuery();

        while (employee.next()) {
            log.debug("El empleado mejor pagado del departamento {} es {} {} con un salario de {}",
                    department,
                    employee.getString("first_name"),
                    employee.getString("last_name"),
                    employee.getString("salary"));
        }

    }
    private static void SecondHighestPaidByDepartment(Connection connection, String department) throws SQLException {
        PreparedStatement selectSecondHighestPayed = connection.prepareStatement("SELECT employees.first_name, employees.last_name, salaries.salary \n"+
                "FROM employees.employees INNER JOIN employees.salaries ON employees.emp_no = salaries.emp_no \n" +
                "INNER JOIN employees.dept_emp ON employees.emp_no = dept_emp.emp_no \n" +
                "WHERE dept_emp.dept_no= ? \n" +
                "AND salaries.to_date > DATE(NOW()) \n" +
                "ORDER BY  salaries.salary DESC \n" +
                "LIMIT 1 OFFSET 1; \n");

        selectSecondHighestPayed.setString(1, department);
        ResultSet employee = selectSecondHighestPayed.executeQuery();

        while (employee.next()) {
            log.debug("El segundo empleado mejor pagado del departamento {} es {} {} con un salario de {}",
                    department,
                    employee.getString("first_name"),
                    employee.getString("last_name"),
                    employee.getString("salary"));
        }
    }
    private static void countEmployeesHired(Connection connection, String month, String year) throws SQLException {
        PreparedStatement countemployees = connection.prepareStatement("SELECT COUNT(*) AS total \n" +
                "FROM employees.employees \n"  +
                "WHERE MONTH(hire_date) = ? AND YEAR(hire_date) = ?; \n");

        countemployees.setString(1, month);
        countemployees.setString(2, year);
        ResultSet employees = countemployees.executeQuery();

        while (employees.next()) {
            log.debug("El número de empleados contratados el mes {} del año {} es de {}",
                    month,
                    year,
                    employees.getString("total"));
        }

    }
}

