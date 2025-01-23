package com.unir.app;

import com.unir.config.HibernateMySqlConfig;
import com.unir.config.LogbackConfig;
import com.unir.dao.DepartmentsDao;
import com.unir.dao.EmployeesDao;
import com.unir.model.mysql.Department;
import com.unir.model.mysql.DeptEmployee;
import com.unir.model.mysql.Employee;
import com.unir.model.mysql.Salary;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

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

            //Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
            log.info("Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.");
            Map<String, Integer> amountMenAndWomen = employeesDao.findNumberOfMenAndWomen();
            for(String key : amountMenAndWomen.keySet()){
                log.info("{}: {}", key, amountMenAndWomen.get(key));
            }

            //Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
            log.info("Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).");
            Employee bestPaidEmployee = employeesDao.findNthPaidEmployeeInDepartment("Customer Service", 0);
            int salario = 0;
            for (Salary s : bestPaidEmployee.getSalaries()){
                if (salario < s.getSalary()){
                    salario = s.getSalary();
                }
            }
            log.info("Mejor empleado: {} {} con salario: {}",
                    bestPaidEmployee.getFirstName(),
                    bestPaidEmployee.getLastName(),
                    salario);

            //Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
            log.info("Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).");
            Employee secondBestPaidEmployee = employeesDao.findNthPaidEmployeeInDepartment("Customer Service",1);
            salario = 0;
            for (Salary s : secondBestPaidEmployee.getSalaries()){
                if (salario < s.getSalary()){
                    salario = s.getSalary();
                }
            }
            log.info("Segundo mejor empleado: {} {} con salario: {}",
                    secondBestPaidEmployee.getFirstName(),
                    secondBestPaidEmployee.getLastName(),
                    salario);

            //Mostrar el número de empleados contratados en un mes concreto.
            log.info("Mostrar el número de empleados contratados en un mes concreto.");
            int employeesHired = employeesDao.hiredEmployeesInMonth(1);
            log.info("Empleados contratados en el mes 1: {}", employeesHired);

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }
}
