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
            log.info("Empleados del departamento d001: {}", employees.size() + "\n");

            List<Employee> employees2 = employeesDao.findByDepartment("d002");
            log.info("Empleados del departamento d002: {}", employees2.size() + "\n");

            //  Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.

            List<Object[]> employees3= employeesDao.CountWomenAndMen();
            log.info("El número de hombres es {} ", employees3.get(0)[1] + " y de mujeres es " + employees3.get(1)[1] + "\n") ;

            // Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
            List<Object[]> employees4 = employeesDao.BestPayed("d001");
            log.info("la persona mejor pagada del departamento d001 es {}", employees4.get(0)[0] + " " + employees4.get(0)[1] + " con un salario de " + employees4.get(0)[2] + "\n");


            // Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
            List<Object[]> employees5 = employeesDao.SecondBestPayed("d001");
            log.info("la persona segunda mejor pagada del departamento d001 es {}", employees5.get(0)[0] + " " + employees5.get(0)[1] + " con un salario de " + employees5.get(0)[2] + "\n");

            //Mostrar el número de empleados contratados en un mes concreto (parámetro variable)
            List<Object[]> employees6 = employeesDao.EmployeesHiredInMonth("05", "1985");
            log.info("El número de empleados contratados en mayo de 1985 es {}", employees6.size() + "\n");


            //Ejemplo de uso de DAO 2: Insertamos un nuevo departamento. Save por defecto no actualiza, solo inserta.
            Department bbddDepartment = new Department();
            bbddDepartment.setDeptName("Database Departmentt");
            bbddDepartment.setDeptNo("d010");

            //Ejemplo de uso de DAO 2.1: Obtenemos un departamento de la base de datos
            Department bbddDepartment2 = departmentsDao.findById("d002");
            log.info("Departamento d002: {}", bbddDepartment2);
            log.info("Empleados del departamento d002: {}", bbddDepartment2.getDeptEmployees().size());

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

            session.beginTransaction();
            log.info("Obteniendo Miguel");
            Employee roberto = employeesDao.getById(1016);
            Set<DeptEmployee> deptEmployee = roberto.getDeptEmployees();
            // Asegurarse de que la colección esté inicializada
            deptEmployee.size(); // Acceder a la colección para inicializarla, ya que si no Hibernate no la cargará

            employeesDao.remove(roberto);
            log.info("Miguel eliminado");
            session.getTransaction().commit();

            //Insertamos de nuevo a Roberto
            session.beginTransaction();
            employeesDao.save(new Employee(roberto.getEmpNo(),roberto.getBirthDate(), roberto.getFirstName(), roberto.getLastName(),roberto.getGender(),roberto.getHireDate(),roberto.getDeptEmployees() ));
            session.getTransaction().commit();
            log.info("Miguel insertado de nuevo");




        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }
}
