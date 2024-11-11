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

            //selectAllEmployeesOfDepartment(connection, "d001");
            //selectAllEmployeesOfDepartment(connection, "d002");

            /*Uncomment to test the methods below
            countByGender(connection);
            bestPaidEmployee(connection, "d005");
            secondBestPaidEmployee(connection, "d005");
            countHiredEmployeesByMonth(connection, 6);
            */

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
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("SELECT gender,count(*) AS total FROM employees.employees GROUP BY gender ORDER BY total DESC");

        while (employees.next()) {
            log.debug("Gender: {}, Total: {}",
                    employees.getString("gender"),
                    employees.getInt("total"));
        }
    }

    private static void bestPaidEmployee(Connection connection,String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT first_name,last_name,salary,dept_no FROM employees.employees\n" +
                "JOIN employees.salaries ON employees.employees.emp_no = employees.salaries.emp_no\n" +
                "JOIN employees.dept_emp ON employees.employees.emp_no = employees.dept_emp.emp_no\n" +
                "WHERE dept_no = ? ORDER BY salary DESC LIMIT 1;");

        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Employee: {} {}, Salary: {}",
                    employees.getString("first_name"),
                    employees.getString("last_name"),
                    employees.getInt("salary"));
        }
    }

    private static void secondBestPaidEmployee(Connection connection,String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT first_name,last_name,salary,dept_no FROM employees.employees\n" +
                "JOIN employees.salaries ON employees.employees.emp_no = employees.salaries.emp_no\n" +
                "JOIN employees.dept_emp ON employees.employees.emp_no = employees.dept_emp.emp_no\n" +
                "WHERE dept_no = ? ORDER BY salary DESC LIMIT 1 OFFSET 1;");

        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Employee: {} {}, Salary: {}",
                    employees.getString("first_name"),
                    employees.getString("last_name"),
                    employees.getInt("salary"));
        }
    }

    private static void countHiredEmployeesByMonth(Connection connection,int month) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT count(*) AS total FROM employees.employees WHERE MONTH(hire_date) = ?");

        selectEmployees.setInt(1, month);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Total de empleados contratados en el mes {}: {}",
                    month,
                    employees.getInt("total"));
        }

    }
}
