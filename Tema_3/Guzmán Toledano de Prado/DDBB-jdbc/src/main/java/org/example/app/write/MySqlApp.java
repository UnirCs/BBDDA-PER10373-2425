package org.example.app.write;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import lombok.extern.slf4j.Slf4j;
import org.example.connectors.MySqlConnector;
import org.example.models.MySql.Department;
import org.example.models.MySql.Employee;
import org.example.app.fileAccess.MySqlCsv;
import org.example.models.MySql.Title;

import java.sql.*;
import java.util.List;

@Slf4j
public class MySqlApp {
    private static final String DATABASE = "employees";

    public static void main(String[] args) {

        try(Connection connection = new MySqlConnector(DATABASE).getConnection()) {
            log.info("Successfully connected to MySQL database");
            //insertTitle(connection, new Title(10001, "Welder", Date.valueOf("2021-06-26"), Date.valueOf("9999-01-01")));

            //List<Department> departments = MySqlCsv.getDepartmentsFromCsv("dept_data.csv");
            List<Employee> employees = MySqlCsv.getEmployeesFromCsv("employees_data.csv");

            //insertDepartments(connection, departments);
            insertEmployees(connection, employees);


        } catch (SQLException e) {
            log.error("Failed to insert data into MySQL database", e);
        } catch (Exception e) {
            log.error("Failed to connect to MySQL database", e);
        }
    }

    private static void testReading() {
        List<Department> departments = MySqlCsv.getDepartmentsFromCsv("dept_data.csv");
        List<Employee> employees = MySqlCsv.getEmployeesFromCsv("employees_data.csv");

        System.out.println("Departments:");
        for (Department department : departments) {
            System.out.println(department);
        }

        System.out.println("Employees:");
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    private static void insertDepartments (Connection connection, List<Department> departments) throws SQLException {
        String selectQuery = "SELECT COUNT(*) FROM departments WHERE dept_name = ?";
        String insertQuery = "INSERT INTO departments (dept_no, dept_name) VALUES (?, ?)";
        String updateQuery = "UPDATE departments SET dept_name = ? WHERE dept_no = ?";

        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        connection.setAutoCommit(false);

        for (Department department: departments){
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, department.getDept_name());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0){
                fillDeptStatement(updateStatement, department);
                updateStatement.executeUpdate();
                log.info("Department with name " + department.getDept_name() + " already exists");
            } else {
                fillDeptStatement(insertStatement, department);
                insertStatement.executeUpdate();
                log.info("Department with name " + department.getDept_name() + " inserted");
            }
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    private static void insertEmployees (Connection connection, List<Employee> employees) throws SQLException {
        String selectQuery = "SELECT COUNT(*) FROM employees WHERE first_name = ? AND last_name = ?";
        String insertQuery = "INSERT INTO employees (emp_no, birth_date, first_name, last_name, gender, hire_date) VALUES (?, ?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE employees SET WHERE emp_no = ?, birth_date = ?, first_name = ?, last_name = ?, gender = ?, hire_date = ?";

        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        connection.setAutoCommit(false);

        for (Employee employee: employees){
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, employee.getFirst_name());
            selectStatement.setString(2, employee.getLast_name());
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0){
                fillEmployeeStatement(updateStatement, employee);
                updateStatement.executeUpdate();
                log.info("Employee with first name " + employee.getFirst_name() + " and last name " + employee.getLast_name() + " already exists");
            } else {
                fillEmployeeStatement(insertStatement, employee);
                insertStatement.executeUpdate();
                insertDepEmployeeRelation(connection, employee);
                log.info("Employee with first name " + employee.getFirst_name() + " and last name " + employee.getLast_name() + " inserted");
            }
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    private static void insertDepEmployeeRelation (Connection connection, Employee employee) throws SQLException {
        String selectQuery = "SELECT COUNT(*) FROM departments WHERE dept_no = ?";
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
        selectStatement.setString(1, employee.getDept_no());
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);

        if (count == 0) {
            throw new SQLException("Department with dept_no " + employee.getDept_no() + " does not exist");
        }

        String insertQuery = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES (?, ?, ?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

        insertStatement.setInt(1, employee.getEmp_no());
        insertStatement.setString(2, employee.getDept_no());
        insertStatement.setString(3, employee.getHire_date().toString());
        insertStatement.setString(4, "9999-01-01");
        insertStatement.executeUpdate();
        log.info("Created relation between employee with emp_no " + employee.getEmp_no() + " and department with dept_no " + employee.getDept_no());
    }

    private static void insertTitle (Connection connection, Title title) throws SQLException {

        if (!employeeExist(connection, title.getEmp_no())) {
            throw new SQLException("Employee with emp_no " + title.getEmp_no() + " does not exist");
        }

        String selectQuery = "SELECT COUNT(*) FROM titles WHERE title = ? AND emp_no = ?";
        String insertQuery = "INSERT INTO titles (emp_no, title, from_date, to_date) VALUES (?, ?, ?, ?)";
        String updateQuery = "UPDATE titles SET title = ?, from_date = ?, to_date = ? WHERE emp_no = ?";

        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        connection.setAutoCommit(false);

        PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
        selectStatement.setString(1, title.getTitle());
        selectStatement.setInt(2, title.getEmp_no());
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);

        if (count > 0){
            fillTitleStatement(updateStatement, title);
            updateStatement.executeUpdate();
        } else {
            fillTitleStatement(insertStatement, title);
            insertStatement.executeUpdate();
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    private static boolean employeeExist(Connection connection, int emp_no) throws SQLException {
        String selectEmployee = "SELECT COUNT(*) FROM employees WHERE emp_no = ?";

        PreparedStatement selectEmployeeStatement = connection.prepareStatement(selectEmployee);
        selectEmployeeStatement.setInt(1,emp_no);
        ResultSet resultSetEmployee = selectEmployeeStatement.executeQuery();

        resultSetEmployee.next();
        int countEmployee = resultSetEmployee.getInt(1);

        return countEmployee != 0;
    }

    private static void fillDeptStatement (PreparedStatement statement, Department department) throws SQLException {
        statement.setString(1, department.getDept_no());
        statement.setString(2, department.getDept_name());
    }

    private static void fillEmployeeStatement (PreparedStatement statement, Employee employee) throws SQLException {
        statement.setInt(1, employee.getEmp_no());
        statement.setString(2, employee.getBirth_date().toString());
        statement.setString(3, employee.getFirst_name());
        statement.setString(4, employee.getLast_name());
        statement.setString(5, employee.getGender());
        statement.setString(6, employee.getHire_date().toString());
    }

    private static void fillTitleStatement (PreparedStatement statement, Title title) throws SQLException {
        statement.setInt(1, title.getEmp_no());
        statement.setString(2, title.getTitle());
        statement.setString(3, title.getFrom_date().toString());
        statement.setString(4, title.getTo_date().toString());
    }
}
