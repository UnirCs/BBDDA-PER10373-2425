package com.unir.app.write;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.unir.config.MySqlConnector;
import com.unir.model.MySQLDeptEmp;
import com.unir.model.MySqlDepartments;
import com.unir.model.MySqlEmployee;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
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
            //List<MySqlEmployee> employees = readEmployee();
            //List<MySqlDepartments> departments = readDepartment();
            //List<MySQLDeptEmp> deptEmps = readDeptEmp();
            readAddEmployee(connection, "nuevosEmpleados.csv");
            readAddDepartment(connection, "nuevosDepartamentos.csv");
            readAddDeptEmp(connection, "empleadosDepartamentos.csv");
            // Introducimos los datos en la base de datos
            //intakeEmployee(connection, employees);
            //intakeDepartment(connection, departments);
            //intakeDeptEmp(connection, deptEmps);

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
    private static List<MySqlEmployee> readEmployee() {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("nuevosEmpleados.csv"))
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
    private static List<MySqlDepartments> readDepartment() {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("nuevosDepartamentos.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            // Creamos la lista de departamentos
            List<MySqlDepartments> departments = new LinkedList<>();

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;

            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {

                // Creamos el departamento y lo añadimos a la lista
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
    private static List<MySQLDeptEmp> readDeptEmp(){
        try(CSVReader reader = new CSVReaderBuilder(
                new FileReader("empleadosDepartamentos.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()){

            List<MySQLDeptEmp> deptEmps = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");

            reader.skip(1);
            String[] nextLine;

            while((nextLine = reader.readNext()) != null){
                MySQLDeptEmp deptEmp = new MySQLDeptEmp(
                        Integer.parseInt(nextLine[0]),
                        nextLine[1],
                        new Date(format.parse(nextLine[2]).getTime()),
                        new Date(format.parse(nextLine[3]).getTime())
                );
                deptEmps.add(deptEmp);
            }
            return deptEmps;
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
    private static void intakeEmployee(Connection connection, List<MySqlEmployee> employees) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
        String insertSql = "INSERT INTO employees (emp_no, first_name, last_name, gender, hire_date, birth_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE employees SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? WHERE emp_no = ?";
        int lote = 5;
        int contador = 0;

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
        PreparedStatement selectStatement = connection.prepareStatement(selectSql);


        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        for (MySqlEmployee employee : employees) {

            // Comprobamos si el empleado existe

            selectStatement.setInt(1, employee.getEmployeeId()); // Código del empleado
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatementEmployee(updateStatement, employee);
                log.info("empleado {} actualizado", employee.getEmployeeId());
                updateStatement.addBatch();
            } else {
                fillInsertStatementEmployee(insertStatement, employee);
                log.info("empleado {} insertado", employee.getEmployeeId());
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

        /**
         * Para probar en modo DEBUG
         * Hasta que no se hace commit, los cambios no se reflejan en la base de datos
         * Es decir, si alguien consulta la base de datos antes de que se ejecute connection.commit(), no verá los cambios
         * Haz la prueba. Modifica el archivo CSV e incluye un nuevo empleado. Copia la ultima linea y cambia el nombre del empleado (pon algo que sea unico). Pon emp_no 99
         * Pon un breakpoint en connection.commit() y ejecuta el programa en modo debug.
         * Abre DataGrip u otro cliente de base de datos y ejecuta la consulta SELECT * FROM employees. Verás que el nuevo empleado no aparece.
         *
         * Descomenta el siguiente codigo para probarlo.
         * Veras que, tras ejecutarse los batch, el empleado con emp_no 99 si existe en esta conexion contra la DB.
         * Sin embargo, si ejecutas la consulta SELECT * FROM employees en DataGrip, no verás a ese empleado aun.
         */
        //PreparedStatement selectStatement = connection.prepareStatement(selectSql);
        //selectStatement.setInt(1, 99); // Código del empleado
        //ResultSet resultSet = selectStatement.executeQuery();
        //resultSet.next(); // Nos movemos a la primera fila
        //int rowCount = resultSet.getInt(1);
        //log.debug("El empleado con emp_no 99 existe en esta conexion contra la DB? {}", rowCount > 0);


        // Hacemos commit y volvemos a activar el autocommit
        connection.commit();
        connection.setAutoCommit(true);
    }
    private static void intakeDepartment(Connection connection, List<MySqlDepartments> departments) throws SQLException {
        String selectSql = "SELECT COUNT(*) FROM departments WHERE dept_no = ?";
        String insertSql = "INSERT INTO departments (dept_no, dept_name) VALUES (?, ?)";
        String updateSql = "UPDATE departments SET dept_name = ? WHERE dept_no = ?";
        int lote = 5;
        int contador = 0;

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        for (MySqlDepartments department : departments) {

            // Comprobamos si el departemento exixte
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, department.getDepartmentId()); // Código del departamento
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatementDepartment(updateStatement, department);
                log.info("departamento {} actualizado", department.getDepartmentId());
                updateStatement.addBatch();
            } else {
                fillInsertStatementDepartment(insertStatement, department);
                log.info("departamento {} insertado", department.getDepartmentId());
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
    private static void intakeDeptEmp(Connection connection, List<MySQLDeptEmp> deptEmps) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM dept_emp WHERE emp_no = ?";
        String insertSql = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE dept_emp SET dept_no = ?, from_date = ?, to_date = ? WHERE emp_no = ?";
        int lote = 5;
        int contador = 0;

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        for (MySQLDeptEmp deptEmp: deptEmps) {

            // Comprobamos si el departemento exixte
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, deptEmp.getEmployeeId()); // Código del departamento
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatementDeptEmp(updateStatement, deptEmp);
                log.info("empleado {} asignado al departamento {} ", deptEmp.getEmployeeId(), deptEmp.getDepartmentId());
                updateStatement.addBatch();
            } else {
                fillInsertStatementDeptEmp(insertStatement, deptEmp);
                log.info("empleado {} actualizado al departamento {} ", deptEmp.getEmployeeId(), deptEmp.getDepartmentId());
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

    /* lee el fichero CSV y añade los empleados a la base de datos sin necesidad de crear una lista */
    private static void readAddEmployee(Connection connection, String nuevosEmpleados) throws SQLException {
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(nuevosEmpleados))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {
            // Creamos el formato de las fechas
            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
            // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
            String selectSql = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
            String insertSql = "INSERT INTO employees (emp_no, first_name, last_name, gender, hire_date, birth_date) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            String updateSql = "UPDATE employees SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? WHERE emp_no = ?";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
            connection.setAutoCommit(false);
            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;
            //variables para ejecutar el batch cada lote
            int lote = 5;
            int contador = 0;
            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {
                // Creamos el departamento y lo añadimos a la lista
                MySqlEmployee employee = new MySqlEmployee(
                        Integer.parseInt(nextLine[0]),
                        nextLine[1],
                        nextLine[2],
                        nextLine[3],
                        new Date(format.parse(nextLine[4]).getTime()),
                        new Date(format.parse(nextLine[5]).getTime())
                );
                // Comprobamos si el departemento exixte
                selectStatement.setInt(1, employee.getEmployeeId()); // Código del departamento
                ResultSet resultSet = selectStatement.executeQuery();
                // Nos movemos a la primera fila
                resultSet.next();
                int rowCount = resultSet.getInt(1);
                // Si existe, actualizamos. Si no, insertamos
                if (rowCount > 0) {
                    fillUpdateStatementEmployee(updateStatement, employee);
                    log.info("empleado {} actualizado", employee.getEmployeeId());
                    updateStatement.addBatch();
                } else {
                    fillInsertStatementEmployee(insertStatement, employee);
                    log.info("empleado {} insertado", employee.getEmployeeId());
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
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private static void readAddDepartment(Connection connection, String nuevosDepartamentos) throws SQLException {
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(nuevosDepartamentos))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {
            // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
            String selectSql = "SELECT COUNT(*) FROM departments WHERE dept_no = ?";
            String insertSql = "INSERT INTO departments (dept_no, dept_name) VALUES (?, ?)";
            String updateSql = "UPDATE departments SET dept_name = ? WHERE dept_no = ?";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
            connection.setAutoCommit(false);
            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;
            //variables para ejecutar el batch cada lote
            int lote = 5;
            int contador = 0;
            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {
                // Creamos el departamento
                MySqlDepartments department = new MySqlDepartments(
                        nextLine[0],
                        nextLine[1]
                );
                // Comprobamos si el departemento exixte
                selectStatement.setString(1, department.getDepartmentId()); // Código del departamento
                ResultSet resultSet = selectStatement.executeQuery();
                // Nos movemos a la primera fila
                resultSet.next();
                int rowCount = resultSet.getInt(1);
                // Si existe, actualizamos. Si no, insertamos
                if (rowCount > 0) {
                    fillUpdateStatementDepartment(updateStatement, department);
                    log.info("departamento {} actualizado", department.getDepartmentId());
                    updateStatement.addBatch();
                } else {
                    fillInsertStatementDepartment(insertStatement, department);
                    log.info("departamento {} insertado", department.getDepartmentId());
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
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
    private static void readAddDeptEmp(Connection connection, String empleadosDepartamentos) throws SQLException {
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(empleadosDepartamentos))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {
            // Creamos el formato de las fechas
            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
            // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
            String selectSql = "SELECT COUNT(*) FROM dept_emp WHERE emp_no = ?";
            String insertSql = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES (?, ?, ?, ?)";
            String updateSql = "UPDATE dept_emp SET dept_no = ?, from_date = ?, to_date = ? WHERE emp_no = ?";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
            connection.setAutoCommit(false);
            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;
            //variables para ejecutar el batch cada lote
            int lote = 5;
            int contador = 0;
            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {
                // Creamos el departamento y lo añadimos a la lista
                MySQLDeptEmp deptEmp = new MySQLDeptEmp(
                        Integer.parseInt(nextLine[0]),
                        nextLine[1],
                        new Date(format.parse(nextLine[2]).getTime()),
                        new Date(format.parse(nextLine[3]).getTime())
                );
                // Comprobamos si el departemento exixte
                selectStatement.setInt(1, deptEmp.getEmployeeId());
                ResultSet resultSet = selectStatement.executeQuery();
                // Nos movemos a la primera fila
                resultSet.next();
                int rowCount = resultSet.getInt(1);
                // Si existe, actualizamos. Si no, insertamos
                if(rowCount > 0) {
                    fillUpdateStatementDeptEmp(updateStatement, deptEmp);
                    log.info("empleado {} actualizado al departamento {} ", deptEmp.getEmployeeId(), deptEmp.getDepartmentId());
                    updateStatement.addBatch();
                } else {
                    fillInsertStatementDeptEmp(insertStatement, deptEmp);
                    log.info("empleado {} asignado al departamento {} ", deptEmp.getEmployeeId(), deptEmp.getDepartmentId());
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
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE.
     *
     * @param statement - PreparedStatement
     * @param employee - Empleado
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillUpdateStatementEmployee(PreparedStatement statement, MySqlEmployee employee) throws SQLException {
        statement.setString(1, employee.getFirstName());
        statement.setString(2, employee.getLastName());
        statement.setString(3, employee.getGender());
        statement.setDate(4, employee.getHireDate());
        statement.setDate(5, employee.getBirthDate());
        statement.setInt(6, employee.getEmployeeId());
    }
    private static void fillUpdateStatementDepartment(PreparedStatement statement, MySqlDepartments department) throws SQLException {
        statement.setString(2, department.getDepartmentId());
        statement.setString(1, department.getDepartmentName());
    }
    private static void fillUpdateStatementDeptEmp(PreparedStatement statement, MySQLDeptEmp deptEmp) throws SQLException {
        statement.setString(1, deptEmp.getDepartmentId());
        statement.setDate(2, deptEmp.getFromDate());
        statement.setDate(3, deptEmp.getToDate());
        statement.setInt(4, deptEmp.getEmployeeId());
    }
    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT.
     *
     * @param statement - PreparedStatement
     * @param employee - Empleado
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillInsertStatementEmployee(PreparedStatement statement, MySqlEmployee employee) throws SQLException {
        statement.setInt(1, employee.getEmployeeId());
        statement.setString(2, employee.getFirstName());
        statement.setString(3, employee.getLastName());
        statement.setString(4, employee.getGender());
        statement.setDate(5, employee.getHireDate());
        statement.setDate(6, employee.getBirthDate());
    }
    private static void fillInsertStatementDepartment(PreparedStatement statement, MySqlDepartments department) throws SQLException {
        statement.setString(1, department.getDepartmentId());
        statement.setString(2, department.getDepartmentName());
    }
    private static void fillInsertStatementDeptEmp(PreparedStatement statement, MySQLDeptEmp deptEmp) throws SQLException {
        statement.setInt(1, deptEmp.getEmployeeId());
        statement.setString(2, deptEmp.getDepartmentId());
        statement.setDate(3, deptEmp.getFromDate());
        statement.setDate(4, deptEmp.getToDate());
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
