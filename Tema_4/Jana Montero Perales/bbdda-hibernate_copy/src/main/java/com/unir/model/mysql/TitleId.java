package com.unir.model.mysql;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Data
public class TitleId implements Serializable {

    private Integer empNo;

    private String title;

    private Date fromDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TitleId titleId)) return false;
        return Objects.equals(empNo, titleId.empNo) && Objects.equals(title, titleId.title) && Objects.equals(fromDate, titleId.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empNo, title, fromDate);
    }
}
