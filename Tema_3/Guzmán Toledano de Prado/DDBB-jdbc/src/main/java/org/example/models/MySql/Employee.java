package org.example.models.MySql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Date;

@Getter
@AllArgsConstructor
public class Employee {
    private int emp_no;
    private Date birth_date;
    private String first_name;
    private String last_name;
    private String gender;
    private Date hire_date;
    private String dept_no;

    @Override
    public String toString() {
        return "Employee{" +
                "emp_no=" + emp_no +
                ", birth_date=" + birth_date +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", gender='" + gender + '\'' +
                ", hire_date=" + hire_date +
                ", dept_no='" + dept_no + '\'' +
                '}';
    }
}
