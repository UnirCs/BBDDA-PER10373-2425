package com.unir.app;

import com.unir.config.HibernateMySqlConfig;
import com.unir.config.LogbackConfig;
import com.unir.dao.EmployeesDao;
import com.unir.model.mysql.Employee;
import com.unir.model.mysql.Salary;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.util.Comparator;
import java.util.Map;

@Slf4j
public class MySqlApplication {

    public static void main(String[] args) {

        String exerciseSeparator = "\n" + "-o".repeat(70) + "-\n";
        //Configuramos Logback para que muestre las sentencias SQL que se ejecutan unicamente.
        LogbackConfig.configureLogbackForHibernateSQL();

        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try (Session session = HibernateMySqlConfig.getSessionFactory().openSession()) {

            log.info("Conexión establecida con la base de datos MySQL");

            //Creamos los DAOs que nos permitirán interactuar con la base de datos
            EmployeesDao employeesDao = new EmployeesDao(session);

            // 1. Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
            Map<String, Integer> menAndWomenAmounts = employeesDao.findNumberOfMenAndWomen();
            log.info("Cantidad de hombres y mujeres empleados:");
            menAndWomenAmounts.keySet().forEach(k -> log.info("{}: {}", k, menAndWomenAmounts.get(k)));
            System.out.println(exerciseSeparator);

            // 2. Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
            String departmentName = "Customer Service";
            Employee bestPaidEmployee = employeesDao.findNthPaidEmployeeInDepartment(departmentName, 0);
            log.info("El empleado mejor pagado de {} es {} {} con un salario de {}",
                    departmentName,
                    bestPaidEmployee.getFirstName(),
                    bestPaidEmployee.getLastName(),
                    bestPaidEmployee.getSalaries().stream().max(Comparator.comparingInt(Salary::getSalary))
            );
            System.out.println(exerciseSeparator);

            // 3. Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
            departmentName = "Customer Service";
            Employee secondBestPaidEmployee = employeesDao.findNthPaidEmployeeInDepartment(departmentName, 1);
            log.info("El segundo empleado mejor pagado de {} es {} {} con un salario de {}",
                    departmentName,
                    secondBestPaidEmployee.getFirstName(),
                    secondBestPaidEmployee.getLastName(),
                    secondBestPaidEmployee.getSalaries().stream().max(Comparator.comparingInt(Salary::getSalary))
            );
            System.out.println(exerciseSeparator);

            // 4. Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
            int employeesHired = employeesDao.getNumberOfEmployeesHiredIn(1);
            log.info("En enero se contrataron {} empleados", employeesHired);
            System.out.println(exerciseSeparator);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }
}
