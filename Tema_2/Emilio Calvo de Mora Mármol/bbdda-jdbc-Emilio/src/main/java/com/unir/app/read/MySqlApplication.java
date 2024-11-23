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

            log.info("\n\nEjercicio 1: Obtención del número de hombres y mujeres de la base de datos, ordenados descendentemente\n");
            selectAllEmployeesByGenderDesc(connection);

            log.info("\n\nEjercicio 2: Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable)\n");

            selectBestPaidEmployeeByDept(connection, "Marketing");
            selectBestPaidEmployeeByDept(connection, "Research");
            selectBestPaidEmployeeByDept(connection, "Sales");

            log.info("\n\nEjercicio 3: Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable)\n");
            selectSecondBestPaidEmployeeByDept(connection, "Marketing");
            selectSecondBestPaidEmployeeByDept(connection, "Research");
            selectSecondBestPaidEmployeeByDept(connection, "Sales");

            log.info("\n\nEjercicio 4: Mostrar el número de empleados contratados en un mes concreto (parámetro variable)\n");
            selectnumberOfEmployeesHiredMonth(connection, "1");
            selectnumberOfEmployeesHiredMonth(connection, "6");
            selectnumberOfEmployeesHiredMonth(connection, "11");
        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }



    /**
     * Ejercicio 1
     * Obtención del número de hombres y mujeres de la base de datos, ordenados descendentemente.
     * @param connection
     * @throws SQLException
     */
    private static void selectAllEmployeesByGenderDesc(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT emp.gender AS Genero, COUNT(*) AS Numero\n" +
                "FROM employees.employees emp\n" +
                "GROUP BY emp.gender\n" +
                "ORDER BY Numero DESC ;\n");
        ResultSet employeesByGender = selectEmployees.executeQuery();

        while (employeesByGender.next()) {
            log.debug("Empleados con género {}: {}", employeesByGender.getString("Genero"), employeesByGender.getString("Numero"));
        }
    }


    /**
     * Ejercicio 2
     * Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable)
     * @param connection
     * @throws SQLException
     */

    private static void selectBestPaidEmployeeByDept(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT emp.first_name AS Nombre, emp.last_name AS Apellido, sal.salary AS Salario, dept_nam.dept_name as Departamento\n" +
                "FROM employees.employees emp\n" +
                "    JOIN employees.salaries sal ON emp.emp_no = sal.emp_no\n" +
                "    JOIN employees.dept_emp dept ON emp.emp_no = dept.emp_no\n" +
                "    JOIN employees.departments dept_nam ON dept.dept_no=dept_nam.dept_no\n" +
                "WHERE dept_nam.dept_name = ?\n" +
                "ORDER BY sal.salary DESC\n" +
                "LIMIT 1");
        selectEmployees.setString(1, department);
        ResultSet bestPaidEmployee = selectEmployees.executeQuery();

        while (bestPaidEmployee.next()) {
            log.debug("El empleado mejor pagado del departamento de {} se llama {} {} y tiene un salario de {}",
                    department,
                    bestPaidEmployee.getString("Nombre"),
                    bestPaidEmployee.getString("Apellido"),
                    bestPaidEmployee.getString("Salario"));
        }
    }



    /**
     * Ejercicio 3
     * Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable)
     * @param connection
     * @throws SQLException
     */

    private static void selectSecondBestPaidEmployeeByDept(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT emp.first_name AS Nombre, emp.last_name AS Apellido, sal.salary AS Salario, dept_nam.dept_name as Departamento\n" +
                "FROM employees.employees emp\n" +
                "    JOIN employees.salaries sal ON emp.emp_no = sal.emp_no\n" +
                "    JOIN employees.dept_emp dept ON emp.emp_no = dept.emp_no\n" +
                "    JOIN employees.departments dept_nam ON dept.dept_no=dept_nam.dept_no\n" +
                "/*Hay que poner el nombre del departamente entre comillas dobles*/\n" +
                "WHERE dept_nam.dept_name = ?\n" +
                "ORDER BY sal.salary DESC\n" +
                "LIMIT 1\n" +
                "OFFSET 1");
        selectEmployees.setString(1, department);
        ResultSet secondBestPaidEmployee = selectEmployees.executeQuery();

        while (secondBestPaidEmployee.next()) {
            log.debug("El segundo empleado mejor pagado del departamento de {} se llama {} {} y tiene un salario de {}",
                    department,
                    secondBestPaidEmployee.getString("Nombre"),
                    secondBestPaidEmployee.getString("Apellido"),
                    secondBestPaidEmployee.getString("Salario"));
        }
    }


    /**
     * Ejercicio 4
     * Mostrar el número de empleados contratados en un mes concreto (parámetro variable)
     * @param connection
     * @throws SQLException
     */

    private static void selectnumberOfEmployeesHiredMonth(Connection connection, String month) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("SELECT count(*) as NumeroEmpleados\n" +
                "FROM employees.employees emp\n" +
                "WHERE MONTH(emp.hire_date) = ?");
        selectEmployees.setString(1, month);
        ResultSet numberOfEmployeesHiredMonth = selectEmployees.executeQuery();

        while (numberOfEmployeesHiredMonth.next()) {
            log.debug("El número de empleados contratados en el mes {} fue de {}",
                    month,
                    numberOfEmployeesHiredMonth.getString("NumeroEmpleados"));
        }
    }
}
