package com.unir.dao;

import com.unir.model.mysql.Department;
import com.unir.model.mysql.DeptEmployee;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Slf4j
public class DepartmentsDao {

    private final Session session;

    /**
     * Consulta de todos los departamentos de la base de datos
     * * Se puede hacer de dos formas:
     * * 1. Con SQL nativo
     * * 2. Con HQL: https://docs.jboss.org/hibernate/orm/3.5/reference/es-ES/html/queryhql.html
     * @return Lista de departamentos
     * @throws SQLException Excepción en caso de error
     */
    public List<Department> findAll() throws SQLException {
        List<Department> departments = session.createQuery("from Department", Department.class).list();
        for(Department department : departments) {
            //Podemos cambiar el fetch de Department para ver los cambios. Por defecto es LAZY
            Set<DeptEmployee> employeesOfDepartment = department.getDeptEmployees();
            log.info("Departamento: {} tiene {} empleados", department.getDeptName(), employeesOfDepartment.size());
        }
        return departments;
    }

    /**
     * Consulta de un departamento por su identificador.
     * @param id Identificador del departamento
     * @return Departamento
     * @throws SQLException Excepción en caso de error
     */
    public Department findById(String id) throws SQLException {
        return session.get(Department.class, id);
    }

    /**
     * Inserta un nuevo departamento en la base de datos.
     * @param department Departamento a insertar
     * @return Departamento insertado
     * @throws SQLException Excepción en caso de error
     */
    public Department save(Department department) throws SQLException {
        session.persist(department);
        return department;
    }

    /**
     * Inserta un nuevo departamento en la base de datos si no existe.
     * Si existe, actualiza el departamento.
     *
     * Utilizamos la operacion merge de la sesion de Hibernate.
     * Merge de Hibernate funciona de la siguiente forma:
     * 1. Se comprueba que la entidad no exista en la sesion de Hibernate.
     * 2. Se hace una consulta a la base de datos para comprobar si la entidad existe.
     * 3. Si no existe, se inserta.
     * 4. Si existe, se actualiza.
     *
     * @param department Departamento a insertar o actualizar
     * @return Departamento insertado o actualizado
     * @throws SQLException Excepción en caso de error
     */
    public Department saveOrUpdate(Department department) throws SQLException {
        session.merge(department);
        return department;
    }
}
