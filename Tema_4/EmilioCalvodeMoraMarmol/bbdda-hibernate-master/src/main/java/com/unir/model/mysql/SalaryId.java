package com.unir.model.mysql;

import lombok.*;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Data
public class SalaryId implements Serializable {

    private Integer empNo;
    private java.time.LocalDate fromDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalaryId salaryId)) return false;
        return Objects.equals(empNo, salaryId.empNo) && Objects.equals(fromDate, salaryId.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empNo, fromDate);
    }
}
