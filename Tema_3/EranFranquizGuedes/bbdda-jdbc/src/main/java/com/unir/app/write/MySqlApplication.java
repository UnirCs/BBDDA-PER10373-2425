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
public class MySqlApplication {

    private static final String DATABASE = "employees";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");

    public static void main(String[] args) {

        //Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.warn("Recuerda que el fichero unirEmployees.csv debe estar en la raíz del proyecto, es decir, en la carpeta {}"
                    , System.getProperty("user.dir"));
            log.info("Conexión establecida con la base de datos MySQL");

        //    MySqlEmployee employee = createEmployee();
        //    upsert(connection, employee);


            // Leemos los datos del fichero CSV de departamenrtos
            List<MySqlDepartment> departments = readDataDepartments();

            // Leemos los datos del fichero CSV de emplados
            List<MySqlEmployee> employees = readDataEmployees();

            List<MySqlDeptEmp> listdeptEmpt = createListDeptEmpt(employees);

            intakeDepartmentsEmployees(connection, departments, employees, listdeptEmpt);

            // Introducimos los datos en la base de datos
           // intake(connection, employees);

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
    private static List<MySqlDeptEmp> createListDeptEmpt(List<MySqlEmployee> employees) {
        List<MySqlDeptEmp> deptEmps = new LinkedList<>();
        for (MySqlEmployee e : employees) {
            try {
                Date fromDate = new Date(dateFormat.parse("1996-01-01").getTime());
                Date toDate = new Date(dateFormat.parse("2024-11-09").getTime());
                deptEmps.add(new MySqlDeptEmp(e.getEmployeeId(), "d015", fromDate, toDate));
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }
        return deptEmps;
    }


    /**
     * Rellena los parámetros de un PreparedStatement para una consulta INSERT.
     *
     * @param statement - PreparedStatement
     * @param de - Empleado
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillInsertStatementDeptEmp(PreparedStatement statement, MySqlDeptEmp de) throws SQLException {
        statement.setInt(1, de.getEmployeeId());
        statement.setString(2, de.getDepartmentId());
        statement.setDate(3, de.getFromDate());
        statement.setDate(4, de.getToDate());

    }

    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE.
     *
     * @param statement - PreparedStatement
     * @param de - Empleado
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillUpdateStatementDeptEmp(PreparedStatement statement, MySqlDeptEmp de) throws SQLException {
        statement.setInt(1, de.getEmployeeId());
        statement.setString(2, de.getDepartmentId());
        statement.setDate(3, de.getFromDate());
        statement.setDate(4, de.getToDate());
    }



    /**
     * Introduce los datos en la base de datos.
     * Si ya existe, se actualiza.
     * Si no, se inserta.
     *
     * Toma como referencia el campo emp_no para determinar si existe o no.
     * @param connection - Conexión a la base de datos
     * @param departments - Lista de departamentos
     * @throws SQLException - Error al ejecutar la consulta
     */
    private static void intakeDepartmentsEmployees(Connection connection, List<MySqlDepartment> departments,
                                                   List<MySqlEmployee> employees,  List<MySqlDeptEmp> listdeptEmpt) throws SQLException {

        String selectSqlDepartament = "SELECT COUNT(*) FROM departments WHERE dept_no = ?";
        String insertSqlDepartament = "INSERT INTO departments (dept_no, dept_name) "
                + "VALUES (?, ?)";
        String updateSqlDepartament = "UPDATE departments SET dept_name = ? WHERE dept_no = ?";

        int lote = 5;
        int contador = 0;

        // Desactivamos el autocommit para poder ejecutar el batch y hacer commit al final
        connection.setAutoCommit(false);

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatementDepartamen = connection.prepareStatement(insertSqlDepartament);
        PreparedStatement updateStatementDepartamen = connection.prepareStatement(updateSqlDepartament);

        for (MySqlDepartment department : departments) {

            // Comprobamos si el empleado existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSqlDepartament);
            selectStatement.setString(1, department.getDepartmentId()); // Código del department
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatementDepartment(updateStatementDepartamen, department);
                updateStatementDepartamen.addBatch();
            } else {
                fillUpdateStatementDepartment(insertStatementDepartamen, department);
                insertStatementDepartamen.addBatch();
            }

            // Ejecutamos el batch cada lote de registros
            if (++contador % lote == 0) {
                updateStatementDepartamen.executeBatch();
                insertStatementDepartamen.executeBatch();
            }
        }

        // Ejecutamos el batch final de departamentos
        insertStatementDepartamen.executeBatch();
        updateStatementDepartamen.executeBatch();



        String selectSqlEmployee = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
        String insertSqlEmployee = "INSERT INTO employees (emp_no, first_name, last_name, gender, hire_date, birth_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String updateSqlEmployee = "UPDATE employees SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? WHERE emp_no = ?";

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatementEmployee = connection.prepareStatement(insertSqlEmployee);
        PreparedStatement updateStatementEmployee = connection.prepareStatement(updateSqlEmployee);

        contador = 0;

        for (MySqlEmployee employee : employees) {

            // Comprobamos si el empleado existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSqlEmployee);
            selectStatement.setInt(1, employee.getEmployeeId()); // Código del empleado
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatement(updateStatementEmployee, employee);
                updateStatementEmployee.addBatch();
            } else {
                fillInsertStatement(insertStatementEmployee, employee);
                insertStatementEmployee.addBatch();
            }

            // Ejecutamos el batch cada lote de registros
            if (++contador % lote == 0) {
                updateStatementEmployee.executeBatch();
                insertStatementEmployee.executeBatch();
            }
        }

