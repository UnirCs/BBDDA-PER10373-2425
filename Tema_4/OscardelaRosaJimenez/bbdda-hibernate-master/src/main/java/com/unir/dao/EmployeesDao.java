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

    public List<Object[]> CountWomenAndMen() throws SQLException {
        Query<Object[]> query = session.createNativeQuery("SELECT employees.gender , count(employees.gender) AS 'Total'\n" +
                "from employees.employees\n" +
                "group by employees.employees.gender\n" +
                "order by count(employees.employees.gender) DESC;\n");
        //query.setParameter("deptNo", departmentId);
        return query.list();
    }

    public List<Object[]> BestPayed(String departmentId) throws SQLException {
            Query<Object[]> query2 = session.createNativeQuery("SELECT employees.first_name, employees.last_name, salaries.salary \n" +
                    "FROM employees.employees INNER JOIN employees.salaries ON employees.emp_no = salaries.emp_no \n" +
                    "INNER JOIN employees.dept_emp ON employees.emp_no = dept_emp.emp_no \n" +
                    "WHERE dept_emp.dept_no= :deptNo \n" +
                    "AND salaries.to_date > DATE(NOW()) \n" +
                    "AND salaries.salary = (SELECT MAX(salaries.salary) \n" +
                    "FROM employees.salaries \n" +
                    "INNER JOIN employees.dept_emp ON salaries.emp_no = dept_emp.emp_no \n" +
                    "WHERE dept_emp.dept_no = :deptNo AND salaries.to_date > DATE(NOW()))");
            query2.setParameter("deptNo", departmentId);
            return query2.list();
        }
    public List<Object[]> SecondBestPayed(String departmentId) throws SQLException {
        Query<Object[]> query3 = session.createNativeQuery("SELECT employees.first_name, employees.last_name, salaries.salary \n" +
                "FROM employees.employees INNER JOIN employees.salaries ON employees.emp_no = salaries.emp_no \n" +
                "INNER JOIN employees.dept_emp ON employees.emp_no = dept_emp.emp_no \n" +
                "WHERE dept_emp.dept_no= :deptNo \n" +
                "AND salaries.to_date > DATE(NOW()) \n" +
                "ORDER BY  salaries.salary DESC \n" +
                "LIMIT 1 OFFSET 1");

        query3.setParameter("deptNo", departmentId);
        return query3.list();
    }

    public List<Object[]> EmployeesHiredInMonth(String mes, String anyo) throws SQLException {
        Query<Object[]> query4 = session.createNativeQuery("SELECT employees.emp_no, employees.first_name, employees.last_name AS total \n" +
                "FROM employees.employees \n" +
                "WHERE MONTH(hire_date) = :mes AND YEAR(hire_date) = :anyo");
        query4.setParameter("mes", mes);
        query4.setParameter("anyo", anyo);
        //return query4.list();
        return query4.list();
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

}
