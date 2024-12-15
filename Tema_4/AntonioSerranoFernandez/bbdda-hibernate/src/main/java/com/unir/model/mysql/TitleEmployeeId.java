// Ejercicio: Define una Clase para la Clave Compuesta de la tabla titles
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
// Necesitamos implementar Serializable para que Hibernate pueda serializar y deserializar la clase
public class TitleEmployeeId implements Serializable {

    private Integer empNo;
    private String title;
    private Date fromDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TitleEmployeeId that = (TitleEmployeeId) o;
        return Objects.equals(empNo, that.empNo) &&
                Objects.equals(title, that.title) &&
                Objects.equals(fromDate, that.fromDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empNo, title, fromDate);
    }
}
