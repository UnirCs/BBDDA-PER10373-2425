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
            getGenderCount(connection);
            getBestPaidEmployee(connection, "Marketing");
            getSecondBestPaidEmployee(connection, "Marketing");
            getMonthHiredEmployees(connection, "10");



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

    /*
     * Metodo para contar hombres y mujeres en la base de datos
     * Uso de Statement ya que no hay parámetros
     */
    private static void getGenderCount(Connection connection) throws SQLException {
        Statement genderCount = connection.createStatement();
        ResultSet gender = genderCount.executeQuery("SELECT gender, COUNT(*) AS count " +
                                                    "FROM employees.employees " +
                                                    "GROUP BY gender " +
                                                    "ORDER BY count DESC;");
        while(gender.next()){
            log.debug("Genero: {} Total: {}",
                    gender.getString("gender"),
                    gender.getInt("count"));
        }
    }

    /*
     * Consulta para obtener el nombre, apellido y salario de la persona mejor pagada de un departamento concreto.
     * Usa PreparedStatement ya que requiere un parámetro de entrada.
     */
    private static void getBestPaidEmployee(Connection connection, String department) throws SQLException {
        PreparedStatement bestPaidEmployee = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary " +
                "FROM employees.employees AS e " +
                "JOIN employees.dept_emp AS de ON e.emp_no = de.emp_no " +
                "JOIN employees.salaries AS s ON e.emp_no = s.emp_no " +
                "JOIN employees.departments AS d ON de.dept_no = d.dept_no " +
                "WHERE d.dept_name = ? " +
                "ORDER BY s.salary DESC " +
                "LIMIT 1;");
        bestPaidEmployee.setString(1, department);
        ResultSet employee = bestPaidEmployee.executeQuery();

        while (employee.next()) {
            log.debug("Mejor pagado del departamento {}: {} {} con un salario de {}",
                    department,
                    employee.getString("first_name"),
                    employee.getString("last_name"),
                    employee.getString("salary"));
        }
    }

    /*
     * Consulta para obtener el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto.
     * Usa PreparedStatement ya que requiere un parámetro de entrada.
     */
    private static void getSecondBestPaidEmployee(Connection connection, String department) throws SQLException {
        PreparedStatement secondbestPaidEmployee = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary " +
                "FROM employees.employees AS e " +
                "JOIN employees.dept_emp AS de ON e.emp_no = de.emp_no " +
                "JOIN employees.salaries AS s ON e.emp_no = s.emp_no " +
                "JOIN employees.departments AS d ON de.dept_no = d.dept_no " +
                "WHERE d.dept_name = ? " +
                "ORDER BY s.salary DESC " +
                "LIMIT 1 OFFSET 1;");
        secondbestPaidEmployee.setString(1, department);
        ResultSet employee = secondbestPaidEmployee.executeQuery();

        while (employee.next()) {
            log.debug("Segundo mejor pagado del departamento {}: {} {} con un salario de {}",
                    department,
                    employee.getString("first_name"),
                    employee.getString("last_name"),
                    employee.getString("salary"));
        }
    }

    /*
     * Consulta para obtener el total de contratados en un mes concreto.
     * Usa PreparedStatement ya que requiere un parámetro de entrada.
     */
    private static void getMonthHiredEmployees(Connection connection, String month) throws SQLException {
        PreparedStatement monthHiredEmployees = connection.prepareStatement("SELECT COUNT(*) AS count_employees " +
                                                                            "FROM employees.employees AS e " +
                                                                            "WHERE MONTH(e.hire_date) = ?;");
        monthHiredEmployees.setInt(1, Integer.parseInt(month)); //convertir mes a un entero
        ResultSet employees = monthHiredEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Mes: {} Total contratados: {}",
                    month,
                    employees.getInt("count_employees"));
        }
    }
}
