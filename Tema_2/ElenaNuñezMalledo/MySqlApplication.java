package com.unir.app.read;

import com.unir.config.MySqlConnector;
import lombok.extern.slf4j.Slf4j;
import java.sql.PreparedStatement;

import java.sql.*;

@Slf4j

public class MySqlApplication {

    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        //Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try (Connection connection = new MySqlConnector("localhost", DATABASE).getConnection()) {

            log.info("Conexión establecida con la base de datos MySQL");

            countEmployedMenWomen(connection);
            highestPaidPerson (connection, "d008");
            secondHighestPaidPerson (connection, "d008");
            employeesSpecificMonth(connection, "19890101","19890131");

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }

    /**
     * Consulta 1: Número de hombres y mujeres
     */
    private static void countEmployedMenWomen(Connection connection) throws SQLException {
        String query = "select gender, count(*) as total from employees.employees group by gender order by total desc";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            log.info("Distribución de género:");

            while (resultSet.next()) {
                log.info("Género: {}, Cantidad: {}", resultSet.getString("gender"), resultSet.getInt("total"));
            }
        }
    }

    /**
    * Consulta 2: La persona mejor pagada de un departamento concreto
    */

        private static void highestPaidPerson (Connection connection, String departamento) throws SQLException {
            String query = "select e.emp_no, d.dept_name, e.first_name, e.last_name, s.salary\n" +
                    "from employees.dept_emp de\n" +
                    "join employees.departments d on de.dept_no = d.dept_no #Aquí se muestra a través de que atributo se relacionan las tablas\n" +
                    "join employees.employees e on e.emp_no = de.emp_no\n" +
                    "join employees.salaries s on s.emp_no = e.emp_no\n" +
                    "where d.dept_no = ?\n" +
                    "order by s.salary desc\n" +
                    "limit 1";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                // Establecer el valor del parámetro "?" (nombre del departamento)
                statement.setString(1, departamento);

                try (ResultSet resultSet = statement.executeQuery()) {
                    ;
                    log.info("La persona mejor pagada del departamento es: ");
                    while (resultSet.next()) {
                        log.info("Emp_no: {}, Departamento: {}, Nombre: {} {}, Salario: {},",
                                resultSet.getInt("e.emp_no"),
                                resultSet.getString("d.dept_name"),
                                resultSet.getString("e.first_name"),
                                resultSet.getString("e.last_name"),
                                resultSet.getInt("s.salary"));
                    }
                }
            }

        }

    /**
     * Consulta 3: La segunda persona mejor pagada de un departamento concreto.
     */
            private static void secondHighestPaidPerson (Connection connection, String departamento) throws SQLException {
                String query = "select e.emp_no, d.dept_name, e.first_name, e.last_name, s.salary\n" +
                        "from employees.dept_emp de\n" +
                        "join employees.departments d on de.dept_no = d.dept_no\n" +
                        "join employees.employees e on e.emp_no = de.emp_no\n" +
                        "join employees.salaries s on s.emp_no = e.emp_no\n" +
                        "where d.dept_no = ?\n" +
                        "limit 1 offset 1";

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    // Establecer el valor del parámetro "?" (nombre del departamento)
                    statement.setString(1, departamento);

                    try (ResultSet resultSet = statement.executeQuery()){;
                        log.info("La segunda persona mejor pagada del departamento es: ");
                        while (resultSet.next()) {
                            log.info("Emp_no: {}, Departamento: {}, Nombre: {} {}, Salario: {},",
                                    resultSet.getInt("e.emp_no"),
                                    resultSet.getString("d.dept_name"),
                                    resultSet.getString("e.first_name"),
                                    resultSet.getString("e.last_name"),
                                    resultSet.getInt("s.salary"));
                        }
                    }
                }
        }

    /**
     * Consulta 4: Mostrar el número de empleados contratados en un mes concreto (parámetro variable)
     */
    private static void employeesSpecificMonth (Connection connection, String fechaInicial, String fechaFinal) throws SQLException {
        String query = "select count(*) as cantidad from employees.employees\n" +
                "where hire_date between ? and ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Establecer el valor del parámetro "?" (nombre del departamento)
            statement.setString(1, fechaInicial);
            statement.setString(2, fechaFinal);

            try (ResultSet resultSet = statement.executeQuery()) {

                log.info("¿Cuantas personas se contrataron entre '{}' y '{}': ", fechaInicial, fechaFinal);
                while (resultSet.next()) {
                    log.info("Cantidad: {}",
                            resultSet.getInt("cantidad"));
                }
            }
        }
    }
}












