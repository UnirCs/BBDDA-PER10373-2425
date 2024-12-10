package com.unir.employees.model.db;


import lombok.*;


import java.io.Serializable;
import java.util.Date;

public class SalaryId implements Serializable {
    private Integer employee; // Debe coincidir con 'emp_no' (Integer)
    private Date fromDate;    // Debe coincidir con 'from_date' (Date)

    public SalaryId() {}

    public SalaryId(Integer employee, Date fromDate) {
        this.employee = employee;
        this.fromDate = fromDate;
    }

    // Getters y Setters
    public Integer getEmployee() {
        return employee;
    }

    public void setEmployee(Integer employee) {
        this.employee = employee;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    // Sobrescribir equals() y hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SalaryId salaryId = (SalaryId) o;

        if (!employee.equals(salaryId.employee)) return false;
        return fromDate.equals(salaryId.fromDate);
    }

    @Override
    public int hashCode() {
        int result = employee.hashCode();
        result = 31 * result + fromDate.hashCode();
        return result;
    }
}

