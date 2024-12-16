package com.unir.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "dept_emp")
// @IdClass se utiliza para indicar que la clase tiene una clave primaria compuesta. La clase referenciada contiene la definición de la clave primaria
@IdClass(DeptEmployeeId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeptEmployee {

    // @Id indica que el atributo es la clave primaria de la tabla (o parte de ella)
    @Id
    // @ManyToOne indica que la relación es de muchos a uno
    // @JoinColumn indica el nombre de la columna ¡DE LA DB! en la tabla que representa la relación (al fin y al cabo es una especializacion de @Column)
    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee empNo;

    // @Id indica que el atributo es la clave primaria de la tabla (o parte de ella)
    @Id
    @ManyToOne
    @JoinColumn(name = "dept_no", columnDefinition = "CHAR(4)")
    private Department deptNo;

    @Column(name = "from_date", columnDefinition="DATE")
    private Date fromDate;

    @Column(name = "to_date", columnDefinition="DATE")
    private Date toDate;
}
