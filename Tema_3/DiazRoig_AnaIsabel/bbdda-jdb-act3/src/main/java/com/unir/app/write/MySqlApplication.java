package com.unir.app.write;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.unir.config.MySqlConnector;
import com.unir.model.MySqlDepartment;
import com.unir.model.MySqlDepartmentEmployee;
import com.unir.model.MySqlEmployee;
import lombok.extern.slf4j.Slf4j;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * La version para Oracle seria muy similar a esta, cambiando únicamente el
 * Driver y los datos de sentencias.
 * La tabla de Oracle contiene muchas restricciones y triggers. Por simplicidad,
 * usamos MySQL en este caso.
 */
@Slf4j
public class MySqlApplication {

    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        // Creamos conexion. No es necesario indicar puerto en host si usamos el
        // default, 1521
        // Try-with-resources. Se cierra la conexión automáticamente al salir del bloque
        // try
        try (Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.warn("Recuerda que el fichero unirEmployees.csv debe estar en la raíz del " +
                    "proyecto, es decir, en la carpeta {}", System.getProperty("user.dir"));
            log.info("Conexión establecida con la base de datos MySQL");

            // Leer y procesar departamentos
            List<MySqlDepartment> departments = readDepartments("newDepartments.csv");
            intakeDepartment(connection, departments);
            System.out.println("Departamentos insertados correctamente");

            // Leer y procesar empleados
            List<MySqlEmployee> employees = readEmployees("newEmployees.csv");
            intakeEmployees(connection, employees);
            System.out.println("Empleados insertados correctamente");

            // Leer y procesar relaciones departamento-empleado
            List<MySqlDepartmentEmployee> departmentEmployees = readDepartmentEmployees("newEmployeeDepartment.csv");
            intakeDepartEmp(connection, departmentEmployees);
            System.out.println("Relaciones departamento-empleado insertadas correctamente");

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Lee los empleados de un archivo CSV.
     *
     * @param fileName - Nombre del archivo
     * @return Lista de empleados
     * @throws IOException            - Error al leer el archivo
     * @throws CsvValidationException - Error al validar el archivo
     * @throws ParseException         - Error al parsear la fecha
     */
    private static List<MySqlEmployee> readEmployees(String fileName)
            throws IOException, CsvValidationException, ParseException {

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(fileName))
                .withCSVParser(new CSVParserBuilder().withSeparator(',').build()).build()) {

            List<MySqlEmployee> employees = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");

            reader.skip(1); // Saltar la cabecera
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length != 6) {
                    throw new IllegalArgumentException("El archivo no tiene el formato esperado para empleados");
                }
                MySqlEmployee employee = new MySqlEmployee(
                        Integer.parseInt(nextLine[0]),
                        nextLine[1],
                        nextLine[2],
                        nextLine[3],
                        new Date(format.parse(nextLine[4]).getTime()),
                        new Date(format.parse(nextLine[5]).getTime()));
                employees.add(employee);
            }
            return employees;
        }
    }

    /**
     * Lee los departamentos de un archivo CSV.
     *
     * @param fileName - Nombre del archivo
     * @return Lista de departamentos
     * @throws IOException            - Error al leer el archivo
     * @throws CsvValidationException - Error al validar el archivo
     */
    private static List<MySqlDepartment> readDepartments(String fileName)
            throws IOException, CsvValidationException {

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(fileName))
                .withCSVParser(new CSVParserBuilder().withSeparator(',').build()).build()) {

            List<MySqlDepartment> departments = new LinkedList<>();

            reader.skip(1); // Saltar la cabecera
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                MySqlDepartment department = new MySqlDepartment(
                        nextLine[0],
                        nextLine[1]);
                departments.add(department);
            }
            return departments;
        }
    }

    /**
     * Lee las relaciones departamento-empleado de un archivo CSV.
     *
     * @param fileName - Nombre del archivo
     * @return Lista de relaciones departamento-empleado
     * @throws IOException            - Error al leer el archivo
     * @throws CsvValidationException - Error al validar el archivo
     * @throws ParseException         - Error al parsear la fecha
     */
    private static List<MySqlDepartmentEmployee> readDepartmentEmployees(String fileName)
            throws IOException, CsvValidationException, ParseException, IllegalArgumentException {

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(fileName))
                .withCSVParser(new CSVParserBuilder().withSeparator(',').build()).build()) {

            List<MySqlDepartmentEmployee> departmentEmployees = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");

            reader.skip(1); // Saltar la cabecera
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length != 4) {
                    throw new IllegalArgumentException(
                            "El archivo no tiene el formato esperado para relaciones departamento-empleado");
                }
                MySqlDepartmentEmployee departmentEmployee = new MySqlDepartmentEmployee(
                        Integer.parseInt(nextLine[0]),
                        nextLine[1],
                        new Date(format.parse(nextLine[2]).getTime()),
                        new Date(format.parse(nextLine[3]).getTime()));
                departmentEmployees.add(departmentEmployee);
            }
            return departmentEmployees;
        }
    }

    /**
     * Introduce los datos en la base de datos.
     * Si el empleado ya existe, se actualiza.
     * Si no existe, se inserta.
     * Toma como referencia el campo emp_no para determinar si el empleado existe o
     * no.
     *
     * @param connection - Conexión a la base de datos
     * @param employees  - Lista de empleados
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void intakeEmployees(Connection connection, List<MySqlEmployee> employees)
            throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
        String insertSql = "INSERT INTO employees " +
                "(emp_no, first_name, last_name, gender, hire_date, birth_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE employees " +
                "SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? " +
                "WHERE emp_no = ?";
        int lote = 5;
        int contador = 0;

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al
        // final
        connection.setAutoCommit(false);

        for (MySqlEmployee employee : employees) {

            // Comprobamos si el empleado existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, employee.getEmployeeId()); // Código del empleado
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if (rowCount > 0) {
                fillUpdateStatement(updateStatement, employee);
                updateStatement.addBatch();
            } else {
                fillInsertStatement(insertStatement, employee);
                insertStatement.addBatch();
            }

            // Ejecutamos el batch cada lote de registros
            if (++contador % lote == 0) {
                updateStatement.executeBatch();
                insertStatement.executeBatch();
            }
        }

        // Ejecutamos el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        // Hacemos commit y activamos el autocommit
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Departamentos: Introduce los datos en la base de datos.
     * Si la relación ya existe, se actualiza.
     * Si no existe, se inserta.
     * Toma como referencia el campo emp_no y dept_no para determinar si la relación
     * existe o no.
     *
     * @param connection  - Conexión a la base de datos
     * @param departments - Lista de relaciones departamento-empleado
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void intakeDepartment(Connection connection, List<MySqlDepartment> departments)
            throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM departments WHERE dept_no = ?";
        String insertSql = "INSERT INTO departments " +
                "(dept_no, dept_name) " +
                "VALUES (?, ?)";
        String updateSql = "UPDATE departments " +
                "SET dept_name = ? " +
                "WHERE dept_no = ?";

        int lote = 5;
        int contador = 0;

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al
        // final
        connection.setAutoCommit(false);
        for (MySqlDepartment department : departments) {
            // Comprobamos si el departamento existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, department.getDept_no()); // Código del departamento
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if (rowCount > 0) {
                fillUpdateStatementDepartment(updateStatement, department);
                updateStatement.addBatch();
            } else {
                fillInsertStatementDepartment(insertStatement, department);
                insertStatement.addBatch();
            }

            // Ejecutamos el batch cada lote de registros
            if (++contador % lote == 0) {
                updateStatement.executeBatch();
                insertStatement.executeBatch();
            }
        }
    }

    /**
     * Departamento: Introduce los datos en la base de datos.
     * Si la relación ya existe, se actualiza.
     * Si no existe, se inserta.
     * Toma como referencia el campo emp_no y dept_no para determinar si la relación
     * existe o no.
     *
     * @param connection          - Conexión a la base de datos
     * @param departmentEmployees - Lista de relaciones departamento-empleado
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void intakeDepartEmp(Connection connection, List<MySqlDepartmentEmployee> departmentEmployees)
            throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM dept_emp WHERE emp_no = ? AND dept_no = ?";
        String insertSql = "INSERT INTO dept_emp " +
                "(emp_no, dept_no, from_date, to_date) " +
                "VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE dept_emp " +
                "SET from_date = ?, to_date = ? " +
                "WHERE emp_no = ? AND dept_no = ?";

        int lote = 5;
        int contador = 0;

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al
        // final
        connection.setAutoCommit(false);
        for (MySqlDepartmentEmployee departmentEmployee : departmentEmployees) {
            // Comprobamos si la relación existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, departmentEmployee.getEmp_no()); // Código del empleado
            selectStatement.setString(2, String.valueOf(departmentEmployee.getDept_no())); // Código del departamento
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if (rowCount > 0) {
                fillUpdateStatementDeptEmp(updateStatement, departmentEmployee);
                updateStatement.addBatch();
            } else {
                fillInsertStatementDeptEmp(insertStatement, departmentEmployee);
                insertStatement.addBatch();
            }

            // Ejecutamos el batch cada lote de registros
            if (++contador % lote == 0) {
                updateStatement.executeBatch();
                insertStatement.executeBatch();
            }
        }

        // Ejecutamos el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        // Hacemos commit y activamos el autocommit
        connection.commit();
        connection.setAutoCommit(true);

    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT.
     *
     * @param statement - PreparedStatement
     * @param employee  - Empleado
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
     * Departamento: Rellena los parámetros de un PreparedStatement para una
     * consulta INSERT.
     *
     * @param statement  - PreparedStatement
     * @param department - Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillInsertStatementDepartment(PreparedStatement statement, MySqlDepartment department)
            throws SQLException {
        statement.setString(1, String.valueOf(department.getDept_no()));
        statement.setString(2, department.getDept_name());
    }

    /**
     * Departamento: Rellena los parámetros de un PreparedStatement para una
     * consulta INSERT.
     *
     * @param statement          - PreparedStatement
     * @param departmentEmployee - Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillInsertStatementDeptEmp(PreparedStatement statement,
            MySqlDepartmentEmployee departmentEmployee) throws SQLException {
        statement.setInt(1, departmentEmployee.getEmp_no());
        statement.setString(2, String.valueOf(departmentEmployee.getDept_no()));
        statement.setDate(3, departmentEmployee.getFrom_date());
        statement.setDate(4, departmentEmployee.getTo_date());
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE.
     *
     * @param statement - PreparedStatement
     * @param employee  - Empleado
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

    /**
     * Departamento: Rellena los parámetros de un PreparedStatement para una
     * consulta UPDATE.
     *
     * @param statement  - PreparedStatement
     * @param department - Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillUpdateStatementDepartment(PreparedStatement statement, MySqlDepartment department)
            throws SQLException {
        statement.setString(1, department.getDept_name());
        statement.setString(2, String.valueOf(department.getDept_no()));
    }

    /**
     * Departamento: Rellena los parámetros de un PreparedStatement para una
     * consulta UPDATE.
     *
     * @param statement          - PreparedStatement
     * @param departmentEmployee - Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillUpdateStatementDeptEmp(PreparedStatement statement,
            MySqlDepartmentEmployee departmentEmployee) throws SQLException {
        statement.setDate(1, departmentEmployee.getFrom_date());
        statement.setDate(2, departmentEmployee.getTo_date());
        statement.setInt(3, departmentEmployee.getEmp_no());
        statement.setString(4, String.valueOf(departmentEmployee.getDept_no()));
    }

}
