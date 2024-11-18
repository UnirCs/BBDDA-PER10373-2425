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

            //selectAllEmployeesOfDepartment(connection, "d001");
            //selectAllEmployeesOfDepartment(connection, "d002");
            numberEmployeesFM(connection);
            maxSalaryDepartment(connection, "Finance");
            secondSalaryDepartment(connection, "Finance");
            hideEmployeesHireMonth(connection, 1);

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

    /*** ACTIVIDAD TEMA 2 - CONSULTA 1 ***/
    private static void numberEmployeesFM(Connection connection) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select EMP.gender, count(EMP.gender) as numEmpleados\n" +
                "from employees EMP\n" +
                "group by EMP.gender\n" +
                "order by numEmpleados");
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("Número de empleados {}: {}",
                    employees.getString("gender"),
                    employees.getString("numEmpleados"));
        }
    }

    /*** ACTIVIDAD TEMA 2 - CONSULTA 2 ***/
    private static void maxSalaryDepartment(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select EMP.first_name, EMP.last_name, SAL.salary\n" +
                "from employees.employees as EMP\n" +
                "join employees.dept_emp as EMP_DEP on (EMP_DEP.emp_no = EMP.emp_no)\n" +
                "join employees.salaries as SAL on (SAL.emp_no = EMP_DEP.emp_no and SAL.from_date = EMP_DEP.from_date)\n" +
                "join employees.departments as DEP on (DEP.dept_no = EMP_DEP.dept_no)\n" +
                "where DEP.dept_name = ? and EMP_DEP.to_date > CURRENT_DATE\n" +
                "order by SAL.salary DESC");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        if (employees.next()) {
            log.debug("El empleado con mayor sueldo del departamento {} es {} {}: {}",
                    department,
                    employees.getString("first_name"),
                    employees.getString("last_name"),
                    employees.getString("salary"));
        }
    }

    /*** ACTIVIDAD TEMA 2 - CONSULTA 3 ***/
    private static void secondSalaryDepartment(Connection connection, String department) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select EMP.first_name, EMP.last_name, SAL.salary\n" +
                "from employees.employees as EMP\n" +
                "join employees.dept_emp as EMP_DEP on (EMP_DEP.emp_no = EMP.emp_no)\n" +
                "join employees.salaries as SAL on (SAL.emp_no = EMP_DEP.emp_no and SAL.from_date = EMP_DEP.from_date)\n" +
                "join employees.departments as DEP on (DEP.dept_no = EMP_DEP.dept_no)\n" +
                "where DEP.dept_name = ? and EMP_DEP.to_date > CURRENT_DATE\n" +
                "order by SAL.salary DESC\n" +
                "LIMIT 1 OFFSET 1");
        selectEmployees.setString(1, department);
        ResultSet employees = selectEmployees.executeQuery();

        if (employees.next()) {
            log.debug("El empleado con el segundo sueldo del departamento {} es {} {}: {}",
                    department,
                    employees.getString("first_name"),
                    employees.getString("last_name"),
                    employees.getString("salary"));
        }
    }

    /*** ACTIVIDAD TEMA 2 - CONSULTA 4 ***/
    private static void hideEmployeesHireMonth(Connection connection, int mes) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select count(*) as cuenta\n" +
                "from employees as EMP\n" +
                "where MONTH(EMP.hire_date) = ?");
        selectEmployees.setInt(1, mes);
        ResultSet employees = selectEmployees.executeQuery();

        if (employees.next()) {
            log.debug("El numero de empleados contratados en el mes {} es {}",
                    mes,
                    employees.getLong("cuenta"));
        }
    }


}
