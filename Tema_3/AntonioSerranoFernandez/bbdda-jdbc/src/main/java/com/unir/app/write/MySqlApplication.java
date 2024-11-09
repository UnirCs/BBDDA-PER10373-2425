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

    public static void main(String[] args) {

        //Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.warn("Recuerda que el fichero unirEmployees.csv debe estar en la raíz del proyecto, es decir, en la carpeta {}"
                    , System.getProperty("user.dir"));
            log.info("Conexión establecida con la base de datos MySQL");

            // Leemos los datos del fichero CSV por lotes y realizamos las sentencias UPDATE/INSERT
            readInsertUpdateDepartments(connection);
            readInsertUpdateEmployees(connection);
            readInsertUpdateDeptEmp(connection);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    private static void readInsertUpdateDepartments(Connection connection) throws SQLException, IOException, CsvValidationException {

        // Preparamos sentencias SQL INSERT y UPDATE
        String insertDepartmentSql = "INSERT INTO departments (dept_no, dept_name) VALUES (?, ?)";
        String updateDepartmentSql = "UPDATE departments SET dept_name = ? WHERE dept_no = ?";

        PreparedStatement insertDeptStmt = connection.prepareStatement(insertDepartmentSql);
        PreparedStatement updateDeptStmt = connection.prepareStatement(updateDepartmentSql);

        /* Desactivamos el modo de autocommit en la conexión con la base de datos, dado que
         * es más eficiente confirmar los cambios una vez por lote en lugar de después de
         * cada sentencia individual. Así también nos aseguramos de que todoo el grupo de
         * operaciones se ejecuta de manera atómica (todas o ninguna)
         */
        connection.setAutoCommit(false);

        int batchSize = 10;
        int count = 0;
        int totalUpdates = 0;
        int totalInserts = 0;

        try (CSVReader reader = new CSVReaderBuilder(new FileReader("new_departments.csv"))
                .withCSVParser(new CSVParserBuilder().withSeparator(',').build()).build()) {
            reader.skip(1);
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                /* Preparamos la sentencia de actualización. Intentamos primero actualizar
                 * el registro mediante la sentencia UPDATE. La llamada a updateStmt.executeUpdate()
                 *  devuelve el número de filas que se ven afectadas por la operación de
                 * actualización. Si el resultado es 0, significa que no se encontró un
                 * registro coincidente, y por lo tanto, la fila no existe en la base de datos
                 */
                updateDeptStmt.setString(1, nextLine[1]);
                updateDeptStmt.setString(2, nextLine[0]);
                // Ejecutamos la sentencia de actualización
                int rowsAffected = updateDeptStmt.executeUpdate();
                totalUpdates += rowsAffected;

                /* Si no se actualizó ninguna fila (rowsAffected == 0), preparamos la
                 * sentencia de inserción.
                 * Al probar primero la sentencia UPDATE y luego la de INSERT de manera
                 * condicional, podemos prescindir de realizar la sentencia SELECT que
                 * venía en el código de apoyo. Esta forma de comprobación es más eficiente
                 * porque se reduce el número de consultas enviadas a la base de datos
                 */
                if (rowsAffected == 0) {
                    insertDeptStmt.setString(1, nextLine[0]);
                    insertDeptStmt.setString(2, nextLine[1]);
                    insertDeptStmt.addBatch();
                    totalInserts++;
                }

                if (++count % batchSize == 0) {
                    // Ejecutamos el lote (batch) de inserciones
                    insertDeptStmt.executeBatch();
                    /* Hacemos commit de la transacción. De esta manera, actualizamos la base de
                     * datos en cada lote. Así evitamos cargar en memoria todos los nuevos
                     * registros del archivo CSV. En este caso son pocos, pero si fueran muchos
                     * podríamos quedarnos sin memoria
                     */
                    connection.commit();
                }
            }
            // Ejecutamos cualquier batch restante
            insertDeptStmt.executeBatch();
            // Hacemos el commit final de la transacción
            connection.commit();

            // Log de los resultados totales de sentencias UPDATE e INSERT
            log.info("Total de sentencias UPDATE ejecutadas en departamentos: " + totalUpdates);
            log.info("Total de sentencias INSERT ejecutadas en departamentos: " + totalInserts);
        }
    }

    private static void readInsertUpdateEmployees(Connection connection) throws SQLException, IOException, CsvValidationException, ParseException {

        // Preparamos sentencias SQL INSERT y UPDATE
        String insertEmployeeSql = "INSERT INTO employees (emp_no, first_name, last_name, gender, hire_date, birth_date) VALUES (?, ?, ?, ?, ?, ?)";
        String updateEmployeeSql = "UPDATE employees SET first_name = ?, last_name = ?, gender = ?, hire_date = ?, birth_date = ? WHERE emp_no = ?";

        PreparedStatement insertEmployeeStmt = connection.prepareStatement(insertEmployeeSql);
        PreparedStatement updateEmployeeStmt = connection.prepareStatement(updateEmployeeSql);

        connection.setAutoCommit(false);
        int batchSize = 10;
        int count = 0;
        int totalUpdates = 0;
        int totalInserts = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try (CSVReader reader = new CSVReaderBuilder(new FileReader("new_employees.csv"))
                .withCSVParser(new CSVParserBuilder().withSeparator(',').build()).build()) {
            reader.skip(1);
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                // Preparamos la sentencia de actualización
                updateEmployeeStmt.setString(1, nextLine[1]);
                updateEmployeeStmt.setString(2, nextLine[2]);
                updateEmployeeStmt.setString(3, nextLine[3]);
                updateEmployeeStmt.setDate(4, new Date(format.parse(nextLine[4]).getTime()));
                updateEmployeeStmt.setDate(5, new Date(format.parse(nextLine[5]).getTime()));
                updateEmployeeStmt.setInt(6, Integer.parseInt(nextLine[0]));
                // Ejecutamos la sentencia de actualización
                int rowsAffected = updateEmployeeStmt.executeUpdate();
                totalUpdates += rowsAffected;

                // Si no se actualizó ninguna fila, preparamos la sentencia de inserción
                if (rowsAffected == 0) {
                    insertEmployeeStmt.setInt(1, Integer.parseInt(nextLine[0]));
                    insertEmployeeStmt.setString(2, nextLine[1]);
                    insertEmployeeStmt.setString(3, nextLine[2]);
                    insertEmployeeStmt.setString(4, nextLine[3]);
                    insertEmployeeStmt.setDate(5, new Date(format.parse(nextLine[4]).getTime()));
                    insertEmployeeStmt.setDate(6, new Date(format.parse(nextLine[5]).getTime()));
                    insertEmployeeStmt.addBatch();
                    totalInserts++;
                }

                if (++count % batchSize == 0) {
                    // Ejecutamos el lote de inserciones
                    insertEmployeeStmt.executeBatch();
                    // Hacemos el commit de la transacción
                    connection.commit();
                }
            }
            // Ejecutamos cualquier lote restante
            insertEmployeeStmt.executeBatch();
            // Hacemos commit final de la transacción
            connection.commit();

            // Log de los resultados totales de sentencias UPDATE e INSERT
            log.info("Total de sentencias UPDATE ejecutadas en empleados: " + totalUpdates);
            log.info("Total de sentencias INSERT ejecutadas en empleados: " + totalInserts);
        }
    }

    private static void readInsertUpdateDeptEmp(Connection connection) throws SQLException, IOException, CsvValidationException, ParseException {

        // Preparamos sentencias SQL INSERT y UPDATE
        String insertDeptEmpSql = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES (?, ?, ?, ?)";
        String updateDeptEmpSql = "UPDATE dept_emp SET dept_no = ?, from_date = ?, to_date = ? WHERE emp_no = ?";

        PreparedStatement insertDeptEmpStmt = connection.prepareStatement(insertDeptEmpSql);
        PreparedStatement updateDeptEmpStmt = connection.prepareStatement(updateDeptEmpSql);

        connection.setAutoCommit(false);
        int batchSize = 10;
        int count = 0;
        int totalUpdates = 0;
        int totalInserts = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try (CSVReader reader = new CSVReaderBuilder(new FileReader("new_employee_departments.csv"))
                .withCSVParser(new CSVParserBuilder().withSeparator(',').build()).build()) {
            reader.skip(1);
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                // Preparamos la sentencia de actualización
                updateDeptEmpStmt.setString(1, nextLine[1]);
                updateDeptEmpStmt.setDate(2, new Date(format.parse(nextLine[2]).getTime()));
                updateDeptEmpStmt.setDate(3, new Date(format.parse(nextLine[3]).getTime()));
                updateDeptEmpStmt.setInt(4, Integer.parseInt(nextLine[0]));
                // Ejecutamos la sentencia de actualización
                int rowsAffected = updateDeptEmpStmt.executeUpdate();
                totalUpdates += rowsAffected;

                // Si no se actualizó ninguna fila, preparamos la sentencia de inserción
                if (rowsAffected == 0) {
                    insertDeptEmpStmt.setInt(1, Integer.parseInt(nextLine[0]));
                    insertDeptEmpStmt.setString(2, nextLine[1]);
                    insertDeptEmpStmt.setDate(3, new Date(format.parse(nextLine[2]).getTime()));
                    insertDeptEmpStmt.setDate(4, new Date(format.parse(nextLine[3]).getTime()));
                    insertDeptEmpStmt.addBatch();
                    totalInserts++;
                }

                if (++count % batchSize == 0) {
                    // Ejecutamos el lote de inserciones
                    insertDeptEmpStmt.executeBatch();
                    // Hacemos el commit de la transacción
                    connection.commit();
                }
            }
            // Ejecutamos cualquier lote restante
            insertDeptEmpStmt.executeBatch();
            // Hacemos el commit final de la transacción
            connection.commit();

            // Log de los resultados totales de sentencias UPDATE e INSERT
            log.info("Total de sentencias UPDATE ejecutadas en dept_emp: " + totalUpdates);
            log.info("Total de sentencias INSERT ejecutadas en dept_emp: " + totalInserts);
        }
    }
}
