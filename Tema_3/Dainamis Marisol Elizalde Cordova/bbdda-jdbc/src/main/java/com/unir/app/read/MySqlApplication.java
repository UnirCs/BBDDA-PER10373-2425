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

            //Ejercicio 1
            selectCountGenders(connection);

            //Ejericico 2
            getHighestPaidEmployee(connection, "Marketing");

            //Ejercicio 3
            getSecondHighestPaidEmployee(connection, "Customer Service");

            //Ejercicio 4
            getEmployeesHiredInMonth(connection, 5);

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

    //1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
    private static void selectCountGenders(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT gender, COUNT(*) AS total\n" +
                "FROM    employees\n" +
                "GROUP BY gender\n" +
                "ORDER BY total DESC;");

        ResultSet genders = selectEmployees.executeQuery();

        while (genders.next()) {
            log.debug("Género: {}, Total: {}",
                    genders.getString("gender"),
                    genders.getString("total"));
        }
    }

    //2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
    private static void getHighestPaidEmployee(Connection connection, String nombreDep) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT e.first_name AS nombre, e.last_name AS apellido, s.salary AS salario\n" +
               "FROM employees e\n" +
               "JOIN dept_emp de ON e.emp_no = de.emp_no\n" +
               "JOIN salaries s ON e.emp_no = s.emp_no\n" +
               "JOIN departments d ON de.dept_no = d.dept_no\n" +
               "WHERE d.dept_name = ?" +
               "ORDER BY s.salary DESC\n" +
               "LIMIT 1;");

        statement.setString(1, nombreDep);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            log.debug("nombre: {}, apellido: {}, salario: {}",
                    resultSet.getString("nombre"),
                    resultSet.getString("apellido"),
                    resultSet.getString("salario"));
        }
    }

    //3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
    private static void getSecondHighestPaidEmployee(Connection connection, String nombreDep){
        String query = "SELECT e.first_name AS nombre, e.last_name AS apellido, s.salary AS salario " +
                "FROM employees e " +
                "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                "JOIN salaries s ON e.emp_no = s.emp_no " +
                "JOIN departments d ON de.dept_no = d.dept_no " +
                "WHERE d.dept_name = ? " +
                "ORDER BY s.salary DESC " +
                "LIMIT 1 OFFSET 1";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nombreDep);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                log.debug("nombre: {}, apellido: {}, salario: {}",
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("salario"));
            } else {
                log.debug("No se encontró la segunda persona mejor pagada en el departamento: {}", nombreDep);
            }
        } catch (SQLException e) {
            log.error("Error al ejecutar la consulta para obtener la segunda persona mejor pagada: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Se produjo un error inesperado: {}", e.getMessage());
        }
    }

    //4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
    private static void getEmployeesHiredInMonth(Connection connection, int numMes) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS num_Empleados\n" +
                "FROM employees\n" +
                "WHERE MONTH(hire_date) = ?");

        statement.setInt(1, numMes);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            log.debug("Numero de empleados: {}",
                    resultSet.getString("num_Empleados"));
        }
    }
}
