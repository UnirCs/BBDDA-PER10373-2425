package com.unir.app.write;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.unir.config.MySqlConnector;
import com.unir.model.MySqlEmployee;
import com.unir.model.MySqlDepartment;
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

        // Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        // Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.warn("Recuerda que el fichero unirEmployees.csv debe estar en la raíz del proyecto, es decir, en la carpeta {}"
                    , System.getProperty("user.dir"));
            log.info("Conexión establecida con la base de datos MySQL");

            // Leemos los datos del fichero CSV de departamentos
            List<MySqlDepartment> departments = readDepartments();

            // Leemos los datos del fichero CSV de empleados
            List<MySqlEmployee> employees = readEmployees();

            // Introducimos los datos en la base de datos
            intake(connection, departments, employees);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de departamentos.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de departamentos
     */
    private static List<MySqlDepartment> readDepartments() {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("bbdda-jdbc/newDepartments.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            // Creamos la lista de departamentos (ID y Nombre))
            List<MySqlDepartment> departments = new LinkedList<>();

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;

            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {

                // Creamos el departamento y lo añadimos a la lista
                MySqlDepartment department = new MySqlDepartment(
                        nextLine[0],
                        nextLine[1]
                );
                departments.add(department);
            }
            return departments;
        } catch (IOException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de empleados.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de empleados
     */
    private static List<MySqlEmployee> readEmployees() {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("bbdda-jdbc/newEmployees.csv"))
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
     * Toma como referencia el campo emp_no para determinar si el empleado existe o no.
     * @param connection - Conexión a la base de datos
     * @param departments - Lista de departamentos
     * @param employees - Lista de empleados
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void intake(Connection connection, List<MySqlDepartment> departments, List<MySqlEmployee> employees) throws SQLException {

        // Consultas de departamentos
        String selectDepartmentSql = "SELECT COUNT(*) FROM departments WHERE dept_no = ?";
        String insertDepartmentSql = "INSERT INTO departments (dept_no, dept_name) VALUES (?, ?)";
        String updateDepartmentSql = "UPDATE departments SET dept_no = ?, dept_name = ?";

        // Consultas de empleados
        String selectEmployeeSql = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
        String insertEmployeeSql = "INSERT INTO employees (emp_no, first_name, last_name, gender, hire_date, birth_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String updateEmployeeSql = "UPDATE employees SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? WHERE emp_no = ?";

        // Contadores "Batch"
        int lote = 5;
        int contador = 0;

        // Preparamos las consultas de departamentos, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertDepartmentStatement = connection.prepareStatement(insertDepartmentSql);
        PreparedStatement updateDepartmentStatement = connection.prepareStatement(updateDepartmentSql);

        // Preparamos las consultas de empleados, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertEmployeeStatement = connection.prepareStatement(insertEmployeeSql);
        PreparedStatement updateEmployeeStatement = connection.prepareStatement(updateEmployeeSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);



        // Metemos, primero, todos los departamentos
        for (MySqlDepartment department : departments) {

            // Comprobamos si el departamento existe
            PreparedStatement selectStatement = connection.prepareStatement(selectDepartmentSql);
            selectStatement.setString(1, department.getDepartmentID()); // Código del departamento
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateDepartmentStatement(updateDepartmentStatement, department);
                updateDepartmentStatement.addBatch();
            } else {
                fillInsertDepartmentStatement(insertDepartmentStatement, department);
                insertDepartmentStatement.addBatch();
            }

            // Ejecutamos el batch cada lote de registros
            if (++contador % lote == 0) {
                updateDepartmentStatement.executeBatch();
                insertDepartmentStatement.executeBatch();
            }
        }

        // Ejecutamos el batch final de departamentos
        insertDepartmentStatement.executeBatch();
        updateDepartmentStatement.executeBatch();



        // Metemos, después de los departamenos, todos los empleados
        for (MySqlEmployee employee : employees) {

            // Comprobamos si el empleado existe
            PreparedStatement selectStatement = connection.prepareStatement(selectEmployeeSql);
            selectStatement.setInt(1, employee.getEmployeeId()); // Código del empleado
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateEmployeeStatement(updateEmployeeStatement, employee);
                updateEmployeeStatement.addBatch();
            } else {
                fillInsertEmployeeStatement(insertEmployeeStatement, employee);
                insertEmployeeStatement.addBatch();
            }

            // Ejecutamos el batch cada lote de registros
            if (++contador % lote == 0) {
                updateEmployeeStatement.executeBatch();
                insertEmployeeStatement.executeBatch();
            }
        }

        // Ejecutamos el batch final de empleados
        insertEmployeeStatement.executeBatch();
        updateEmployeeStatement.executeBatch();

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
    private static void fillInsertDepartmentStatement(PreparedStatement statement, MySqlDepartment department) throws SQLException {
        statement.setString(1, department.getDepartmentID());
        statement.setString(2, department.getDeptName());

    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE.
     *
     * @param statement - PreparedStatement
     * @param department - Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillUpdateDepartmentStatement(PreparedStatement statement, MySqlDepartment department) throws SQLException {
        statement.setString(1, department.getDepartmentID());
        statement.setString(2, department.getDeptName());
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT.
     *
     * @param statement - PreparedStatement
     * @param employee - Empleado
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillInsertEmployeeStatement(PreparedStatement statement, MySqlEmployee employee) throws SQLException {
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
    private static void fillUpdateEmployeeStatement(PreparedStatement statement, MySqlEmployee employee) throws SQLException {
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
}
