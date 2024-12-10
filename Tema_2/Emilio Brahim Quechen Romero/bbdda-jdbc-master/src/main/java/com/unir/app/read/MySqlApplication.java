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

            getEmployeesNumberByGender(connection);
            getBestPaidEmployeeName(connection);
            getSecondBestPaidEmployeeName(connection);
            employeesHiredPerMonth(connection);

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

    //  1.  Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
    private static void getEmployeesNumberByGender(Connection conection) throws SQLException {
        PreparedStatement statement = conection.prepareStatement("SELECT gender, count(*) AS 'num_empleados' "
                + "FROM employees.employees "
                + "GROUP BY gender "
                + "ORDER BY num_empleados DESC;");
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            log.debug("Total {}: {}",
                    resultSet.getString("gender").equals("M") ? "hombres" : "mujeres",
                    resultSet.getString("num_empleados"));
        }
    }

    //  2.  Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto.
    private static void getBestPaidEmployeeName(Connection conection) throws SQLException {
        PreparedStatement statement = conection.prepareStatement(
                "SELECT employees.first_name, employees.last_name, salaries.salary, departments.dept_name "
                        + "FROM employees.employees "
                        + "JOIN employees.salaries     ON employees.emp_no = salaries.emp_no "
                        + "JOIN employees.dept_emp     ON employees.emp_no = dept_emp.emp_no "
                        + "JOIN employees.departments  ON dept_emp.dept_no = departments.dept_no "
                        + "WHERE departments.dept_no = 'D004' "
                        + "ORDER BY salaries.salary DESC "
                        + "LIMIT 1;");
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            log.debug("Empleado/a mejor pagado en {}: {} {}, cantidad: {}",
                    resultSet.getString("dept_name"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("salary"));
        }
    }


    //  3.  Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto.
    private static void getSecondBestPaidEmployeeName(Connection connection) throws SQLException {

         String query = "SELECT employees.first_name, employees.last_name, salaries.salary, departments.dept_name "
                    +   "FROM employees "
                    +   "JOIN salaries ON employees.emp_no = salaries.emp_no "
                    +   "JOIN dept_emp ON employees.emp_no = dept_emp.emp_no "
                    +   "JOIN departments ON dept_emp.dept_no = departments.dept_no "
                    +   "WHERE departments.dept_no = 'D004' "
                    +   "ORDER BY salaries.salary DESC "
                    +   "LIMIT 1 OFFSET 1;";

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            log.debug("Segundo empleado/a mejor pagado en {}: {} {}, cantidad: {}",
                    resultSet.getString("dept_name"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("salary"));
        }
    }

    //  4.  Mostrar el número de empleados contratados en un mes concreto.
    private static void employeesHiredPerMonth(Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) AS total_contratados "
                    +  "FROM employees "
                    +  "WHERE employees.hire_date >= '1985-05-01' AND employees.hire_date <= '1985-05-31'";

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            log.debug("Total de empleados contratados en mayo de 1985 -> : {}",
                    resultSet.getString("total_contratados"));
        }
    }
}