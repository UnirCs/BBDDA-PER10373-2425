// Ejercicio Tema 5, Parte 2: Ampliación de Repositorios
// Clase creada para permitir consultas dinámicas relacionadas
// con Employees
package com.unir.employees.specifications;

import com.unir.employees.model.db.Employee;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.sql.Date;

public class EmployeeSpecifications {

    /**
     * Filtrar empleados por nombre de pila (parcial).
     *
     * @param firstName el nombre de pila que se desea buscar.
     * @return Specification<Employee> que aplica el filtro "LIKE" en el atributo firstName.
     */
    public static Specification<Employee> withFirstName(String firstName) {
        return (root, query, builder) ->
                StringUtils.hasText(firstName) ? builder.like(root.get("firstName"), "%" + firstName + "%") : null;
    }

    /**
     * Filtrar empleados por apellido (parcial).
     *
     * @param lastName el apellido que se desea buscar.
     * @return Specification<Employee> que aplica el filtro "LIKE" en el atributo lastName.
     */
    public static Specification<Employee> withLastName(String lastName) {
        return (root, query, builder) ->
                StringUtils.hasText(lastName) ? builder.like(root.get("lastName"), "%" + lastName + "%") : null;
    }

    /**
     * Filtrar empleados por género.
     *
     * @param gender el género que se desea buscar (ejemplo: "M" o "F").
     * @return Specification<Employee> que aplica el filtro "EQUAL" en el atributo gender.
     */
    public static Specification<Employee> withGender(String gender) {
        return (root, query, builder) ->
                StringUtils.hasText(gender) ? builder.equal(root.get("gender"), gender) : null;
    }

    /**
     * Filtrar empleados contratados en una fecha exacta.
     *
     * @param hireDate la fecha de contratación exacta que se desea buscar.
     * @return Specification<Employee> que aplica el filtro "EQUAL" en el atributo hireDate.
     */
    public static Specification<Employee> withHireDate(Date hireDate) {
        return (root, query, builder) ->
                hireDate != null ? builder.equal(root.get("hireDate"), hireDate) : null;
    }

    /**
     * Filtrar empleados contratados entre dos fechas.
     *
     * @param hiredAfter fecha inicial del rango (inclusive).
     * @param hiredBefore fecha final del rango (inclusive).
     * @return Specification<Employee> que aplica el filtro "BETWEEN" en el atributo hireDate.
     */
    public static Specification<Employee> withHireDateBetween(Date hiredAfter, Date hiredBefore) {
        return (root, query, builder) ->
                (hiredAfter != null && hiredBefore != null) ?
                        builder.between(root.get("hireDate"), hiredAfter, hiredBefore) : null;
    }

    /**
     * Filtrar empleados por fecha de nacimiento exacta.
     *
     * @param birthDate la fecha de nacimiento exacta que se desea buscar.
     * @return Specification<Employee> que aplica el filtro "EQUAL" en el atributo birthDate.
     */
    public static Specification<Employee> withBirthDate(Date birthDate) {
        return (root, query, builder) ->
                birthDate != null ? builder.equal(root.get("birthDate"), birthDate) : null;
    }
}
