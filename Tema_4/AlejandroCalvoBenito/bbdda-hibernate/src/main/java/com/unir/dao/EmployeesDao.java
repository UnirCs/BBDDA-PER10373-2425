package com.unir.dao;

import com.unir.model.mysql.Employee;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.sql.SQLException;
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

    /**
     * Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
     * @throws SQLException Excepción en caso de error
     */
    public List<Object[]> countByGender() throws SQLException {
        List<Object[]> byGender = session.createNativeQuery(
                "SELECT gender, COUNT(*) AS Total " +
                        "FROM employees.employees " +
                        "GROUP BY gender " +
                        "ORDER BY Total DESC"
        ).list();
        return byGender;
    }


    /**
     * Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable)
     * @param departmentId Identificador del departamento
     * @return Persona mejor pagada
     * @throws SQLException Excepción en caso de error
     */
    public List<Object[]> bestPaid(String departmentId) throws SQLException {
        Query<Object[]> best = session.createNativeQuery(
                "SELECT e.first_name, e.last_name, s.salary " +
                        "FROM employees.employees e " +
                        "JOIN employees.salaries s ON e.emp_no = s.emp_no " +
                        "JOIN employees.dept_emp d ON e.emp_no = d.emp_no " +
                        "WHERE d.dept_no = :deptNo " +
                        "ORDER BY s.salary DESC " +
                        "LIMIT 1");
        best.setParameter("deptNo", departmentId);
        return best.list();
    }

    /**
     * Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable)
     * @param departmentId Identificador del departamento
     * @return Persona segunda mejor pagada
     * @throws SQLException Excepción en caso de error
     */
    public List<Object[]> secondBestPaid(String departmentId) throws SQLException {
        Query<Object[]> second = session.createNativeQuery("SELECT e.first_name, e.last_name, s.salary\n" +
                "FROM employees.employees e\n" +
                "JOIN employees.salaries s ON e.emp_no = s.emp_no\n" +
                "JOIN employees.dept_emp d ON e.emp_no = d.emp_no\n" +
                "WHERE d.dept_no = :deptNo\n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1 OFFSET 1");
        second.setParameter("deptNo", departmentId);
        return second.list();
    }

    /**
     * Mostrar el número de empleados contratados en un mes concreto (parámetro variable)
     * @param hireMonth Identificador del departamento
     * @return Numero de empleados contratados ese mes
     * @throws SQLException Excepción en caso de error
     */
    public List<Long> monthHire(Integer hireMonth) throws SQLException {
        Query<Long> month = session.createNativeQuery(
                "SELECT COUNT(*) as total_contratados " +
                        "FROM employees.employees e " +
                        "WHERE MONTH(e.hire_date) = :hireDate", Long.class
        );
        month.setParameter("hireDate", hireMonth);
        return month.list(); // Devuelve una lista con un único valor
    }

}
