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

    //tema 4
    /*
    * Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
    * */
    public List<Object[]> selectEmployesByGenderDesc() throws SQLException {
        Query<Object[]> query = session.createNativeQuery("SELECT count(*) as num_empleados, Gender \n" +
                "FROM employees.employees \n" +
                "group by gender \n" +
                "order by num_empleados desc", Object[].class);
        return query.list();
    }

    /*
    * Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto (parámetro variable).
    * */
    public Object[] selectEmployesBestSalaryByDepartment(String department) throws SQLException {
        Query<Object[]> query = session.createNativeQuery(
                "SELECT count(*) as cantidad, gender " +
                        "FROM employees.employees e " +
                        "JOIN employees.salaries s ON e.emp_no = s.emp_no " +
                        "JOIN employees.dept_emp de ON e.emp_no = de.emp_no " +
                        "JOIN employees.departments dep ON de.dept_no = dep.dept_no " +
                        "WHERE dep.dept_name = :deptNo " +
                        "AND s.salary = ( " +
                        "    SELECT MAX(sal.salary) " +
                        "    FROM employees.salaries sal " +
                        "    JOIN employees.dept_emp de2 ON sal.emp_no = de2.emp_no " +
                        "    JOIN employees.departments dep2 ON de2.dept_no = dep2.dept_no " +
                        "    WHERE dep2.dept_name = :deptNo " +
                        ") " +
                        "GROUP BY gender " +
                        "ORDER BY cantidad DESC",
                Object[].class
        ).setParameter("deptNo", department);

        return query.list().get(0); // Devuelve el primer resultado
    }


    /*
    * Mostrar el nombre, apellido y salario de la segunda persona mejor pagada de un departamento concreto (parámetro variable).
    * */
    public Object[] selectEmployesSecondBestSalaryByDepartment(String department) throws SQLException {
        Query<Object[]> query = session.createNativeQuery("SELECT e.first_name, e.last_name, s.salary\n" +
                        "FROM employees.employees e\n" +
                        "JOIN employees.salaries s ON e.emp_no = s.emp_no\n" +
                        "JOIN employees.dept_emp d ON e.emp_no = d.emp_no\n" +
                        "JOIN employees.departments ds ON d.dept_no = ds.dept_no\n" +
                        "WHERE ds.dept_name = :deptNo\n" +
                        "ORDER BY s.salary DESC\n" +
                        "LIMIT 1 OFFSET 1;", Object[].class)
                .setParameter("deptNo", department);
        return query.list().get(0);
    }

    /*
    * Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
    * */
    public List<Object[]> selecNumberEmployeesByMonth(String month) throws SQLException {
        Query<Object[]> query = session.createNativeQuery("select count(*) as cantidad_contratada, DATE_FORMAT(de.from_date, '%M') as mes" +
                        " from employees join employees.dept_emp de on employees.emp_no = de.emp_no\n"
                        + "where DATE_FORMAT(de.from_date, '%M') = :month\n"
                        + "group by mes;", Object[].class)
                .setParameter("month", month);
        return query.list();
    }

}
