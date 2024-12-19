package com.unir.app.write;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.unir.config.MySqlConnector;
import com.unir.model.MySqlEmployee;
import com.unir.model.MySqlDepartment;
import com.unir.model.MySqlEmpDpto;
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

            // Leemos los datos del fichero CSV
            //List<MySqlEmployee> employees = readData();
            List<MySqlEmployee> employees = readDataNewEmployees();
            List<MySqlDepartment> departments = readDataNewDepartment();
            List<MySqlEmpDpto> empDptos = readDataNewEmpDpto();


            // Introducimos los datos en la base de datos
            intakeNewEmployee(connection, employees);
            intakeNewDepartment (connection, departments);
            intakeNewEmpDpto (connection, empDptos);
            insertUnicoRegistro (connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de empleados.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de empleados
     */



    private static List<MySqlEmployee> readDataNewEmployees() {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("employees_new.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            // Creamos la lista de empleados y el formato de fecha
            List<MySqlEmployee> employees = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;

            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {

                // Creamos el empleado y lo añadimos a la lista
                MySqlEmployee employee = new MySqlEmployee(
                        Integer.parseInt(nextLine[0]),
                        nextLine[1],
                        nextLine[2],
                        nextLine[3],
                        new Date(format.parse(nextLine[4]).getTime()),
                        new Date(format.parse(nextLine[5]).getTime())
                );
                employees.add(employee);
            }
            return employees;
        } catch (IOException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        } catch (CsvValidationException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Introduce los datos en la base de datos.
     * Si el empleado ya existe, se actualiza.
     * Si no existe, se inserta.
     *
     * Toma como referencia el campo emp_no para determinar si el empleado existe o no.
     * @param connection - Conexión a la base de datos
     * @param employees - Lista de empleados
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void intakeNewEmployee(Connection connection, List<MySqlEmployee> employees) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
        String insertSql = "INSERT INTO employees (emp_no, first_name, last_name, gender, hire_date, birth_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE employees SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? WHERE emp_no = ?";

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        for (MySqlEmployee employee : employees) {

            // Comprobamos si el empleado existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, employee.getEmployeeId()); // Código del empleado
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatementNewEmployee(updateStatement, employee);
                updateStatement.addBatch();
            } else {
                fillInsertStatementNewEmployee(insertStatement, employee);
                insertStatement.addBatch();
            }
        }

        // Ejecutamos el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        // Hacemos commit y volvemos a activar el autocommit
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT.
     *
     * @param statement - PreparedStatement
     * @param employee - Empleado
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillInsertStatementNewEmployee(PreparedStatement statement, MySqlEmployee employee) throws SQLException {
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
    private static void fillUpdateStatementNewEmployee(PreparedStatement statement, MySqlEmployee employee) throws SQLException {
        statement.setString(1, employee.getFirstName());
        statement.setString(2, employee.getLastName());
        statement.setString(3, employee.getGender());
        statement.setDate(4, employee.getHireDate());
        statement.setDate(5, employee.getBirthDate());
        statement.setInt(6, employee.getEmployeeId());
    }

    /**
     * Devuelve el último id de una columna de una tabla.
     * Util para obtener el siguiente id a insertar.
     *
     * @param connection - Conexión a la base de datos
     * @param table - Nombre de la tabla
     * @param fieldName - Nombre de la columna
     * @return - Último id de la columna
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static int lastId(Connection connection, String table, String fieldName) throws SQLException {
        String selectSql = "SELECT MAX(?) FROM ?";
        PreparedStatement selectStatement = connection.prepareStatement(selectSql);
        selectStatement.setString(1, fieldName);
        selectStatement.setString(2, table);
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next(); // Nos movemos a la primera fila
        return resultSet.getInt(1);
    }

    /**
     * Lectura de departamentos
     */
    private static List<MySqlDepartment> readDataNewDepartment() {

        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("departaments_new.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            // Creamos la lista de departamentos y el formato de fecha
            List<MySqlDepartment> departaments = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;

            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {

                // Creamos el departamento y lo añadimos a la lista
                MySqlDepartment departament = new MySqlDepartment(
                        nextLine[0],
                        nextLine[1]
                );
                departaments.add(departament);
            }
            return departaments;
        } catch (IOException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void intakeNewDepartment(Connection connection, List<MySqlDepartment> departaments) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM employees.departments WHERE dept_no = ?";
        String insertSql = "INSERT INTO employees.departments (dept_no, dept_name) "
                + "VALUES (?, ?)";
        String updateSql = "UPDATE employees.departments SET dept_name = ? WHERE dept_no = ?";

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        for (MySqlDepartment departament : departaments) {

            // Comprobamos si el empleado existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, departament.getDeptId()); // Código del departamento
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatementNewDepartment(updateStatement, departament);
                updateStatement.addBatch();
            } else {
                fillInsertStatementNewDepartment(insertStatement, departament);
                insertStatement.addBatch();
            }

        }

        // Ejecutamos el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        // Hacemos commit y volvemos a activar el autocommit
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT.
     *
     * @param statement - PreparedStatement
     * @param department - Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillInsertStatementNewDepartment(PreparedStatement statement, MySqlDepartment department) throws SQLException {
        statement.setString(1, department.getDeptId());
        statement.setString(2, department.getDeptName());

    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE.
     *
     * @param statement - PreparedStatement
     * @param department - Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillUpdateStatementNewDepartment(PreparedStatement statement, MySqlDepartment department) throws SQLException {
        statement.setString(1, department.getDeptName());
        statement.setString(2, department.getDeptId());
    }

    /**
     * Lectura de Empleados con Departamentos
     */
    private static List<MySqlEmpDpto> readDataNewEmpDpto() {

        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("employee_department_relations.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            List<MySqlEmpDpto> empDptos = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;

            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {

                // Creamos el empleado y lo añadimos a la lista
                MySqlEmpDpto empDpto = new MySqlEmpDpto(
                        Integer.parseInt(nextLine[0]),
                        nextLine[1],
                        new Date(format.parse(nextLine[2]).getTime()),
                        new Date(format.parse(nextLine[3]).getTime())
                );
                empDptos.add(empDpto);
            }
            return empDptos;
        } catch (IOException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        } catch (CsvValidationException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Introduce los datos en la base de datos.
     * INTAKE NEW EMP DPTO
     */
    private static void intakeNewEmpDpto(Connection connection, List<MySqlEmpDpto> empDptos) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM employees.dept_emp WHERE emp_no = ? and dept_no = ?";
        String insertSql = "INSERT INTO employees.dept_emp (emp_no, dept_no, from_date, to_date) "
                + "VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE employees.dept_emp SET from_date = ?, to_date = ? WHERE dept_no = ? and emp_no = ?";

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        for (MySqlEmpDpto empDpto : empDptos) {

            // Comprobamos si el empleado existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, empDpto.getEmployeeId()); // Código del empleado
            selectStatement.setString(2, empDpto.getDptoId()); // Código del departamento
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatementNewEmpDpto(updateStatement, empDpto);
                updateStatement.addBatch();
            } else {
                fillInsertStatementNewEmpDpto(insertStatement, empDpto);
                insertStatement.addBatch();
            }

        }

        // Ejecutamos el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        // Hacemos commit y volvemos a activar el autocommit
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT.
     *
     * INSERTS NEW EMP DPTO
     */
    private static void fillInsertStatementNewEmpDpto(PreparedStatement statement, MySqlEmpDpto empDpto) throws SQLException {
        statement.setInt(1, empDpto.getEmployeeId());
        statement.setString(2, empDpto.getDptoId());
        statement.setDate(3, empDpto.getFromDate());
        statement.setDate(4, empDpto.getToDate());

    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE.
     *
     * UPDATE NEW EMPDPTO
     */
    private static void fillUpdateStatementNewEmpDpto(PreparedStatement statement, MySqlEmpDpto empDpto) throws SQLException {
        statement.setDate(1, empDpto.getFromDate());
        statement.setDate(2, empDpto.getToDate());
        statement.setString(3, empDpto.getDptoId());
        statement.setInt(4, empDpto.getEmployeeId());
    }

    private static void insertUnicoRegistro (Connection connection) throws SQLException {
        // Datos del empleado que vamos a insertar
        int employeeId = 500113;
        String firstName = "Elena";
        String lastName = "Núñez";
        String gender = "F";
        String hireDate = "2016-05-04";
        String birthDate = "1990-02-12";

        // Consulta SQL para insertar un registro
        String query = "INSERT INTO employees.employees (emp_no, first_name, last_name, gender, hire_date, birth_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            // Establecer valores en los parámetros de la consulta
            statement.setInt(1, employeeId);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, gender);
            statement.setString(5, hireDate);
            statement.setString(6, birthDate);

            // Ejecutar la consulta
            boolean rowsInserted = statement.execute();
            if (rowsInserted) {
                System.out.println("El registro fue insertado exitosamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
