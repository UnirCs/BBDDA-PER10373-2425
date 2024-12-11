package com.unir.app.write;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.unir.config.MySqlConnector;
import com.unir.model.MySqlDepartEmp;
import com.unir.model.MySqlDepartments;
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
public class MySqlApplication {

    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        //Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.warn("Recuerda que el fichero unirEmployees.csv debe estar en la raíz del proyecto, es decir, en la carpeta {}"
                    , System.getProperty("user.dir"));
            log.info("Conexión establecida con la base de datos MySQL");

            //Ejemplo utilizado en clase
            // Leemos los datos del fichero CSV
            //List<MySqlEmployee> employees = readDataEmployees("unirEmployees.csv");
            // Introducimos los datos en la base de datos
            //intakeEmployees(connection, employees);

            //Inserción de nuevos departamentos
            List<MySqlDepartments> departments = readDataDepartments("newDepartments.csv");
            intakeDepartment(connection, departments);

            //Inserción de nuevos empleados
            List<MySqlEmployee> employees = readDataEmployees("newEmployees.csv");
            intakeEmployees(connection, employees);

            //Inserción de nuevos empleados
            List<MySqlDepartEmp> departEmps = readDataDepartEmp("newEmpDepart.csv");
            intakeDepartEmp(connection, departEmps);



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
    private static List<MySqlEmployee> readDataEmployees(String ficheroCSV) {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(ficheroCSV))
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
    private static void intakeEmployees(Connection connection, List<MySqlEmployee> employees) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
        String insertSql = "INSERT INTO employees (emp_no, first_name, last_name, gender, hire_date, birth_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE employees SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? WHERE emp_no = ?";
        int lote = 5;
        int contador = 0;

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
                fillUpdateStatementEmployees(updateStatement, employee);
                updateStatement.addBatch();
            } else {
                fillInsertStatementEmployees(insertStatement, employee);
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
    private static void fillInsertStatementEmployees(PreparedStatement statement, MySqlEmployee employee) throws SQLException {
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
    private static void fillUpdateStatementEmployees(PreparedStatement statement, MySqlEmployee employee) throws SQLException {
        statement.setString(1, employee.getFirstName());
        statement.setString(2, employee.getLastName());
        statement.setString(3, employee.getGender());
        statement.setDate(4, employee.getHireDate());
        statement.setDate(5, employee.getBirthDate());
        statement.setInt(6, employee.getEmployeeId());
    }

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de departamentos.
     *
     * @param ficheroCSV
     * @return
     */
    private static List<MySqlDepartments> readDataDepartments(String ficheroCSV) {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(ficheroCSV))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            // Creamos la lista de empleados y el formato de fecha
            List<MySqlDepartments> departments = new LinkedList<>();

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;

            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {

                // Creamos el empleado y lo añadimos a la lista
                MySqlDepartments department = new MySqlDepartments(
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
     * Introduce los datos de los departamentos en la base de datos.
     * Si ya existe, se actualiza.
     * Si no existe, se inserta.
     *
     * @param connection
     * @param departments
     * @throws SQLException
     */
    private static void intakeDepartment(Connection connection, List<MySqlDepartments> departments) throws SQLException {

        String selectSql = "SELECT count(*) FROM departments WHERE dept_no = ?";
        String insertSql = "INSERT INTO departments (dept_no, dept_name) "
                + "VALUES (?, ?)";
        String updateSql = "UPDATE departments SET dept_name = ? WHERE dept_no = ?";
        int lote = 5;
        int contador = 0;

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        for (MySqlDepartments department : departments) {

            // Comprobamos si el departamento existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, department.getDept_no()); // Código del departamento
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatementDepartments(updateStatement, department);
                updateStatement.addBatch();
            } else {
                fillInsertStatementDepartments(insertStatement, department);
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

        // Hacemos commit y volvemos a activar el autocommit
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT department
     *
     * @param statement
     * @param department
     * @throws SQLException
     */
    private static void fillInsertStatementDepartments(PreparedStatement statement, MySqlDepartments department) throws SQLException {
        statement.setString(1, department.getDept_no());
        statement.setString(2, department.getDept_name());
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE department
     *
     * @param statement
     * @param department
     * @throws SQLException
     */
    private static void fillUpdateStatementDepartments(PreparedStatement statement, MySqlDepartments department) throws SQLException {
        statement.setString(1, department.getDept_name());
        statement.setString(2, department.getDept_no());
    }

    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de empleados-departamentos.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de empleados
     */
    private static List<MySqlDepartEmp> readDataDepartEmp(String ficheroCSV) {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(ficheroCSV))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            // Creamos la lista de empleados y el formato de fecha
            List<MySqlDepartEmp> departEmps = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;

            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {

                // Creamos el departamento/empleado y lo añadimos a la lista
                MySqlDepartEmp departEmp = new MySqlDepartEmp(
                        Integer.parseInt(nextLine[0]),
                        nextLine[1],
                        new Date(format.parse(nextLine[2]).getTime()),
                        new Date(format.parse(nextLine[3]).getTime())
                );
                departEmps.add(departEmp);
            }
            return departEmps;
        } catch (IOException e) {
            log.error("Error al leer el fichero CSV", e);
            throw new RuntimeException(e);
        } catch (CsvValidationException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Introduce los datos de los departamentos-empleados en la base de datos.
     * Si ya existe, se actualiza.
     * Si no existe, se inserta.
     *
     * @param connection
     * @param departEmps
     * @throws SQLException
     */
    private static void intakeDepartEmp(Connection connection, List<MySqlDepartEmp> departEmps) throws SQLException {

        String selectSql = "SELECT count(*) FROM dept_emp WHERE (emp_no = ? AND dept_no = ?)";
        String insertSql = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) "
                + "VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE departments SET from_date = ?, to_date = ? WHERE (emp_no = ? AND dept_no = ?)";
        int lote = 5;
        int contador = 0;

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        for (MySqlDepartEmp departEmp : departEmps) {

            // Comprobamos si el departamento existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, departEmp.getEmp_no()); // Código del departamento
            selectStatement.setString(2, departEmp.getDept_no()); // Código del departamento
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatementDepartEmp(updateStatement, departEmp);
                updateStatement.addBatch();
            } else {
                fillInsertStatementDepartEmp(insertStatement, departEmp);
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

        // Hacemos commit y volvemos a activar el autocommit
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE DepartEmp
     *
     * @param statement
     * @param departEmp
     * @throws SQLException
     */
    private static void fillUpdateStatementDepartEmp(PreparedStatement statement, MySqlDepartEmp departEmp) throws SQLException {
        statement.setInt(3, departEmp.getEmp_no());
        statement.setString(4, departEmp.getDept_no());
        statement.setDate(1, departEmp.getFrom_date());
        statement.setDate(2, departEmp.getTo_date());
    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT DepartEmp
     *
     * @param statement
     * @param departEmp
     * @throws SQLException
     */
    private static void fillInsertStatementDepartEmp(PreparedStatement statement, MySqlDepartEmp departEmp) throws SQLException {
        statement.setInt(1, departEmp.getEmp_no());
        statement.setString(2, departEmp.getDept_no());
        statement.setDate(3, departEmp.getFrom_date());
        statement.setDate(4, departEmp.getTo_date());
    }
}
