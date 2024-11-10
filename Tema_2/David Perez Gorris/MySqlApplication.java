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
            //selectAllEmployees(connection);
            selectGenderCount(connection);
            selectFirstBestPaidEmployeeOfDepartmentX(connection, "Development");
            selectSecondBestPaidEmployeeOfDepartmentX(connection, "Development");
            selectAllEmployeesHiredMonthY(connection, "08");

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

    private static void selectAllEmployees(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("select * from employees");

        while (employees.next()) {
            log.debug("Employee: {} {}",
                    employees.getString("first_name"),
                    employees.getString("last_name"));
        }
    }
     */

    /**
     * Ejemplo de consulta a la base de datos usando PreparedStatement.
     * PreparedStatement es la forma más segura de ejecutar consultas a la base de datos.
     * Se protege de ataques de inyección SQL.
     * Es útil para sentencias DML.
     * @param connection
     * @throws SQLException

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
     */

    /**
     * Consulta 1 a la base de datos usando PreparedStatement.
     * Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
     */
    private static void selectGenderCount(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT gender AS 'Gender', count(*) AS 'GenderCount'\n" +
                "FROM employees.employees\n" +
                "GROUP BY gender\n" +
                "ORDER BY gender DESC;\n");
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("\nGender Count: {} {}",
                    employees.getString("Gender"),
                    employees.getString("GenderCount"));
        }
    }

    /**
     * Consulta 2 a la base de datos usando PreparedStatement.
     * Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
     */
    private static void selectFirstBestPaidEmployeeOfDepartmentX(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary\n" +
                "FROM employees.employees e\n" +
                "INNER JOIN employees.dept_emp de ON e.emp_no = de.emp_no\n" +
                "INNER JOIN employees.departments d ON de.dept_no = d.dept_no\n" +
                "INNER JOIN employees.salaries s ON e.emp_no = s.emp_no\n" +
                "WHERE d.dept_name = ?\n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1;\n");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("\n1st Best Paid Employee of '" + department + "' Department:\nName: {} Surname: {} Salary: {}",
                    employees.getString("first_name"),
                    employees.getString("last_name"),
                    employees.getString("salary"));
        }
    }

    /**
     * Consulta 3 a la base de datos usando PreparedStatement.
     * Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
     */
    private static void selectSecondBestPaidEmployeeOfDepartmentX(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary\n" +
                "FROM employees.employees e\n" +
                "INNER JOIN employees.dept_emp de ON e.emp_no = de.emp_no\n" +
                "INNER JOIN employees.departments d ON de.dept_no = d.dept_no\n" +
                "INNER JOIN employees.salaries s ON e.emp_no = s.emp_no\n" +
                "WHERE d.dept_name = ?\n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1 OFFSET 1;\n");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("\n2nd Best Paid Employee of '" + department + "' Department:\nName: {} Surname: {} Salary: {}",
                    employees.getString("first_name"),
                    employees.getString("last_name"),
                    employees.getString("salary"));
        }
    }

    /**
     * Consulta 4 a la base de datos usando PreparedStatement.
     * Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
     */
    private static void selectAllEmployeesHiredMonthY(Connection connection, String month) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT MONTH(e.hire_date) AS 'MesContratacion', COUNT(*) AS 'NumeroEmpleadosContratados'\n" +
                "FROM employees.employees e\n" +
                "WHERE MONTH(e.hire_date) = ?\n" +
                "GROUP BY MesContratacion;\n");
        selectEmployees.setString(1, month);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("\nHired Employees Count Month " + month + ": {}",
                    employees.getString("NumeroEmpleadosContratados"));
        }
    }

}