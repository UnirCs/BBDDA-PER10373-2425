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
            selectGenderCount(connection);
            selectMaxSalary(connection, "d005");
            selectSecondMaxSalary(connection, "d005");
            selectEmployeesHiredPerMonth(connection, 11);

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

    /**
     * Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
     * @param connection
     * @throws SQLException
     */
    private static void selectGenderCount(Connection connection) throws SQLException {

        // Preparamos la consulta SQL
        PreparedStatement selectGenderCount = connection.prepareStatement(
                "SELECT e.gender as 'Generos'," +
                " COUNT(*) as 'Cantidad'" +
                " FROM employees.employees e" +
                " GROUP BY e.gender" +
                " ORDER BY Cantidad DESC;"
        );

        // Ejecutamos la consulta SQL
        ResultSet employees = selectGenderCount.executeQuery();

        // Mostramos los resultados de la consulta SQL
        while (employees.next()) {
            log.debug("Número de empleados {}: {}",
                    employees.getString("Generos").equals("M") ? "hombres" : "mujeres",
                    employees.getString("Cantidad"));
        }
    }

    /**
     * Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
     * @param connection
     * @throws SQLException
     */
    private static void selectMaxSalary(Connection connection, String department) throws SQLException {

        // Preparamos la consulta SQL
        PreparedStatement selectMaxSalary = connection.prepareStatement(
                "SELECT e.first_name as Nombre," +
                        " e.last_name as Apellido," +
                        " s.salary as Salario" +
                        " FROM employees.employees e," +
                        " employees.salaries s," +
                        " employees.dept_emp d" +
                        " WHERE e.emp_no = s.emp_no" +
                        " AND e.emp_no = d.emp_no" +
                        " AND d.dept_no = ?" +
                        " ORDER BY s.salary DESC" +
                        " LIMIT 1;"
        );

        // Fijamos el parametro de la consulta SQL
        selectMaxSalary.setString(1, department);

        // Ejecutamos la consulta SQL
        ResultSet employee = selectMaxSalary.executeQuery();

        // Mostramos los resultados de la consulta SQL
        while (employee.next()) {
            log.debug("Empleado con más salario --> Su nombre es: {} {} y su salario es: {}",
                    employee.getString("Nombre"),
                    employee.getString("Apellido"),
                    employee.getString("Salario"));
        }
    }

    /**
     * Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
     * @param connection
     * @throws SQLException
     */
    private static void selectSecondMaxSalary(Connection connection, String department) throws SQLException {

        // Preparamos la consulta SQL
        PreparedStatement selectSecondMaxSalary = connection.prepareStatement(
                "SELECT e.first_name as Nombre," +
                        " e.last_name as Apellido," +
                        " s.salary as Salario" +
                        " FROM employees.employees e," +
                        " employees.salaries s," +
                        " employees.dept_emp d" +
                        " WHERE e.emp_no = s.emp_no" +
                        " AND e.emp_no = d.emp_no" +
                        " AND d.dept_no = ?" +
                        " ORDER BY s.salary DESC" +
                        " LIMIT 1 OFFSET 1;"
        );

        // Fijamos el parametro de la consulta SQL
        selectSecondMaxSalary.setString(1, department);

        // Ejecutamos la consulta SQL
        ResultSet employee = selectSecondMaxSalary.executeQuery();

        // Mostramos los resultados de la consulta SQL
        while (employee.next()) {
            log.debug("Empleado con segundo mejor salario --> Su nombre es: {} {} y su salario es: {}",
                    employee.getString("Nombre"),
                    employee.getString("Apellido"),
                    employee.getString("Salario"));
        }
    }

    /**
     * Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
     * @param connection
     * @throws SQLException
     */
    private static void selectEmployeesHiredPerMonth(Connection connection, int month) throws SQLException {

        // Preparamos la consulta SQL
        PreparedStatement selectEmployeesHiredPerMonth = connection.prepareStatement(
                "SELECT MONTH(e.hire_date) as Mes," +
                        " COUNT(*) as Cantidad" +
                        " FROM employees.employees e" +
                        " WHERE MONTH(e.hire_date) = ?" +
                        " GROUP BY MONTH(e.hire_date);"
        );

        // Fijamos el parametro de la consulta SQL
        selectEmployeesHiredPerMonth.setInt(1, month);

        // Ejecutamos la consulta SQL
        ResultSet employee = selectEmployeesHiredPerMonth.executeQuery();

        // Mostramos los resultados de la consulta SQL
        while (employee.next()) {
            log.debug("En el mes {} se han contratado {} empleados",
                    employee.getString("Mes"),
                    employee.getString("Cantidad"));
        }
    }
}