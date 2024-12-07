package com.unir.app.write;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.unir.config.MySqlConnector;
import com.unir.model.MySqlDepartment;
import com.unir.model.MySqlEmployee;
import com.unir.model.MySqlEmployeeDepartment;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * La version para Oracle seria muy similar a esta, cambiando únicamente el Driver y los datos de sentencias.
 * La tabla de Oracle contiene muchas restricciones y triggers. Por simplicidad, usamos MySQL en este caso.
 */
@Slf4j
public class MySqlApplication {

    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        //Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.warn("Recuerda que el fichero unirEmployees.csv debe estar en la raíz del proyecto, es decir, en la carpeta {}"
                    , System.getProperty("user.dir"));
            log.info("Conexión establecida con la base de datos MySQL");

            addDepartmentsAndEmployees(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    private static void addDepartmentsAndEmployees(Connection connection) throws SQLException {

        // quitar el autocommit
        connection.setAutoCommit(false);

        // actualizar la tabla de departamentos
        addOrUpdateDepartments(connection, "unirDepartments.csv");

        // actualizar datos de empleados
        addOrUpdateEmployees(connection, "unirEmployeesDepartments.csv");

        //commitear transaccion y restaurar autocommit
        connection.commit();
        connection.setAutoCommit(true);

    }

    /**
     * Lee todos los departamentos de un archivo csv para añdirlos o actualizarlos en BD
     * @param connection
     * @param departmentsFilePath
     * @throws SQLException
     */
    private static void addOrUpdateDepartments(Connection connection, String departmentsFilePath) throws SQLException {
        // crear objetos de departamentos
        List<MySqlDepartment> newDepartments = null;
        try(CSVReader reader = new CSVReaderBuilder(new FileReader(departmentsFilePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                .build()) {

            List<MySqlDepartment> departments = new LinkedList<>();

            reader.skip(1);
            String[] nextLine;

            // iteramos por lineas del csv creando objetos de departamento
            while((nextLine = reader.readNext()) != null) {
                MySqlDepartment department = new MySqlDepartment(nextLine[0], nextLine[1]);
                departments.add(department);
            }
            newDepartments = departments;
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException("Error al leer el archivo " + departmentsFilePath, e);
        }

        // creación de queries de select, insert y update
        String selectSql = "SELECT COUNT(*) FROM departments WHERE dept_no = ?";
        String updateSql = "UPDATE departments SET dept_name = ? WHERE dept_no = ?";
        String insertSql = "INSERT INTO departments (dept_no, dept_name) VALUES (?, ?)";

        // creación de registros en BD
        for (MySqlDepartment department : newDepartments) {
            // comprobamos existencia de departamente con una query
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, department.getDeptNo());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();

            // preparamos statements de insert o update
            PreparedStatement updateDepartment = connection.prepareStatement(updateSql);
            PreparedStatement insertDepartment = connection.prepareStatement(insertSql);
            if (resultSet.getInt(1) > 0) { // si el departamento existe, se actualiza el registro existente
                updateDepartment.setString(1, department.getDeptName());
                updateDepartment.setString(2, department.getDeptNo());
                updateDepartment.addBatch();
            } else {
                insertDepartment.setString(1, department.getDeptNo());
                insertDepartment.setString(2, department.getDeptName());
                insertDepartment.addBatch();
            }

            // ejecutamos batch
            updateDepartment.executeBatch();
            insertDepartment.executeBatch();
        }

    }

    /**
     * Lee de 10 en 10 registros de empleados y sus departamentos asignados de un archivo csv y los añade a o actualiza en BD
     * @param connection
     * @param employeesFilePath
     */
    private static void addOrUpdateEmployees(Connection connection, String employeesFilePath) {

        try {
            // leer csv
            CSVReader reader = new CSVReaderBuilder(new FileReader(employeesFilePath))
                    .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                    .build();

            reader.skip(1);
            String[] nextLine = reader.readNext();
            int batchSize = 10;
            while (nextLine != null) {
                // crear siguiente batch de objetos de empleados y empDept

                List<EmployeeAndEmpDept> employeeAndEmpDepts = new LinkedList<>();
                int lineCount = 0;
                try {
                    while(lineCount < batchSize && nextLine != null) {
                        SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
                        MySqlEmployee employee = new MySqlEmployee(
                                Integer.parseInt(nextLine[0]),
                                nextLine[1],
                                nextLine[2],
                                nextLine[3],
                                new Date(format.parse(nextLine[4]).getTime()),
                                new Date(format.parse(nextLine[5]).getTime())
                        );

                        MySqlEmployeeDepartment empDept = new MySqlEmployeeDepartment(
                                Integer.parseInt(nextLine[0]),
                                nextLine[6],
                                new Date(format.parse(nextLine[7]).getTime()),
                                new Date(format.parse(nextLine[8]).getTime())
                        );

                        employeeAndEmpDepts.add(new EmployeeAndEmpDept(employee, empDept));
                        lineCount++;
                        // si no hemos alcanzado el tamaño del batch, debe seguirse leyendo del csv
                        if (lineCount < batchSize) nextLine = reader.readNext();
                    }
                } catch (IOException | CsvValidationException | ParseException e) {
                    throw new RuntimeException("Error while reading csv data", e);
                }

                try {
                    // crear/actualizar registros de la tabla de empleados en BD
                    updateEmployeeTable(connection, employeeAndEmpDepts);

                    // crear/actualizar registros de la tabla de emp_dept en BD
                    updateEmpDeptTable(connection, employeeAndEmpDepts);

                } catch (SQLException e) {
                    log.error("Error al tratar con la base de datos", e);
                }

                nextLine = reader.readNext();
            }
            //cerrar reader
            reader.close();

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Error al leer el archivo " + employeesFilePath, e);
        }
    }

    /**
     * Actualiza la tabla employees de BD a partir de una lista de objetos de empleados y sus departamentos
     * @param connection
     * @param employeeAndEmpDepts
     * @throws SQLException
     */
    private static void updateEmployeeTable(Connection connection, List<EmployeeAndEmpDept> employeeAndEmpDepts) throws SQLException {
        // crear queries de select, insert y update
        String selectSql = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
        String insertSql = "INSERT INTO employees (emp_no, first_name, last_name, gender, hire_date, birth_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE employees SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? WHERE emp_no = ?";

        for (EmployeeAndEmpDept employeeAndEmpDept : employeeAndEmpDepts) {
            MySqlEmployee employee = employeeAndEmpDept.employee();
            // comprobamos si existe el empleado
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, employee.getEmployeeId());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();

            // preparamos statements de insert o update
            PreparedStatement updateEmployee = connection.prepareStatement(updateSql);
            PreparedStatement insertEmployee = connection.prepareStatement(insertSql);
            if (resultSet.getInt(1) > 0) { // el empleado existe => actualizamos registro existente
                updateEmployee.setString(1, employee.getFirstName());
                updateEmployee.setString(2, employee.getLastName());
                updateEmployee.setString(3, employee.getGender());
                updateEmployee.setDate(4, employee.getHireDate());
                updateEmployee.setDate(5, employee.getBirthDate());
                updateEmployee.setInt(6, employee.getEmployeeId());
                updateEmployee.addBatch();
            } else {
                insertEmployee.setInt(1, employee.getEmployeeId());
                insertEmployee.setString(2, employee.getFirstName());
                insertEmployee.setString(3, employee.getLastName());
                insertEmployee.setString(4, employee.getGender());
                insertEmployee.setDate(5, employee.getHireDate());
                insertEmployee.setDate(6, employee.getBirthDate());
                insertEmployee.addBatch();
            }

            // ejecutamos batch
            updateEmployee.executeBatch();
            insertEmployee.executeBatch();
        }
    }


    /**
     * Actualiza la tabla dept_emp de BD con los datos de una lista de objetos de empleados y sus departamentos
     * @param connection
     * @param employeeAndEmpDepts
     * @throws SQLException
     */
    private static void updateEmpDeptTable(Connection connection, List<EmployeeAndEmpDept> employeeAndEmpDepts) throws SQLException {
        // crear queries de select, insert y update
        String selectSql = "SELECT COUNT(*) FROM dept_emp WHERE emp_no = ? AND dept_no = ?";
        String insertSql = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE dept_emp SET from_date = ?, to_date = ? WHERE emp_no = ? AND dept_no = ?";

        for (EmployeeAndEmpDept employeeAndEmpDept : employeeAndEmpDepts) {
            MySqlEmployeeDepartment deptEmp = employeeAndEmpDept.empDept();
            // ejecutamos query de select para ver si el la combinacion de empleado y departamento existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, deptEmp.getEmpNo());
            selectStatement.setString(2, deptEmp.getDeptNo());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();

            // preparamos statements de insert o update
            PreparedStatement updateDeptEmp = connection.prepareStatement(updateSql);
            PreparedStatement insertDeptEmp = connection.prepareStatement(insertSql);
            if (resultSet.getInt(1) > 0) {
                updateDeptEmp.setDate(1, deptEmp.getFromDate());
                updateDeptEmp.setDate(2, deptEmp.getToDate());
                updateDeptEmp.setInt(3, deptEmp.getEmpNo());
                updateDeptEmp.setString(4, deptEmp.getDeptNo());
                updateDeptEmp.addBatch();
            } else {
                insertDeptEmp.setInt(1, deptEmp.getEmpNo());
                insertDeptEmp.setString(2, deptEmp.getDeptNo());
                insertDeptEmp.setDate(3, deptEmp.getFromDate());
                insertDeptEmp.setDate(4, deptEmp.getToDate());
                insertDeptEmp.addBatch();
            }

            // ejecutamos batch
            updateDeptEmp.executeBatch();
            insertDeptEmp.executeBatch();
        }
    }

    /**
     * Registro que contiene un objeto Employee y su objeto EmployeeDepartment relacionado
     * @param employee
     * @param empDept
     */
    record EmployeeAndEmpDept(MySqlEmployee employee, MySqlEmployeeDepartment empDept) {}
}
