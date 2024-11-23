package com.unir.app.read;

import com.unir.config.MySqlConnector;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Objects;

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
            selectNumEmployeesByGender(connection);
            firstSalaryByDeparment(connection,"Customer Service");
            secondSalaryByDeparment(connection,"Customer Service");
            hiredEmployeesByMonths(connection,"10");

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
     * Consulta para obtener los empleados por género de la base de datos.
     * @param connection
     * @throws SQLException
     */
    private static void selectNumEmployeesByGender(Connection connection) throws SQLException {
        PreparedStatement countEmployees = connection.prepareStatement("SELECT e.gender, count(1) \n" +
                "as num_employees  FROM employees.employees e group by e.gender \n" +
                "order by num_employees desc;");
        ResultSet result = countEmployees.executeQuery();

        while (result.next()) {
            log.debug(Objects.equals(result.getString("gender"), "M") ?
                    "Número de empleados hombres: " + result.getString("num_employees") :
                            "Número de empleadas mujeres: " + result.getString("num_employees"));
        }
    }

    /**
     * Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento
     * concreto (parámetro variable).
     * No se ha tenido en cuenta si están activo o no el empleado ni si el sueldo es el actual.
     * @param connection
     * @param department
     * @throws SQLException
     */
    private static void firstSalaryByDeparment(Connection connection, String department) throws SQLException {
        PreparedStatement firstSalary = connection.prepareStatement("SELECT e.first_name, e.last_name,\n"+
                "s.salary FROM employees.employees e, employees.departments d, \n"+
                "employees.dept_emp de, employees.salaries s \n"+
                "WHERE de.emp_no = e.emp_no \n"+
                "AND de.dept_no = d.dept_no \n"+
                "AND s.emp_no = e.emp_no \n"+
                "AND d.dept_name = ? \n"+
                "ORDER BY s.salary desc \n"+
                "LIMIT 1");
        firstSalary.setString(1, department);
        ResultSet firstSalaryResult = firstSalary.executeQuery();

        while (firstSalaryResult.next()) {
            log.debug("La persona mejor pagada del departamento {} es: " +
                    "{} {} y cobra {}.",
                    department,
                    firstSalaryResult.getString("first_name"),
                    firstSalaryResult.getString("last_name"),
                    firstSalaryResult.getString("salary"));
        }
    }

    /**
     * Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento
     * concreto (parámetro variable).
     * No se ha tenido en cuenta si están activo o no el empleado ni si el sueldo es el actual.
     * @param connection
     * @param department
     * @throws SQLException
     */
    private static void secondSalaryByDeparment(Connection connection, String department) throws SQLException {
        PreparedStatement secondSalary = connection.prepareStatement("SELECT e.first_name, e.last_name,\n"+
                "s.salary FROM employees.employees e, employees.departments d, \n"+
                "employees.dept_emp de, employees.salaries s \n"+
                "WHERE de.emp_no = e.emp_no \n"+
                "AND de.dept_no = d.dept_no \n"+
                "AND s.emp_no = e.emp_no \n"+
                "AND d.dept_name = ? \n"+
                "ORDER BY s.salary desc \n"+
                "LIMIT 1, 1");
        secondSalary.setString(1, department);
        ResultSet secondSalaryResult = secondSalary.executeQuery();

        while (secondSalaryResult.next()) {
            log.debug("La segunda persona mejor pagada del departamento {} es: " +
                            "{} {} y cobra {}.",
                    department,
                    secondSalaryResult.getString("first_name"),
                    secondSalaryResult.getString("last_name"),
                    secondSalaryResult.getString("salary"));
        }
    }

    /**
     * Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
     * @param connection
     * @param month
     * @throws SQLException
     */
    private static void hiredEmployeesByMonths(Connection connection, String month) throws SQLException {
        PreparedStatement hiredEmployees = connection.prepareStatement("SELECT COUNT(1) as num_employees FROM \n" +
                        "employees.employees e where month(e.hire_date) = ?");
        hiredEmployees.setString(1, month);
        ResultSet hiredEmployeesResult = hiredEmployees.executeQuery();

        while (hiredEmployeesResult.next()) {
            log.debug("Se contrataron un total de {} empleados en el mes {}.",
                    hiredEmployeesResult.getString("num_employees"),
                    month);
        }
    }
}
