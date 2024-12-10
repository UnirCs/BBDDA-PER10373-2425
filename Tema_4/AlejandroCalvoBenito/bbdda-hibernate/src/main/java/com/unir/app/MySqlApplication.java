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

            //Ejemplo de uso de DAO 1: Obtenemos todos los empleados de la base de datos
            List<Employee> employees = employeesDao.findByDepartment("d001");
            log.info("Empleados del departamento d001: {}", employees.size());

            //Ejemplo de uso de DAO 2: Insertamos un nuevo departamento. Save por defecto no actualiza, solo inserta.
            Department bbddDepartment = new Department();
            bbddDepartment.setDeptName("Database Departmentt");
            bbddDepartment.setDeptNo("d010");

            //Ejemplo de uso de DAO 2.1: Obtenemos un departamento de la base de datos
            Department bbddDepartment2 = departmentsDao.findById("d002");
            log.info("Departamento d002: {}", bbddDepartment2);
            log.info("Empleados del departamento d002: {}", bbddDepartment2.getDeptEmployees().size());

            // Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
            List<Object[]> byGender = employeesDao.countByGender();
            for (Object[] row : byGender) {
                // Convierte la columna 'gender' al tipo enum Employee.Gender
                Employee.Gender gender = Employee.Gender.valueOf(row[0].toString());
                Long count = ((Number) row[1]).longValue(); // Columna 'COUNT(*)'

                log.info("Gender: {}, Count: {}", gender, count);
            }


            //Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable)
            List<Object[]> bestPaid = employeesDao.bestPaid("d001");
            for (Object[] row : bestPaid) {
                String firstName = (String) row[0];
                String lastName = (String) row[1];
                Integer salary = ((Number) row[2]).intValue();

                log.info("Empleado mejor pagado: Nombre: {}, Apellido: {}, Salario: {}",
                        firstName, lastName, salary);
            }


            //Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable)
            List<Object[]> secondBestPaid = employeesDao.secondBestPaid("d001");
            for (Object[] row : secondBestPaid) {
                String firstName = (String) row[0];
                String lastName = (String) row[1];
                Integer salary = ((Number) row[2]).intValue();

               log.info("Segundo empleado mejor pagado: Nombre: {}, Apellido: {}, Salario: {}", firstName, lastName, salary);
            }

            //Mostrar el número de empleados contratados en un mes concreto (parámetro variable)
            Integer hireMonth = 06;
            List<Long> results = employeesDao.monthHire(hireMonth);
            if (!results.isEmpty()) {
                log.info("En el mes: {} contratados un total de empleados de: {}", hireMonth, results.get(0));
            } else log.info("No se han contratado empleados ese mes");


            //OJO! Las operaciones de escritura se deben hacer DENTRO de una transacción
            try {
                //departmentsDao.save(bbddDepartment);
                departmentsDao.saveOrUpdate(bbddDepartment);
                log.info("Departamento insertado o actualizado: {}", bbddDepartment);
            } catch (Exception e) {
                log.error("Error al insertar el departamento", e);
            }

            //Ejemplo de uso de DAO 3: La actualizacion ocurre cuando modificamos un objeto que ya existe en la base de datos (Entity Manager controla su ciclo de vida) y hacemos commit
            //Lo recuperamos de la base de datos.
            //Lo modificamos.
            session.beginTransaction();
            log.info("Obteniendo jesus");
            Employee jesus = employeesDao.getById(1001);
            jesus.setFirstName(("J" + System.currentTimeMillis()));
            log.info("jesus modificado");
            //Al hacer commit de la transaccion se actualiza el objeto en la base de datos sin hacer un update explicito (EM controla el ciclo de vida del objeto)
            session.getTransaction().commit();

            //Ejemplo de uso de DAO 4: Eliminamos un empleado
            session.beginTransaction();
            //Eliminamos un empleado
            employeesDao.remove(jesus);
            jesus = employeesDao.getById(1001);
            //Hacemos rollback para que no se aplique la eliminación
            session.getTransaction().rollback();

            //session.beginTransaction();
            //log.info("Obteniendo Roberto");
            //Employee roberto = employeesDao.getById(1002);
            //Set<DeptEmployee> deptEmployee = roberto.getDeptEmployees();
            // Asegurarse de que la colección esté inicializada
            //deptEmployee.size(); // Acceder a la colección para inicializarla, ya que si no Hibernate no la cargará

            //employeesDao.remove(roberto);
            //log.info("Roberto eliminado");
            //session.getTransaction().commit();

            //Insertamos de nuevo a Roberto
            //session.beginTransaction();
            //employeesDao.save(new Employee(roberto.getEmpNo(), roberto.getBirthDate(), roberto.getFirstName(), roberto.getLastName(), roberto.getGender(), roberto.getHireDate(), deptEmployee));
            //session.getTransaction().commit();
            //log.info("Roberto insertado de nuevo");

        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }
}
