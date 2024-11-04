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
            selectCountMensWomansDesc(connection);
            selectHighestPaidPersonInDepartment(connection, "Customer Service");
            selectSecondHighestPaidPersonInDepartment(connection, "Customer Service");
            selectEmployeeCountByHireMonth(connection, "2000-01");
        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
     * @param connection
     * @throws SQLException
     */
    private static void selectCountMensWomansDesc(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement(" SELECT gender, COUNT(distinct emp_no) " +
                "AS cantidad FROM employees.employees " +
                "GROUP BY gender " +
                "ORDER BY cantidad DESC;");
       // selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();
        log.debug("Cantidad de hombres y mujeres en la base de datos");
        while (employees.next()) {
            log.debug("{}: {}",
                    employees.getString("gender"),
                    employees.getString("cantidad"));
        }
    }

    /**
     * Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
     * @param connection
     * @throws SQLException
     */
    private static void selectHighestPaidPersonInDepartment(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT em.first_name AS 'Nombre', " +
                "em.last_name AS 'Apellidos', (s.salary) AS 'Salario' \n" +
                "    FROM employees.employees em\n" +
                "    JOIN employees.salaries s ON em.emp_no = s.emp_no\n" +
                "    JOIN employees.dept_emp deptem ON deptem.emp_no = em.emp_no\n" +
                "    JOIN employees.departments dept ON dept.dept_no = deptem.dept_no\n" +
                "    WHERE dept.dept_name = ?\n" +
                "    order by s.salary DESC\n" +
                "    limit 1;");
         selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();
        log.debug("Mejor persona pagada de un departamento");
        if (employees.next()) {
            log.debug("{} {} {} ",
                    employees.getString("Nombre"),
                    employees.getString("Apellidos"),
                    employees.getString("Salario"));
        }else{
            log.debug("No se encontraron empleados para el departamento " + department);
        }
    }

    /**
     * Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
     * @param connection
     * @throws SQLException
     */
    private static void selectSecondHighestPaidPersonInDepartment(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT em.first_name AS 'Nombre', " +
                "em.last_name AS 'Apellidos', s.salary AS 'Salario'\n" +
                " FROM employees.employees em\n" +
                " JOIN employees.salaries s ON em.emp_no = s.emp_no\n" +
                " JOIN employees.dept_emp deptem ON deptem.emp_no = em.emp_no\n" +
                " JOIN employees.departments dept ON dept.dept_no = deptem.dept_no\n" +
                " WHERE dept.dept_name = ?\n" +
                " ORDER BY s.salary DESC\n" +
                " LIMIT 1 OFFSET 1;");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();
        log.debug("Mejor persona pagada de un departamento");
        if (employees.next()) {
            log.debug("{} {} {} ",
                    employees.getString("Nombre"),
                    employees.getString("Apellidos"),
                    employees.getString("Salario"));
        }else{
            log.debug("No se encontraron empleados para el departamento " + department);
        }
    }

    /**
     * Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
     * @param connection
     * @throws SQLException
     */
    private static void selectEmployeeCountByHireMonth(Connection connection, String month) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select count(distinct em.emp_no) AS 'count'" +
                " from employees.employees em\n" +
                "WHERE DATE_FORMAT(em.hire_date, '%Y-%m') = ?;");
        selectEmployees.setString(1, month);
        ResultSet employees = selectEmployees.executeQuery();
        log.debug("Número de personas contratadas en " + month);
        if (employees.next()) {
            log.debug("{}", employees.getString("count"));
        }
    }
}
