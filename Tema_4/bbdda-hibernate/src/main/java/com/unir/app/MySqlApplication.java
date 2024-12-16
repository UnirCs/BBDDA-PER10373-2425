package com.unir.app;

import com.unir.config.HibernateMySqlConfig;
import com.unir.config.LogbackConfig;
import com.unir.dao.DepartmentsDao;
import com.unir.dao.EmployeesDao;
import com.unir.model.mysql.Department;
import com.unir.model.mysql.DeptEmployee;
import com.unir.model.mysql.Employee;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Slf4j
public class MySqlApplication {

    public static void main(String[] args) {

        //Configuramos Logback para que muestre las sentencias SQL que se ejecutan unicamente.
        LogbackConfig.configureLogbackForHibernateSQL();

        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try (Session session = HibernateMySqlConfig.getSessionFactory().openSession()) {

            log.info("Conexión establecida con la base de datos MySQL");

            //Creamos los DAOs que nos permitirán interactuar con la base de datos
            EmployeesDao employeesDao = new EmployeesDao(session);
            DepartmentsDao departmentsDao = new DepartmentsDao(session);


            // 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
            List<Object[]> byGender = employeesDao.countByGender();
            byGender.forEach(row -> {
                Employee.Gender gender = Employee.Gender.valueOf(row[0].toString());
                Long count = ((Number) row[1]).longValue();
                log.info("Gender: {}, Count: {}", gender, count);
            });


            // 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable)
            Object[] bestPaid = employeesDao.bestPaid("d001").stream().findFirst().orElse(null);
            if (bestPaid != null) {
                String firstName = (String) bestPaid[0];
                String lastName = (String) bestPaid[1];
                Integer salary = ((Number) bestPaid[2]).intValue();

                log.info("Empleado mejor pagado: Nombre: {}, Apellido: {}, Salario: {}",
                        firstName, lastName, salary);
            } else {
                log.info("No se encontró ningún empleado en el departamento especificado.");
            }


            // 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable)
            employeesDao.secondBestPaid("d001").forEach(row -> {
                String firstName = (String) row[0];
                String lastName = (String) row[1];
                Integer salary = ((Number) row[2]).intValue();

                log.info("Segundo empleado mejor pagado: Nombre: {}, Apellido: {}, Salario: {}", firstName, lastName, salary);
            });

            // 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable)
            int hireMonth = 6;
            List<Long> results = employeesDao.monthHire(hireMonth);
            if (!results.isEmpty()) {
                log.info("En el mes: {} se contrataron un total de empleados de: {}", hireMonth, results.get(0));
            } else {
                log.info("No se han contratado empleados ese mes");
            }


        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }
}
