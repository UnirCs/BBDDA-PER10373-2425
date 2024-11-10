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


            // Ejercicios TEMA 2
            log.info("Ejercicios TEMA 2");
            selectTheNumberOfMenAndWomenOrderDesc(connection);
            getHighestPaidEmployeeByDepartment(connection, "d001");
            getSecondHighestPaidEmployeeByDepartment(connection, "d001");
            getEmployeeCountByHiringMonth(connection, "October");


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
     */
    private static void selectTheNumberOfMenAndWomenOrderDesc(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select count(*) as cantidad, gender from employees\n"
                + "group by gender\n"
                + "order by cantidad desc;");
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("{}: {}",
                    employees.getString("gender"), employees.getString("cantidad"));
        }
    }

    /*
    * Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
    * */
    private static void getHighestPaidEmployeeByDepartment(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select salaries.emp_no, first_name, last_name, salary from employees join salaries on employees.emp_no = salaries.emp_no\n"
                + "join employees.dept_emp de on employees.emp_no = de.emp_no join departments on de.dept_no = departments.dept_no\n"
                + "where departments.dept_no  = ?"
                + "order by salary desc\n"
                + "LIMIT 1;");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Empleado mejor pago de {} es {} {} cuyo salario es de: {}",
                    department,
                    employees.getString("first_name"),
                    employees.getString("last_name"),
                    employees.getString("salary"));
        }
    }

    /*
     * Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
     * */
    private static void getSecondHighestPaidEmployeeByDepartment(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select salaries.emp_no, first_name, last_name, salary from employees join salaries on employees.emp_no = salaries.emp_no\n"
                + "join employees.dept_emp de on employees.emp_no = de.emp_no join departments on de.dept_no = departments.dept_no\n"
                + "where departments.dept_no  = ?"
                + "order by salary desc\n"
                + "LIMIT 1 OFFSET 1;");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Segundo empleado mejor pago de {} es {} {} cuyo salario es de: {}",
                    department,
                    employees.getString("first_name"),
                    employees.getString("last_name"),
                    employees.getString("salary"));
        }
    }


    /*
     * Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
     * */
    private static void getEmployeeCountByHiringMonth(Connection connection, String month) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select count(*) as cantidad_contratada, DATE_FORMAT(de.from_date, '%M') as mes from employees join employees.dept_emp de on employees.emp_no = de.emp_no\n"
                + "where DATE_FORMAT(de.from_date, '%M') = ?\n"
                + "                                                                    group by mes;");
        selectEmployees.setString(1, month);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Numero de empleados contratados de {} fue {}",
                    month,
                    employees.getString("cantidad_contratada"));
        }
    }
}
