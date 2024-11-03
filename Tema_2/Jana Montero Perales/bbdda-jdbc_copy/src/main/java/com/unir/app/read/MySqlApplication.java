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

            log.info("----------------------------------------------------------------------------");
            log.info("Resultados ejercicio 1:");
            selectCountsByGender(connection);

            log.info("----------------------------------------------------------------------------");
            log.info("Resultados ejercicio 2:");
            selectFullNameAndSalaryOfHighestPaidEmployeeInDepartment(connection, "Customer Service");

            log.info("----------------------------------------------------------------------------");
            log.info("Resultados ejercicio 3:");
            selectFullNameAndSalaryOfSecondHighestPaidEmployeeInDepartment(connection, "d001");

            log.info("----------------------------------------------------------------------------");
            log.info("Resultados ejercicio 4:");
            countEmployeesHiredInMonth(connection, 1);
        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Implementacion del ejercicio de MySQL 1.
     * Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
     *
     * @param connection
     * @throws SQLException
     */
    private static void selectCountsByGender(Connection connection) throws SQLException {
        Statement selectCountsByGender = connection.createStatement();
        ResultSet countsByGender = selectCountsByGender.executeQuery("select gender, count(*) as \"cantidad\" " +
                "from employees.employees\n" +
                "group by gender\n" +
                "order by cantidad desc;");

        while (countsByGender.next()) {
            log.debug("Gender: {} Amount: {}", countsByGender.getString("gender"), countsByGender.getString("cantidad"));
        }
    }

    /**
     * Implementacion del ejercicio de MySQL 2.
     * Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
     *
     * @param connection
     * @param departmentName
     * @throws SQLException
     */
    private static void selectFullNameAndSalaryOfHighestPaidEmployeeInDepartment(Connection connection, String departmentName) throws SQLException {
        PreparedStatement selectSecondHighestPaidEmployeeInDepartment = connection.prepareStatement("select e.first_name, e.last_name, s.salary\n" +
                "from employees.employees e\n" +
                "    join employees.salaries s  on e.emp_no = s.emp_no\n" +
                "    join employees.dept_emp de on e.emp_no = de.emp_no\n" +
                "    join employees.departments d on de.dept_no = d.dept_no\n" +
                "where d.dept_name = ?\n" +
                "order by s.salary desc\n" +
                "limit 1;");
        selectSecondHighestPaidEmployeeInDepartment.setString(1, departmentName);
        ResultSet employee = selectSecondHighestPaidEmployeeInDepartment.executeQuery();

        while (employee.next()) {
            log.debug("El empleado mejor pagado en el departamento de {} es {} {} con un salario de {}",
                    departmentName, employee.getString("first_name"),
                    employee.getString("last_name"),
                    employee.getString("salary"));
        }

    }

    /**
     * Implementacion del ejercicio de MySQL 4.
     * Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
     *
     * @param connection
     * @param month
     * @throws SQLException
     */
    private static void countEmployeesHiredInMonth(Connection connection, int month) throws SQLException {
        PreparedStatement countEmployeesHiredInMonth = connection.prepareStatement("select count(*) as 'num_empleados' " +
                "from employees.employees e\n" +
                "where MONTH(e.hire_date) = ?;");
        countEmployeesHiredInMonth.setInt(1, month);
        ResultSet employeeCount = countEmployeesHiredInMonth.executeQuery();

        while (employeeCount.next()) {
            log.debug("Numero de empleados contratados en el mes {}: {}",
                    month, employeeCount.getString("num_empleados"));
        }
    }

    /**
     * Implementacion del ejercicio de MySQL 3.
     * Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
     * Obs: se utiliza el código de departamento para mostrar una alternativa distinta a la utilizada en el ejercicio 2.
     *
     * @param connection
     * @param departmentCode
     * @throws SQLException
     */
    private static void selectFullNameAndSalaryOfSecondHighestPaidEmployeeInDepartment(Connection connection, String departmentCode) throws SQLException {
        PreparedStatement selectHighestPaidEmployeeInDepartment = connection.prepareStatement("select e.first_name, e.last_name, s.salary\n" +
                "from employees.employees e\n" +
                "         join employees.salaries s  on e.emp_no = s.emp_no\n" +
                "         join employees.dept_emp de on e.emp_no = de.emp_no\n" +
                "where de.dept_no = ?\n" +
                "order by s.salary desc\n" +
                "limit 1\n" +
                "offset 1;");
        selectHighestPaidEmployeeInDepartment.setString(1, departmentCode);
        ResultSet employee = selectHighestPaidEmployeeInDepartment.executeQuery();

        while (employee.next()) {
            log.debug("El segundo empleado mejor pagado en el departamento {} es {} {} con un salario de {}",
                    departmentCode, employee.getString("first_name"),
                    employee.getString("last_name"),
                    employee.getString("salary"));
        }
    }

}
