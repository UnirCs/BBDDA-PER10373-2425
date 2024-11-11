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

            showGender(connection);
            employeesHiredByMonth(connection, 5);
            employeeMaxSalaryOfDepartment(connection, "Development");
            employeeSecondMaxSalaryOfDepartment(connection, "Development");


        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /*
    Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente
    */
    private static void showGender(Connection connection) throws SQLException {
        Statement showGender = connection.createStatement();
        ResultSet gender = showGender.executeQuery("Select count(emp_no) as cantidad, gender\n" +
                "from employees.employees\n" +
                "group by gender\n" +
                "order by cantidad desc;");

        log.info("Número de hombres y mujeres en la empresa:");

        while (gender.next()) {
            if( gender.getString("gender")== "M"){
                log.debug("{} hombres",
                        gender.getString("cantidad"));
            }
            else{
                log.debug("{} mujeres",
                        gender.getString("cantidad"));
            }
        }
    }

    /*
    Obtener el número de personas contratadas en un mes concreto
    */
    private static <string> void employeesHiredByMonth(Connection connection, int mes) throws SQLException {
        PreparedStatement selectEmployees = connection.prepareStatement("select count(distinct emp.emp_no) as 'Total'\n" +
                "from employees emp\n" +
                "where month(emp.hire_date) = ?;\n");
        selectEmployees.setInt(1, mes);
        ResultSet employees = selectEmployees.executeQuery();
        String mesEscrito;
        switch (mes) {
            case 1:
                mesEscrito = "Enero";
                break;
            case 2:
                mesEscrito = "Febrero";
                break;
            case 3:
                mesEscrito = "Marzo";
                break;
            case 4:
                mesEscrito = "Abril";
                break;
            case 5:
                mesEscrito = "Mayo";
                break;
            case 6:
                mesEscrito = "Junio";
                break;
            case 7:
                mesEscrito = "Julio";
                break;
            case 8:
                mesEscrito = "Agosto";
                break;
            case 9:
                mesEscrito = "Septiembre";
                break;
            case 10:
                mesEscrito = "Octubre";
                break;
            case 11:
                mesEscrito = "Noviembre";
                break;
            case 12:
                mesEscrito = "Diciembre";
                break;
            default:
                mesEscrito = "Mes no válido";
                break;
        }
        while (employees.next() && mesEscrito != "Mes no válido") {
            log.debug("Empleados contratados en " + mesEscrito + " : {}",
                    employees.getString("Total"));
        }
    }
    /**
     * Ejemplo de consulta a la base de datos usando PreparedStatement.
     * PreparedStatement es la forma más segura de ejecutar consultas a la base de datos.
     * Se protege de ataques de inyección SQL.
     * Es útil para sentencias DML.
     *
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

    /*
    Obtener la persona con mayor salario de un departamento concreto
    */
    private static void employeeMaxSalaryOfDepartment (Connection connection, String department) throws SQLException{
        PreparedStatement selectEmployees = connection.prepareStatement("Select employees.first_name as 'nombre', employees.last_name as 'apellido', salaries.salary as 'salario'\n" +
                "from employees.employees\n" +
                "    inner join employees.salaries on employees.emp_no=salaries.emp_no\n" +
                "    inner join employees.dept_emp on employees.emp_no = dept_emp.emp_no\n" +
                "    inner join employees.departments on departments.dept_no = dept_emp.dept_no\n" +
                "where salaries.salary = (select max(salaries.salary)\n" +
                "                         from employees.salaries\n" +
                "                            inner join employees.dept_emp on salaries.emp_no = dept_emp.emp_no\n" +
                "                            inner join employees.departments on dept_emp.dept_no = departments.dept_no\n" +
                "                         where departments.dept_name like ?)\n" +
                "    and departments.dept_name like ?;\n");
        selectEmployees.setString(1, department);
        selectEmployees.setString(2, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("La persona del departamento " + department+" con mayor salario es {} {} y cobra {}",
                    employees.getString("nombre"),
                    employees.getString("apellido"),
                    employees.getString("salario"));
        }
    }
    /*
    Obtener la persona con segundo mayor salario de un departamento concreto
    */
    private static void employeeSecondMaxSalaryOfDepartment (Connection connection, String department) throws SQLException{
        PreparedStatement selectEmployees = connection.prepareStatement("Select employees.first_name as 'nombre', employees.last_name as 'apellido', salaries.salary as 'salario'\n" +
                "from employees.employees\n" +
                "    inner join employees.salaries on employees.emp_no=salaries.emp_no\n" +
                "    inner join employees.dept_emp on employees.emp_no = dept_emp.emp_no\n" +
                "    inner join employees.departments on departments.dept_no = dept_emp.dept_no\n" +
                "where salaries.salary = (select max(salaries.salary)\n" +
                "                         from employees.salaries\n" +
                "                            inner join employees.dept_emp on salaries.emp_no = dept_emp.emp_no\n" +
                "                            inner join employees.departments on dept_emp.dept_no = departments.dept_no\n" +
                "                         where salaries.salary < (select max(salaries.salary)\n" +
                "                                                  from employees.salaries\n" +
                "                                                  inner join employees.dept_emp on salaries.emp_no = dept_emp.emp_no\n" +
                "                                                  inner join employees.departments on dept_emp.dept_no = departments.dept_no\n" +
                "                                                  where departments.dept_name like ?)\n" +
                "                           and departments.dept_name like ?)\n" +
                "    and departments.dept_name like ?");
        selectEmployees.setString(1, department);
        selectEmployees.setString(2, department);
        selectEmployees.setString(3, department);
        ResultSet employees = selectEmployees.executeQuery();

        while (employees.next()) {
            log.debug("La persona del departamento " + department+" con segundo mayor salario es {} {} y cobra {}",
                    employees.getString("nombre"),
                    employees.getString("apellido"),
                    employees.getString("salario"));
        }
    }
}