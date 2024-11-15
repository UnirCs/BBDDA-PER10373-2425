package com.unir.app.write;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.unir.config.MySqlConnector;
import com.unir.model.MySqlDepartment;
import com.unir.model.MySqlDeptEmp;
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
 * La version para Oracle seria muy similar a esta, cambiando únicamente el Driver y los datos de sentencias.
 * La tabla de Oracle contiene muchas restricciones y triggers. Por simplicidad, usamos MySQL en este caso.
 */
@Slf4j
public class MySqlApplicationDPG {

    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        //Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.warn("Recuerda que los ficheros unirXxxxxxxx.csv deben estar en la raíz del proyecto, es decir, en la carpeta {}", System.getProperty("user.dir"));
            log.info("Conexión establecida con la base de datos MySQL");

            // Leemos los datos de los ficheros CSV
            List<MySqlDepartment> departments = readDepartments("unirDepartmentsNew.csv");
            List<MySqlEmployee> employees = readEmployees("unirEmployeesNew.csv");
            List<MySqlDeptEmp> deptXemps = readDeptEmps("unirNewDeptEmp.csv");

            // Introducimos los datos en la base de datos
            intakeDept(connection, departments);
            intakeEmps(connection, employees);
            intakeDeptEmps(connection, deptXemps);

            // Insercion Simple de Empleado
            int emp_no = lastId(connection, "employees", "emp_no") + 1;
            //int emp_no = 500000;
            MySqlEmployee uniqueEmployee = new MySqlEmployee(emp_no,"David","PerezG","M",Date.valueOf("2024-11-10"),Date.valueOf("1990-05-15"));
            simpleInsertEmployee(connection, uniqueEmployee);

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
    private static List<MySqlDepartment> readDepartments(String fileName) {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(fileName))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            // Creamos la lista de departamentos
            List<MySqlDepartment> departments = new LinkedList<>();

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1); // Saltar la cabecera
            String[] nextLine;

            // Leemos el fichero linea a linea
            while ((nextLine = reader.readNext()) != null) {

                // Creamos el departamento y lo añadimos a la lista
                MySqlDepartment department = new MySqlDepartment(
                        nextLine[0],
                        nextLine[1]
                );
                departments.add(department);
            }
            //System.out.println(departments);
            return departments;

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Error al leer departamentos desde el archivo CSV", e);
        }
    }

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de empleados.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de empleados
     */
    private static List<MySqlEmployee> readEmployees(String fileName) {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(fileName))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            // Creamos la lista de empleados y el formato de fecha
            List<MySqlEmployee> employees = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1); // Saltar la cabecera
            String[] nextLine;

            // Leemos el fichero linea a linea
            while ((nextLine = reader.readNext()) != null) {

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
            //System.out.println(employees);
            return employees;

        } catch (IOException | CsvValidationException | ParseException e) {
            throw new RuntimeException("Error al leer empleados desde el archivo CSV", e);
        }
    }

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de empleados x departamento.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de empleados x departamento
     */
    private static List<MySqlDeptEmp> readDeptEmps(String fileName) {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(fileName))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            // Creamos la lista de empleados por departamento
            List<MySqlDeptEmp> deptEmps = new LinkedList<>();
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1); // Saltar la cabecera
            String[] nextLine;

            // Leemos el fichero linea a linea
            while ((nextLine = reader.readNext()) != null) {

                // Creamos el registro de empleado x departamento y lo añadimos a la lista
                MySqlDeptEmp deptEmp = new MySqlDeptEmp(
                        Integer.parseInt(nextLine[0]),
                        nextLine[1],
                        Date.valueOf(nextLine[2]),
                        Date.valueOf(nextLine[3])
                );
                deptEmps.add(deptEmp);
            }
            //System.out.println(deptEmps);
            return deptEmps;

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Error al leer datos de empleados por departamento desde el archivo CSV", e);
        }
    }

    /**
     * Introduce los datos en la base de datos.
     * Si el departamento ya existe, se actualiza.
     * Si no existe, se inserta.
     *
     * Toma como referencia el campo dept_no para determinar si el empleado existe o no.
     * @param connection - Conexión a la base de datos
     * @param departments - Lista de empleados
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void intakeDept(Connection connection, List<MySqlDepartment> departments) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM departments WHERE dept_no = ?";
        String insertSql = "INSERT INTO departments (dept_no, dept_name) VALUES (?, ?)";
        String updateSql = "UPDATE departments SET dept_name = ? WHERE dept_no = ?";
        int lote = 5;
        int contador = 0;

        // Preparamos las consultas
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para batch
        connection.setAutoCommit(false);

        for (MySqlDepartment department : departments) {
            // Comprobamos si el departamento existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, department.getDeptId());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Movemos al primer registro
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos; si no, insertamos
            if (rowCount > 0) {
                fillUpdateDepartmentStatement(updateStatement, department); // Actualizar departamento
                updateStatement.addBatch();
            } else {
                fillInsertDepartmentStatement(insertStatement, department); // Insertar departamento
                insertStatement.addBatch();
            }

            // Ejecutamos batch cada 5 registros
            if (++contador % lote == 0) {
                updateStatement.executeBatch();
                insertStatement.executeBatch();
            }
        }

        // Ejecutamos el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        // Commit y activamos autocommit de nuevo
        connection.commit();
        connection.setAutoCommit(true);
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
    private static void intakeEmps(Connection connection, List<MySqlEmployee> employees) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
        String insertSql = "INSERT INTO employees (emp_no, first_name, last_name, gender, hire_date, birth_date) " + "VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE employees SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? WHERE emp_no = ?";
        int lote = 5;
        int contador = 0;

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        for (MySqlEmployee employee : employees) {

            // Prueba de desarrollo
            //log.info("Employee:\n", employee);

            // Comprobamos si el empleado existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, employee.getEmployeeId()); // Código del empleado
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateEmployeeStatement(updateStatement, employee);
                updateStatement.addBatch();
            } else {
                fillInsertEmployeeStatement(insertStatement, employee);
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

        // Commit y activamos autocommit de nuevo
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Introduce los datos en la base de datos.
     * Si el registro empleado x departamento ya existe, se actualiza.
     * Si no existe, se inserta.
     *
     * Toma como referencia los campos emp_no y dept_no para determinar si el registro existe o no.
     * @param connection - Conexión a la base de datos
     * @param deptXemps - Lista de empleados
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void intakeDeptEmps(Connection connection, List<MySqlDeptEmp> deptXemps) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM dept_emp WHERE emp_no = ? AND dept_no = ?";
        String insertSql = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE dept_emp SET from_date = ?, to_date = ? WHERE emp_no = ? AND dept_no = ?";
        int lote = 5;
        int contador = 0;

        // Preparamos las consultas
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para batch
        connection.setAutoCommit(false);

        for (MySqlDeptEmp deptEmp : deptXemps) {
            // Comprobamos si la relación entre el empleado y el departamento existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, deptEmp.getEmployeeId());
            selectStatement.setString(2, deptEmp.getDeptId());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Movemos al primer registro
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos; si no, insertamos
            if (rowCount > 0) {
                fillUpdateDeptEmpStatement(updateStatement, deptEmp); // Actualizar relación
                updateStatement.addBatch();
            } else {
                fillInsertDeptEmpStatement(insertStatement, deptEmp); // Insertar relación
                insertStatement.addBatch();
            }

            // Ejecutamos batch cada 5 registros
            if (++contador % lote == 0) {
                updateStatement.executeBatch();
                insertStatement.executeBatch();
            }
        }

        // Ejecutamos el batch final
        insertStatement.executeBatch();
        updateStatement.executeBatch();

        // Commit y activamos autocommit de nuevo
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Introduce los datos de un único empleado en la base de datos.
     * Si el empleado ya existe, se actualiza.
     * Si no existe, se inserta.
     *
     * Toma como referencia el campo emp_no para determinar si el empleado existe o no.
     * @param connection - Conexión a la base de datos
     * @param uniqueEmployee - Empleado a insertar o actualizar
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void simpleInsertEmployee(Connection connection, MySqlEmployee uniqueEmployee) throws SQLException {
        String selectSql = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
        String insertSql = "INSERT INTO employees (emp_no, first_name, last_name, gender, hire_date, birth_date) " + "VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE employees SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? WHERE emp_no = ?";

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        // Preparamos las consultas, en este caso con una unica vez es suficiente
        PreparedStatement selectStatement = connection.prepareStatement(selectSql);
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Comprobamos si el empleado existe por código del empleado 'emp_no'
        selectStatement.setInt(1, uniqueEmployee.getEmployeeId());
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next(); // Nos movemos a la primera fila
        int rowCount = resultSet.getInt(1);

        // Si existe, actualizamos. Si no, insertamos
        if (rowCount > 0) {
            fillUpdateEmployeeStatement(updateStatement, uniqueEmployee);
            int rowsAffected = updateStatement.executeUpdate();
            System.out.println("Filas actualizadas: " + rowsAffected);
            log.info("\n" + updateStatement.toString());
        } else {
            fillInsertEmployeeStatement(insertStatement, uniqueEmployee);
            int rowsAffected = insertStatement.executeUpdate();
            System.out.println("Filas insertadas: " + rowsAffected);
            log.info("\n" + insertStatement.toString());
        }
        // Hacemos commit
        connection.commit();
        //Volvemos a activar el autocommit
        connection.setAutoCommit(true);
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
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT.
     *
     * @param statement - PreparedStatement
     * @param department - Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillInsertDepartmentStatement(PreparedStatement statement, MySqlDepartment department) throws SQLException {
        statement.setString(1, department.getDeptId());
        statement.setString(2, department.getDeptName());
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT.
     *
     * @param statement - PreparedStatement
     * @param deptEmp - Empleado x Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillInsertDeptEmpStatement(PreparedStatement statement, MySqlDeptEmp deptEmp) throws SQLException {
        statement.setInt(1, deptEmp.getEmployeeId());
        statement.setString(2, deptEmp.getDeptId());
        statement.setDate(3, deptEmp.getFromDate());
        statement.setDate(4, deptEmp.getToDate());
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
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE.
     *
     * @param statement - PreparedStatement
     * @param department - Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillUpdateDepartmentStatement(PreparedStatement statement, MySqlDepartment department) throws SQLException {
        // Establecemos el nuevo nombre del departamento
        statement.setString(1, department.getDeptName());
        // Establecemos el dept_no para identificar el departamento a actualizar
        statement.setString(2, department.getDeptId());
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE.
     *
     * @param statement - PreparedStatement
     * @param deptEmp - Empleado x Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillUpdateDeptEmpStatement(PreparedStatement statement, MySqlDeptEmp deptEmp) throws SQLException {
        // Establecemos las nuevas fechas
        statement.setDate(1, deptEmp.getFromDate());
        statement.setDate(2, deptEmp.getToDate());
        // Establecemos el emp_no para identificar al empleado
        statement.setInt(3, deptEmp.getEmployeeId());
        // Establecemos el dept_no para identificar el departamento
        statement.setString(4, deptEmp.getDeptId());
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
        //String selectSql = "SELECT MAX(?) FROM ?";
        String selectSql = "SELECT MAX(" + fieldName + ") FROM " + table;
        PreparedStatement selectStatement = connection.prepareStatement(selectSql);
        //selectStatement.setString(1, fieldName);
        //selectStatement.setString(2, table);
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next(); // Nos movemos a la primera fila
        return resultSet.getInt(1);
    }
}