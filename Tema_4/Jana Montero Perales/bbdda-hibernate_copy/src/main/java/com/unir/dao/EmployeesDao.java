package com.unir.dao;

import com.unir.model.mysql.Employee;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public class EmployeesDao {

    private final Session session;

    /**
     * Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
     *
     * @return mapa con los generos como claves y las cantidades como valores
     */
    public Map<String, Integer> findNumberOfMenAndWomen() {
        Map<String, Integer> amounts = new HashMap<>();
        Query<Object[]> query = session.createNativeQuery("SELECT gender, COUNT(*) AS 'cantidad' from employees.employees\n" +
                "GROUP BY gender\n" +
                "ORDER BY cantidad DESC", Object[].class);
        query.getResultList().forEach(result -> amounts.put(result[0].toString(), ((Long) result[1]).intValue()));
        return amounts;
    }

    /**
     * Mostrar el nombre, apellido y salario de la persona mejor pagada de un departamento concreto
     * (parámetro variable).
     *
     * @param n offset (ordinal en la lista de mejor pagados)
     * @return el empleado
     */
    public Employee findNthPaidEmployeeInDepartment(String departmentName, int n) {
        Query<Employee> query = session.createNativeQuery("SELECT e.*\n" +
                "FROM employees.employees e\n" +
                "   JOIN employees.salaries s  ON e.emp_no = s.emp_no\n" +
                "   JOIN employees.dept_emp de ON e.emp_no = de.emp_no\n" +
                "   JOIN employees.departments d ON de.dept_no = d.dept_no\n" +
                "WHERE d.dept_name = :departmentName\n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1\n" +
                "OFFSET :offset\n", Employee.class);
        query.setParameter("departmentName", departmentName);
        query.setParameter("offset", n);
        return query.uniqueResult();
    }

    /**
     * Mostrar el número de empleados contratados en un mes concreto (parámetro variable).
     *
     * @param month el numero del mes
     * @return el numero de empleados
     */
    public Integer getNumberOfEmployeesHiredIn(int month) {
        Query<Integer> query = session.createNativeQuery(
                "SELECT COUNT(*) AS 'num_empleados' FROM employees.employees e\n" +
                        "WHERE MONTH(e.hire_date) = :month", Integer.class);
        query.setParameter("month", month);
        return query.uniqueResult();
    }

}
