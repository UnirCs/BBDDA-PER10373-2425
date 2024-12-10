package com.unir.dao;

import com.unir.model.mysql.Employee;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class EmployeesDao {

    private final Session session;

    /**
     * Consulta de todos los empleados de la base de datos
     * Se puede hacer de dos formas:
     * 1. Con SQL nativo
     * 2. Con HQL: https://docs.jboss.org/hibernate/orm/3.5/reference/es-ES/html/queryhql.html
     * @throws SQLException Excepción en caso de error
     */
    public List<Employee> findAll() throws SQLException {
        List<Employee> employees = session.createNativeQuery("SELECT * FROM employees", Employee.class).list();
        log.debug("Número de empleados: {}", employees.size());
        session.createQuery("FROM Employee", Employee.class).list();
        return employees;
    }

    /**
     * Consulta de todos los empleados de un departamento
     * @param departmentId Identificador del departamento
     * @return Lista de empleados
     * @throws SQLException Excepción en caso de error
     */
    public List<Employee> findByDepartment(String departmentId) throws SQLException {
        Query<Employee> query = session.createNativeQuery("SELECT e.*\n" +
                "FROM employees.employees e\n" +
                "JOIN employees.dept_emp de ON e.emp_no = de.emp_no\n" +
                "JOIN employees.departments d ON de.dept_no = d.dept_no\n" +
                "WHERE d.dept_no = :deptNo", Employee.class);
        query.setParameter("deptNo", departmentId);
        return query.list();
    }

    /**
     * Obtención de un empleado por su identificador.
     * @param id - Identificador del empleado.
     * @return Empleado.
     * @throws SQLException - Excepción en caso de error.
     */
    public Employee getById(Integer id) throws SQLException {
        return session.get(Employee.class, id);
    }

    /**
     * Elimina un empleado de la base de datos.
     * @param employee - Empleado a eliminar.
     * @return true si se ha eliminado correctamente.
     * @throws SQLException - Excepción en caso de error.
     */
    public Boolean remove(Employee employee) throws SQLException {
        session.remove(employee);
        return true;
    }

    /**
     * Inserta un nuevo empleado en la base de datos.
     * @param employee - Empleado a insertar.
     * @return Empleado insertado.
     * @throws SQLException - Excepción en caso de error.
     */
    public Employee save(Employee employee) throws SQLException {
        session.persist(employee);
        return employee;
    }

    /**
     * Inserta un nuevo empleado en la base de datos si no existe.
     * Si existe, actualiza el empleado.
     *
     * Utilizamos la operacion merge de la sesion de Hibernate.
     * Merge de Hibernate funciona de la siguiente forma:
     * 1. Se comprueba que la entidad no exista en la sesion de Hibernate.
     * 2. Se hace una consulta a la base de datos para comprobar si la entidad existe.
     * 3. Si no existe, se inserta.
     * 4. Si existe, se actualiza.
     *
     * @param employee Empleado a insertar o actualizar
     * @return Empleado insertado o actualizado
     * @throws SQLException Excepción en caso de error
     */
    public Employee saveOrUpdate(Employee employee) throws SQLException {
        session.merge(employee);
        return employee;
    }

    // Ejercicio: Metodo para la consulta 1 de la Actividad 2
    // Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
    // A diferencia de las consultas anteriores de ejemplo, consultas de agregaciones (COUNT, SUM) o
    // combinaciones complejas (JOIN) no mapean directamente a las entidades. Por tanto, en lugar
    // de devolver un objeto de tipo Employee, le pedimso una lista de objetos genéricos
    public List<Object[]> findMenWomen() throws SQLException {
        Query<Object[]> query = session.createNativeQuery(
                "SELECT e.gender, COUNT(*) AS gender_count " +
                        "FROM employees.employees e " +
                        "GROUP BY e.gender " +
                        "ORDER BY gender_count DESC"
        );
        return query.list();
    }

    // Ejercicio: Metodo para la consulta 2 y 3 de la Actividad 2
    // Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento
    // concreto (parámetro variable).
    public List<Object[]> findBestPaidEmployeesInDepartment(String departmentName) throws SQLException {
        Query<Object[]> query = session.createNativeQuery(
                "SELECT e.first_name, e.last_name, s.salary\n" +
                "FROM employees.employees e\n" +
                "JOIN employees.salaries s ON e.emp_no = s.emp_no\n" +
                "JOIN employees.dept_emp de ON e.emp_no = de.emp_no\n" +
                "JOIN employees.departments d ON de.dept_no = d.dept_no\n" +
                "WHERE d.dept_name = :deptName\n" +
                "ORDER BY s.salary DESC"
        );
        query.setParameter("deptName", departmentName);
        return query.list();
    }

    // Ejercicio: Metodo para la consulta 4 de la Actividad 2
    // Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
    public Long showNumEmployeesHiredInMonth(Integer hireMonth) throws SQLException {
        Query<Long> query = session.createNativeQuery(
                "SELECT COUNT(*) " +
                        "FROM employees.employees e " +
                        "WHERE MONTH(hire_date) = :hireMonth", Long.class
        );
        query.setParameter("hireMonth", hireMonth);
        return query.uniqueResult();
    }
}