        // Ejecutamos el batch final de empleados
        insertStatementEmployee.executeBatch();
        updateStatementEmployee.executeBatch();


        /** hacer el ultimo **/

        String selectSqlDemptEmpt = "SELECT COUNT(*) FROM dept_emp WHERE emp_no = ? and dept_no = ?";
        String insertSqlDeptEmpt = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) "
                + "VALUES (?, ?, ?, ?)";
        String updateSqlDeptempt = "UPDATE dept_emp SET from_date = ?, to_date = ? WHERE emp_no = ? and dept_no = ?";

        contador = 0;

        // Preparamos las consultas, una unica vez para poder reutilizarlas en el batch
        PreparedStatement insertStatementDeptEmpt = connection.prepareStatement(insertSqlDeptEmpt);
        PreparedStatement updateStatementDeptEmpt = connection.prepareStatement(updateSqlDeptempt);

        for (MySqlDeptEmp deptEmpt : listdeptEmpt) {

            // Comprobamos si el empleado existe
            PreparedStatement selectStatement = connection.prepareStatement(selectSqlDemptEmpt);
            selectStatement.setInt(1, deptEmpt.getEmployeeId());
            selectStatement.setString(2, deptEmpt.getDepartmentId());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next(); // Nos movemos a la primera fila
            int rowCount = resultSet.getInt(1);

            // Si existe, actualizamos. Si no, insertamos
            if(rowCount > 0) {
                fillUpdateStatementDeptEmp(updateStatementDeptEmpt, deptEmpt);
                updateStatementDeptEmpt.addBatch();
            } else {
                fillInsertStatementDeptEmp(insertStatementDeptEmpt, deptEmpt);
                insertStatementDeptEmpt.addBatch();
            }

            // Ejecutamos el batch cada lote de registros
            if (++contador % lote == 0) {
                updateStatementDeptEmpt.executeBatch();
                insertStatementDeptEmpt.executeBatch();
            }
        }
        // Ejecutamos el batch final
        insertStatementDeptEmpt.executeBatch();
        updateStatementDeptEmpt.executeBatch();


        // Hacemos commit y volvemos a activar el autocommit
        connection.commit();
        connection.setAutoCommit(true);
    }




    /**
     * Rellena los parámetros de un PreparedStatement para una consulta UPDATE.
     *
     * @param statement - PreparedStatement
     * @param department - Departamento
     * @throws SQLException - Error al rellenar los parámetros
     */
    private static void fillUpdateStatementDepartment(PreparedStatement statement, MySqlDepartment department) throws SQLException {
        statement.setString(1, department.getDepartmentId());
        statement.setString(2, department.getName());
    }


    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de empleados.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de empleados
     */
    private static List<MySqlEmployee> readDataEmployees() {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("empleados.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

            // Creamos la lista de empleados y el formato de fecha
            List<MySqlEmployee> employees = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            // Saltamos la primera linea, que contiene los nombres de las columnas del CSV
            reader.skip(1);
            String[] nextLine;

            // Leemos el fichero linea a linea
            while((nextLine = reader.readNext()) != null) {

                // Creamos el empleado y lo añadimos a la lista
                MySqlEmployee employee = new MySqlEmployee(
                        Integer.parseInt(nextLine[0]),
                        nextLine[2],
                        nextLine[3],
                        nextLine[4],
                        new Date(format.parse(nextLine[1]).getTime()),
                        new Date(format.parse(nextLine[5]).getTime())
                );
                employees.add(employee);
            }
            return employees;
        } catch (IOException e) {
            log.error("Error al leer el fichero CSV de empleados", e);
            throw new RuntimeException(e);
        } catch (CsvValidationException | ParseException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de empleados.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de empleados
     */
    private static List<MySqlDepartment> readDataDepartments() {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("departamentos.csv"))
                .withCSVParser(
                        new CSVParserBuilder()
                                .withSeparator(',')
                                .build())
                .build()) {

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
            log.error("Error al leer el fichero CSV de departamentos", e);
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }






    private static MySqlEmployee createEmployee() throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date hireDate =  new Date(format.parse("2024-11-07").getTime());
        Date birthDate =  new Date(format.parse("1996-04-08").getTime());
        return new MySqlEmployee(1, "Erán", "Guedes", "M",
                hireDate, birthDate);
    }

    /**
     * Inserta un único registro en ella utilizando JDBC.
     */
    private static void upsert(Connection connection, MySqlEmployee employee) throws SQLException{
        String selectSql = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";
        String updateSql = "UPDATE employees SET birth_date = ?, first_name = ?, last_name = ?, gender = ?, hire_date = ? WHERE emp_no = ?";
        String insertSql = "INSERT INTO employees (emp_no, birth_date, first_name, last_name, gender, hire_date) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement selectStatement = connection.prepareStatement(selectSql);
        selectStatement.setInt(1, employee.getEmployeeId());
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next(); // Nos movemos a la primera fila
        int rowCount = resultSet.getInt(1);

        if(rowCount > 0) {
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setDate(1, employee.getBirthDate());
            updateStatement.setString(2, employee.getFirstName());
            updateStatement.setString(3, employee.getLastName());
            updateStatement.setString(4, employee.getGender());
            updateStatement.setDate(5, employee.getHireDate());
            updateStatement.setInt(6, employee.getEmployeeId());
            int filasActualizadas = updateStatement.executeUpdate();
            log.debug("Filas Actualizadas: {}", filasActualizadas);

        } else {
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setInt(1, employee.getEmployeeId());
            insertStatement.setDate(2, employee.getBirthDate());
            insertStatement.setString(3, employee.getFirstName());
            insertStatement.setString(4, employee.getLastName());
            insertStatement.setString(5, employee.getGender());
            insertStatement.setDate(6, employee.getHireDate());
            int filasInsertadas = insertStatement.executeUpdate();
            log.debug("Filas Insertadas: {}", filasInsertadas);
        }
    }





    /**
     * Lee los datos del fichero CSV y los devuelve en una lista de empleados.
     * El fichero CSV debe estar en la raíz del proyecto.
     *
     * @return - Lista de empleados
     */
    private static List<MySqlEmployee> readData() {

        // Try-with-resources. Se cierra el reader automáticamente al salir del bloque try
        // CSVReader nos permite leer el fichero CSV linea a linea
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader("unirEmployees.csv"))
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
    private static void intake(Connection connection, List<MySqlEmployee> employees) throws SQLException {

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
