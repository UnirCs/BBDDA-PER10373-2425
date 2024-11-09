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

        // quitar autocommit
        connection.setAutoCommit(false);

        // actualizar tabla departamentos
        addOrUpdateDepartments(connection, "newDepartments.csv");

        // actualizar datos de empleados
        addOrUpdateEmployees(connection, "newEmployeesInDepartments.csv");

        //commit transaccion y restaurar autocommit
        connection.commit();
        connection.setAutoCommit(true);

    }

    /**
     * Lee todos los departamentos de un archivo csv y los anade a o actualiza en BD
     * @param connection
     * @param departmentsFilePath
     * @throws SQLException
     */
    private static void addOrUpdateDepartments(Connection connection, String departmentsFilePath) throws SQLException {
        // crear objectos de departamentos
        List<MySqlDepartment> newDepartments = readDepartments(departmentsFilePath);

        // crear queries de select, insert y update
        String selectSql = "SELECT COUNT(*) FROM departments WHERE dept_no = ?";
        String insertSql = "INSERT INTO departments (dept_no, dept_name) VALUES (?, ?)";
        String updateSql = "UPDATE departments SET dept_name = ? WHERE dept_no = ?";

        // crear registros en BD
        for (MySqlDepartment department : newDepartments) {
            // ejecutamos query de select para ver si el departamento existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, department.getDeptNo());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();

            // preparamos statements de insert o update
            PreparedStatement updateDepartment = connection.prepareStatement(updateSql);
            PreparedStatement insertDepartment = connection.prepareStatement(insertSql);
            if (resultSet.getInt(1) > 0) { // el departamento existe => actualizamos registro existente
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
     * Compone una lista de objetos MySqlDepartment a partir de registros en un archivo csv
     * @param departmentsFilePath
     * @return
     */
    private static List<MySqlDepartment> readDepartments(String departmentsFilePath) {
        try(CSVReader reader = new CSVReaderBuilder(new FileReader(departmentsFilePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                .build()) {
            // inicializamos lista
            List<MySqlDepartment> departments = new LinkedList<>();

            // saltamos cabecera del csv
            reader.skip(1);
            String[] nextLine;
            // iteramos por lineas del csv creando objetos de departamento
            while((nextLine = reader.readNext()) != null) {
                MySqlDepartment departament = new MySqlDepartment(nextLine[0], nextLine[1]);
                departments.add(departament);
            }
            return departments;
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException("Error al leer el archivo " + departmentsFilePath, e);
        }
    }

    /**
     * Lee de 10 en 10 registros de empleados y sus departamentos asignados de un archivo csv
     * y los añade a o actualiza en BD
     * @param connection
     * @param employeesFilePath
     */
    private static void addOrUpdateEmployees(Connection connection, String employeesFilePath) {

        try {
            // leer csv
            CSVReader reader = new CSVReaderBuilder(new FileReader(employeesFilePath))
                    .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                    .build();

            reader.skip(1); // saltamos la cabecera
            String[] nextLine = reader.readNext(); // leemos primera linea de datos
            while (nextLine != null) {

                // crear siguiente batch de objetos de empleados y empDept
                List<EmployeeAndEmpDept> employeeAndEmpDepts = getBatchOfEmployeeAndEmpDepts(reader, nextLine, 10);

                try {

                    // crear/actualizar registros de empleados en BD
                    updateEmployeeTable(connection, employeeAndEmpDepts);

                    // crear/actualizar registros de emp_dept en BD
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
     * Actualiza la tabla dept_emp de BD con los datos de un alista de objetos de empleados y sus departamentos
     * @param connection
     * @param employeeAndEmpDepts
     * @throws SQLException
     */
    private static void updateEmpDeptTable(Connection connection, List<EmployeeAndEmpDept> employeeAndEmpDepts) throws SQLException {
        // crear queries de select, insert y update
        String selectSql = "SELECT COUNT(*) FROM dept_emp WHERE emp_no = ? AND dept_no = ?";
        String insertSql = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE dept_emp SET from_date = ?, to_date = ? WHERE emp_no = ? AND dept_no = ?";

        // crear registros en BD
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
            if (resultSet.getInt(1) > 0) { // el combinado empleado/departamento existe => actualizamos registro existente
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

        // crear registros en BD
        for (EmployeeAndEmpDept employeeAndEmpDept : employeeAndEmpDepts) {
            MySqlEmployee employee = employeeAndEmpDept.employee();
            // ejecutamos query de select para ver si el empleado existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, employee.getEmployeeId());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();

            // preparamos statements de insert o update
            PreparedStatement updateEmployee = connection.prepareStatement(updateSql);
            PreparedStatement insertEmployee = connection.prepareStatement(insertSql);
            if (resultSet.getInt(1) > 0) { // el empleado existe => actualizamos registro existente
                fillUpdateStatement(updateEmployee, employee);
                updateEmployee.addBatch();
            } else {
                fillInsertStatement(insertEmployee, employee);
                insertEmployee.addBatch();
            }

            // ejecutamos batch
            updateEmployee.executeBatch();
            insertEmployee.executeBatch();
        }
    }

    /**
     * Lee lineas de un csv de empleados y empleadoEnDepartamente de 10 en 10
     * Devuelve una lista de objetos con datos del empleado y de la relacion del
     * empleado con departamento
     * @param reader
     * @param batchSize
     * @return List<EmployeeAndEmpDept>
     */
    private static List<EmployeeAndEmpDept> getBatchOfEmployeeAndEmpDepts(CSVReader reader, String[] firstLine, int batchSize) {
        List<EmployeeAndEmpDept> employeeAndEmpDepts = new LinkedList<>();
        String[] nextLine = firstLine;
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
                // si no hemos alcanzado el tamaño del batch, seguimos leyendo del csv
                if (lineCount < batchSize) nextLine = reader.readNext();
            }
            return employeeAndEmpDepts;
        } catch (IOException | CsvValidationException | ParseException e) {
            throw new RuntimeException("Error while reading csv data", e);
        }
    }

    /**
     * Registro que contiene un objeto Employee y su objeto EmployeeDepartment relacionado
     * @param employee
     * @param empDept
     */
    record EmployeeAndEmpDept(MySqlEmployee employee, MySqlEmployeeDepartment empDept) {}


    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT.
     *
     * @param statement - PreparedStatement
     * @param employee - Empleado
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillInsertStatement(PreparedStatement statement, MySqlEmployee employee) throws SQLException {
        statement.setInt(1, employee.getEmployeeId());
        statement.setString(2, employee.getFirstName());
        statement.setString(3, employee.getLastName());
        statement.setString(4, employee.getGender());
        statement.setDate(5, employee.getHireDate());
        statement.setDate(6, employee.getBirthDate());

    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE.
     *
     * @param statement - PreparedStatement
     * @param employee - Empleado
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillUpdateStatement(PreparedStatement statement, MySqlEmployee employee) throws SQLException {
        statement.setString(1, employee.getFirstName());
        statement.setString(2, employee.getLastName());
        statement.setString(3, employee.getGender());
        statement.setDate(4, employee.getHireDate());
        statement.setDate(5, employee.getBirthDate());
        statement.setInt(6, employee.getEmployeeId());
    }
}
