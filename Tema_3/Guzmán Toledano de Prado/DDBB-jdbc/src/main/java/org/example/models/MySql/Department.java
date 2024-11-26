package org.example.models.MySql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Department {
    private String dept_no;
    private String dept_name;

    @Override
    public String toString() {
        return "Department{" +
                "dept_no='" + dept_no + '\'' +
                ", dept_name='" + dept_name + '\'' +
                '}';
    }
}
