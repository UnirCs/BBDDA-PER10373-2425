package com.unir.dao;

import com.unir.model.mysql.Employee;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public class EmployeesDao {

    private final Session session;


    //Obtener el número de hombres y mujeres de la base de datos. Ordenar de forma descendente.
    public Map<String, Integer> findNumberOfMenAndWomen() {
        Map<String, Integer> number = new HashMap<>();
        Query<Object[]> query = session.createNativeQuery("SELECT emp.gender AS Genero, COUNT(*) AS Numero\n" +
                "FROM employees.employees emp\n" +
                "GROUP BY emp.gender\n" +
                "ORDER BY Numero DESC\n", Object[].class);
        List<Object[]> results = query.getResultList();
        for(Object[] result : results) {
            String gender = result[0].toString();
            Integer amount = ((Long) result[1]).intValue();
            number.put(gender, amount);
        }
        return number;
    }

    //Mostrar el nombre, apellido y salario de la n persona mejor pagada de un departamento concreto (parámetro variable).
    public Employee findNthPaidEmployeeInDepartment(String departmentName, int n) {
        Query<Employee> query = session.createNativeQuery("SELECT emp.*\n" +
                "FROM employees.employees emp\n" +
                "   JOIN employees.salaries s  ON emp.emp_no = s.emp_no\n" +
                "   JOIN employees.dept_emp de ON emp.emp_no = de.emp_no\n" +
                "   JOIN employees.departments d ON de.dept_no = d.dept_no\n" +
                "WHERE d.dept_name = :departmentName\n" +
                "ORDER BY s.salary DESC\n" +
                "LIMIT 1\n" +
                "OFFSET :offset\n", Employee.class);
        query.setParameter("departmentName", departmentName);
        query.setParameter("offset", n);
        return query.uniqueResult();
    }


    //Mostrar el número de empleados contratados en un mes concreto.
    public Integer hiredEmployeesInMonth(int month) {
        Query<Integer> query = session.createNativeQuery("SELECT count(*) as NumeroEmpleados\n" +
                "FROM employees.employees emp\n" +
                "WHERE MONTH(emp.hire_date) = :month", Integer.class);
        query.setParameter("month", month);
        return query.uniqueResult();
    }

}
