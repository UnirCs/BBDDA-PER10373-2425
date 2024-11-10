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

            log.info("Consulta 1");
            selectGenderCount(connection);

            log.info("Consulta 2");
            selectBestSalary(connection,"d001");

            log.info("Consulta 3");
            selectSecondBestSalary(connection,"d001");

            log.info("Consulta 4");
            selectHiredOnMonth(connection,5);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Obtener el número de hombres y mujeres de la base de datos.
     * Ordenar de forma descendente.
     * @param connection
     * @throws SQLException
     */
    private static void selectGenderCount(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("Select COUNT(emp_no) as total, gender\n" +
                "    from employees.employees\n" +
                "    group by gender\n" +
                "    order by total desc");

        while (employees.next()) {
            log.debug("Employee: {} {}",
                    employees.getString("gender"),
                    employees.getString("total"));
        }
    }

    /**
     * Mostrar el nombre, apellido y salario de la persona
     * mejor pagada de un departamento concreto (parámetro variable).
     * @param connection
     * @throws SQLException
     */
    private static void selectBestSalary(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployee = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary\n" +
                "FROM employees.employees e\n" +
                "JOIN employees.dept_emp de ON e.emp_no = de.emp_no\n" +
                "JOIN employees.salaries s ON e.emp_no = s.emp_no\n" +
                "WHERE de.dept_no = ?\n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1");
        selectEmployee.setString(1, department);
        ResultSet employee = selectEmployee.executeQuery();

        while (employee.next()) {
            log.debug("Employee of {}: {} {} - Salary: {}",
                    department,
                    employee.getString("first_name"),
                    employee.getString("Last_name"),
                    employee.getString("salary")
            );
        }
    }

    /**
     * Mostrar el nombre, apellido y salario de la segunda persona
     * mejor pagada de un departamento concreto (parámetro variable).
     * @param connection
     * @throws SQLException
     */
    private static void selectSecondBestSalary(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployee = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary\n" +
                "FROM employees.employees e\n" +
                "JOIN employees.dept_emp de ON e.emp_no = de.emp_no\n" +
                "JOIN employees.salaries s ON e.emp_no = s.emp_no\n" +
                "WHERE de.dept_no = ?\n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1,1");
        selectEmployee.setString(1, department);
        ResultSet employee = selectEmployee.executeQuery();

        while (employee.next()) {
            log.debug("Employee of {}: {} {} - Salary: {}",
                    department,
                    employee.getString("first_name"),
                    employee.getString("Last_name"),
                    employee.getString("salary")
            );
        }
    }

    /**
     * Mostrar el número de empleados contratados
     * en un mes concreto (parámetro variable).
     * @param connection
     * @throws SQLException
     */
    private static void selectHiredOnMonth(Connection connection, Integer month) throws SQLException {
        PreparedStatement selectHired = connection.prepareStatement("SELECT COUNT(*) AS num_employees\n" +
                "FROM employees.employees\n" +
                "WHERE MONTH(hire_date)=?");
        selectHired.setInt(1, month);
        ResultSet hired = selectHired.executeQuery();

        while (hired.next()) {
            log.debug("Hired on Month {}: {}",
                    month,
                    hired.getString("num_employees"));
        }
    }
}
