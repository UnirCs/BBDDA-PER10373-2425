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

            selectGenderOfEmployees(connection);
            selectEmployeeBestSalary(connection, "marketing");
            selectSecondEmployeeBestSalary(connection, "marketing");
            selectNumberEmployeesByMonth(connection, 2);



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

    private static void selectGenderOfEmployees(Connection connection) throws SQLException{
        PreparedStatement p = connection.prepareStatement("SELECT gender, COUNT(*) AS num_empleados\n" +
                "FROM employees.employees\n" +
                "GROUP BY gender\n" +
                "ORDER BY gender ASC;");
        ResultSet rs = p.executeQuery();

        while(rs.next()){
            log.info("selectGenderOfEmployees: " + rs.getString(1) + " , " + rs.getString(2));
        }
    }

    private static void selectEmployeeBestSalary(Connection connection, String department) throws SQLException{
        PreparedStatement p = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary\n" +
                "FROM employees.employees e\n" +
                "JOIN employees.salaries s ON e.emp_no = s.emp_no\n" +
                "JOIN employees.dept_emp d ON e.emp_no = d.emp_no\n" +
                "JOIN employees.departments ds ON d.dept_no = ds.dept_no\n" +
                "WHERE ds.dept_name = ? \n" +
                "AND s.salary = (\n" +
                "    SELECT MAX(sal.salary)\n" +
                "    FROM employees.salaries sal\n" +
                "    JOIN employees.dept_emp de ON sal.emp_no = de.emp_no\n" +
                "    JOIN employees.departments dep ON de.dept_no = dep.dept_no\n" +
                "    WHERE dep.dept_name = ? \n" +
                ")");
        p.setString(1 ,department);
        p.setString(2 ,department);
        ResultSet rs = p.executeQuery();

        while(rs.next()){
            log.info("selectEmployeesBestSalary, name: " + rs.getString(1) + " ,last name: " + rs.getString(2) + " , salary: " + rs.getInt(3));
        }
    }

    private static void selectSecondEmployeeBestSalary (Connection connection, String department) throws SQLException {
        PreparedStatement p = connection.prepareStatement("SELECT e.first_name, e.last_name, s.salary\n" +
                "FROM employees.employees e\n" +
                "JOIN employees.salaries s ON e.emp_no = s.emp_no\n" +
                "JOIN employees.dept_emp d ON e.emp_no = d.emp_no\n" +
                "JOIN employees.departments ds ON d.dept_no = ds.dept_no\n" +
                "WHERE ds.dept_name = ? \n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1 OFFSET 1;");

        p.setString(1, department);
        ResultSet  rs = p.executeQuery();

        while(rs.next()){
            log.info("selectSecondEmployeeBestSalary, name: " + rs.getString(1) + " ,last name: " + rs.getString(2) + " , salary: " + rs.getInt(3));
        }

    }

    private static void selectNumberEmployeesByMonth(Connection connection, Integer month) throws SQLException {

        PreparedStatement p = connection.prepareStatement("SELECT COUNT(*) AS n_empleados\n" +
                "FROM employees.employees\n" +
                "WHERE MONTH(hire_date) = ? ;");

        p.setInt(1, month);
        ResultSet rs = p.executeQuery();

        while(rs.next()){
            log.info("selectNumberEmployeesByMonth: Number of employees: " + rs.getInt(1));
        }
    }

}
