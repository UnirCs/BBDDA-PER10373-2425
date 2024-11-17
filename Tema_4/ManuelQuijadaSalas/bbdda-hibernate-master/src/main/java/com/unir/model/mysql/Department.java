package com.unir.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

//La anotación @Entity indica que la clase representa una entidad de la base de datos
@Entity
//La anotación @Table indica el nombre de la tabla de la base de datos que representa este objeto
@Table(name = "departments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Department {

    //La anotación @Id indica que el atributo es la clave primaria de la tabla
    //La anotación @Column indica el nombre de la columna en la tabla
    //Con @GeneratedValue se indica que el valor de la clave primaria se genera automáticamente (
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE) (Mas info: https://jakarta.ee/specifications/persistence/2.2/apidocs/javax/persistence/generationtype)
    //columnDefinition se utiliza para definir el tipo de dato de la columna en la base de datos
    @Column(name = "dept_no", columnDefinition = "CHAR(4)")
    private String deptNo;

    @Column(name = "dept_name")
    private String deptName;

    //La anotación @OneToMany indica que la relación es de uno a muchos
    // mappedBy indica el nombre del atributo ¡JAVA! (no nombre de columna de DB) en la clase relacionada que mapea esta relación
    // fetch indica la forma en la que se obtienen los datos de la relación:
    // FetchType.EAGER indica que se obtienen los datos de la relación al mismo tiempo que se obtienen los datos de la entidad
    // FetchType.LAZY indica que se obtienen los datos de la relación solo cuando se accede a ellos
    @OneToMany(mappedBy = "deptNo", fetch = FetchType.EAGER)
    private Set<DeptEmployee> deptEmployees;
}
