package com.unir.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class Salaryid implements Serializable {

    private Integer empNo;
    private Date fromDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Salaryid that = (Salaryid) o;
        return Objects.equals(empNo, that.empNo) &&
                Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empNo, fromDate);
    }
}
