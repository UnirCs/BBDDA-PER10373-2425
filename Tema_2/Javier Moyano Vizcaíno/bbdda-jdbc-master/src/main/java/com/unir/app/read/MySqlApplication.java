package com.unir.app.read;

import com.unir.config.MySqlConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class MySqlApplication {

    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        //Creamos conexión. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.info("Conexión establecida con la base de datos MySQL");

            //selectAllEmployeesOfDepartment(connection, "d001");
            //selectAllEmployeesOfDepartment(connection, "d002");
            selectTodosHyM(connection);
            selectMejorPagadoDepartmento(connection, "Marketing");
            selectSegundoMejorPagadoDepartmento(connection, "Marketing");
            selectContratadosMes(connection, "1");


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

    private static void selectTodosHyM(Connection connection) throws SQLException {
        Statement selectEmployees = connection.createStatement();
        ResultSet employees = selectEmployees.executeQuery("select gender, count(*) as n\n" +
                "from employees group by gender order by n desc\n");

        while (employees.next()) {
            log.debug("Género: {} Cantidad: {}", employees.getString("gender"), employees.getString("n"));
        }
    }

    private static void selectMejorPagadoDepartmento(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT format(salaries.salary,0) sueldo, a.first_name, a.last_name, departments.dept_name\n" +
                "FROM employees a join salaries ON a.emp_no=salaries.emp_no\n" +
                "                 join dept_emp b ON a.emp_no = b.emp_no\n" +
                "                 join departments on b.dept_no = departments.dept_no\n" +
                "WHERE departments.dept_name=?\n" +
                "ORDER BY salaries.salary DESC LIMIT 1;");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Empleado que más cobra del departamento de {}: {}, {}. Cobra {} $",
                    department,
                    employees.getString("a.first_name"),
                    employees.getString("a.last_name"),
                    employees.getString("sueldo"));
        }
    }

    private static void selectSegundoMejorPagadoDepartmento(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary as sueldo\n" +
                "FROM employees e\n" +
                "JOIN salaries s ON e.emp_no = s.emp_no\n" +
                "JOIN dept_emp de ON e.emp_no = de.emp_no\n" +
                "JOIN departments d ON de.dept_no = d.dept_no\n" +
                "WHERE d.dept_name = ?\n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1 OFFSET 1;");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("2º Empleado que más cobra del departamento de {}: {}, {}. Cobra {} $",
                    department,
                    employees.getString("e.first_name"),
                    employees.getString("e.last_name"),
                    employees.getString("sueldo"));
        }
    }

    private static void selectContratadosMes(Connection connection, String mes) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT count(*) empleados_mes\n" +
                "from employees\n" +
                "where month(hire_date)=?;");
        selectEmployees.setString(1, mes);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("El mes {}, se contrataron {} empleados.",
                    mes,
                    employees.getString("empleados_mes"));
        }
    }
}
