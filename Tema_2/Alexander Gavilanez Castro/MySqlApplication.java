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
            selectEmployeesMenAndWomen(connection);
            selectEmployeeHighestPaid(connection,"Production");
            selectSecondEmployeeHighestPaid(connection,"Production");
            selectEmployeesHiredInMonth(connection,10);
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

    private static void selectEmployeesMenAndWomen(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT gender,count(*) as total from employees.employees GROUP BY gender ORDER BY total DESC;");
        ResultSet employeesGender = selectEmployees.executeQuery();

        while (employeesGender.next()) {
            String gender = employeesGender.getString("gender");
            int total = employeesGender.getInt("total");
            log.debug("Gender:" + gender +" , Total:" + total);
        }

    }
    private static void selectEmployeeHighestPaid(Connection connection,String department) throws SQLException {
        String query = """
            SELECT e.first_name, e.last_name, MAX(s.salary) AS salary
            FROM employees e
                JOIN dept_emp de ON e.emp_no = de.emp_no
                JOIN departments d ON de.dept_no = d.dept_no
                JOIN salaries s ON e.emp_no = s.emp_no
            WHERE d.dept_name = ?
            GROUP BY e.first_name, e.last_name
            ORDER BY salary DESC
            LIMIT 1;
        """;

        try (PreparedStatement selectEmployee = connection.prepareStatement(query)) {
            selectEmployee.setString(1, department);

            ResultSet result = selectEmployee.executeQuery();

            if (result.next()) {
                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                double salary = result.getDouble("salary");

                log.debug("{} {}, Salario: {}",
                        firstName, lastName, salary);
            } else {
                log.error("No se encontraron empleados en el departamento {}", department);
            }
        } catch (SQLException e) {
            log.error("Error al ejecutar la consulta para el empleado mejor pagado", e);
        }

    }
    private static void selectSecondEmployeeHighestPaid(Connection connection,String department) throws SQLException {
        String query = """
            SELECT e.first_name, e.last_name, MAX(s.salary) AS salary
            FROM employees e
                JOIN dept_emp de ON e.emp_no = de.emp_no
                JOIN departments d ON de.dept_no = d.dept_no
                JOIN salaries s ON e.emp_no = s.emp_no
            WHERE d.dept_name = ?
            GROUP BY e.first_name, e.last_name
            ORDER BY salary DESC
            LIMIT 2
            OFFSET 1;
        """;

        try (PreparedStatement selectEmployee = connection.prepareStatement(query)) {
            selectEmployee.setString(1, department);

            ResultSet result = selectEmployee.executeQuery();

            if (result.next()) {
                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                double salary = result.getDouble("salary");

                log.debug("{} {}, Salario: {}",
                        firstName, lastName, salary);
            } else {
                log.error("No se encontraron empleados en el departamento {}", department);
            }
        } catch (SQLException e) {
            log.error("Error al ejecutar la consulta para el empleado mejor pagado", e);
        }

    }


    private static void selectEmployeesHiredInMonth(Connection connection, int month) throws SQLException {
        String query = """            
            SELECT count(*) as total
            FROM employees
            WHERE MONTH(hire_date) = ?;
        """;

        try (PreparedStatement selectEmployees = connection.prepareStatement(query)) {
            selectEmployees.setInt(1, month);

            ResultSet result = selectEmployees.executeQuery();

            if (result.next()) {
                int totalEmployees = result.getInt("total");
                log.debug("Número de empleados contratados fue : {}",  totalEmployees);
            } else {
                log.debug("No se encontraron empleados contratados en el mes {} ", month);
            }
        } catch (SQLException e) {
            log.error("Error al ejecutar la consulta para empleados contratados", e);
        }
    }



}
