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
            getTotalNumberOfEmployeesByGender(connection);
            selectBestPaidEmployeeInDeparment(connection, "Customer Service");
            selectBestPaidEmployeeInDeparment(connection, "Sales");
            selectSecondBestPaidEmployeeInDeparment(connection, "Customer Service");
            selectSecondBestPaidEmployeeInDeparment(connection, "Sales");
            getEmployeesHiredInMonth(connection, 11);
            getEmployeesHiredInMonth(connection, 12);

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
            log.debug("Employee: {} {}.",
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
            log.debug("Empleados del departamento {}: {}.",
                    department,
                    employees.getString("Total"));
        }
    }

    // 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente
    // Se puede hacer con la interfaz Statement, dado que la query o consulta SQL no incluye
    // ningún parámetro y por tanto no hay riesgo de inyección de código
    private static void getTotalNumberOfEmployeesByGender(Connection connection) throws SQLException {
        String query = "SELECT gender, COUNT(*) AS 'gender_count'\n" +
                        "FROM employees\n" +
                        "GROUP BY gender\n" +
                        "ORDER BY gender_count DESC;\n";

        try (Statement statement = connection.createStatement();
             ResultSet statement_res = statement.executeQuery(query)) {

            while (statement_res.next()) {
                log.debug("Género: {}, Cantidad: {}.",
                        statement_res.getString("gender"),
                        statement_res.getInt("gender_count"));
            }
        }
    }

    // 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
    private static void selectBestPaidEmployeeInDeparment(Connection connection, String department) throws SQLException {
        String query = "SELECT e.first_name, e.last_name, s.salary\n" +
                "FROM employees e\n" +
                "JOIN salaries s ON e.emp_no = s.emp_no\n" +
                "JOIN dept_emp de ON e.emp_no = de.emp_no\n" +
                "JOIN departments d ON de.dept_no = d.dept_no\n" +
                "WHERE d.dept_name = ?\n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1;\n";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, department);
        ResultSet statement_res = statement.executeQuery();

        while (statement_res.next()) {
            log.debug("Mejor pagado empleado en el departamento {}: {} {}, con un salario de {} dólares brutos anuales.",
                    department,
                    statement_res.getString("first_name"),
                    statement_res.getString("last_name"),
                    statement_res.getString("salary"));
        }
    }

    // 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
    private static void selectSecondBestPaidEmployeeInDeparment(Connection connection, String department) throws SQLException {
        String query = "SELECT e.first_name, e.last_name, s.salary\n" +
                "FROM employees e\n" +
                "JOIN salaries s ON e.emp_no = s.emp_no\n" +
                "JOIN dept_emp de ON e.emp_no = de.emp_no\n" +
                "JOIN departments d ON de.dept_no = d.dept_no\n" +
                "WHERE d.dept_name = ?\n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1 OFFSET 1;\n";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, department);
        ResultSet statement_res = statement.executeQuery();

        while (statement_res.next()) {
            log.debug("Segundo mejor pagado empleado en el departamento {}: {} {}, con un salario de {} dólares brutos anuales.",
                    department,
                    statement_res.getString("first_name"),
                    statement_res.getString("last_name"),
                    statement_res.getString("salary"));
        }
    }

    // 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
    private static void getEmployeesHiredInMonth(Connection connection, int month) throws SQLException {
        String query = "SELECT COUNT(*) AS total_empleados\n" +
                "FROM employees\n" +
                "WHERE MONTH(hire_date) = ?;\n";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, month);
        ResultSet statement_res = statement.executeQuery();

        while (statement_res.next()) {
            log.debug("Número de empleados contratados en el mes {}: {}.",
                    month,
                    statement_res.getInt("total_empleados"));
        }
    }
}
