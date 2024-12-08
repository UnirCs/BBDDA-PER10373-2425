package com.unir.employees.service;

import com.unir.employees.data.*;
import com.unir.employees.model.db.*;
import com.unir.employees.model.request.PromotionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final TitlesRepository titleRepository;
    private final SalariesRepository salaryRepository;
    private final DeptEmpRepository deptEmpRepository;
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Transactional
    public String promote(PromotionRequest promotionRequest) throws ParseException {

        Optional<Employee> employee = employeeRepository.findById(promotionRequest.getEmployeeId());
        Optional<Department> department = departmentRepository.findById(promotionRequest.getDepartmentId());

        if(employee.isPresent() && department.isPresent()) {

            Optional<DeptEmp> deptEmp = deptEmpRepository.findDeptEmpByEmployeeId(promotionRequest.getEmployeeId());
            Optional<Salary> salary = salaryRepository.findCurrentSalaryByEmployeeId(promotionRequest.getEmployeeId());
            Optional<Title> title = titleRepository.findTitleEmpByEmployeeId(promotionRequest.getEmployeeId());

            if(deptEmp.isPresent() && salary.isPresent() && title.isPresent()){

                Date newFromDate = promotionRequest.getFromDate();

                // el salario no puede modificarse más de un 15% en una promoción
                if(promotionRequest.getSalary() <= salary.get().getSalary() * 1.15){
                    //Creamos nuevos objetos que representen el nuevo título, salario y departamento del empleado.
                    // Usaremos para el atributo toDate el mismo valor que había previamente en los registros que modificamos.

                    // Manejar el título (Title)
                    Title currentTitle = title.get(); // Obtener el registro actual

                    // Crear la clave compuesta del título existente
                    TitleId titleId = new TitleId(currentTitle.getEmployee().getEmpNo(), currentTitle.getTitle(), currentTitle.getFromDate());

                    // Eliminar el registro anterior
                    titleRepository.deleteById(titleId);

                    // Crear el nuevo registro del título
                    Title newTitle = new Title();
                    newTitle.setEmployee(currentTitle.getEmployee());
                    newTitle.setTitle(promotionRequest.getTitle()); // Usar el nuevo título
                    newTitle.setFromDate(newFromDate);
                    newTitle.setToDate(currentTitle.getToDate());
                    titleRepository.save(newTitle); // Guardar el nuevo registro

                    // Manejar el salario (Salary)
                    Salary currentSalary = salary.get(); // Obtener el registro actual

                    // Crear la clave compuesta del salario existente
                    SalaryId salaryId = new SalaryId(currentSalary.getEmployee().getEmpNo(), currentSalary.getFromDate());

                    // Eliminar el registro anterior
                    salaryRepository.deleteById(salaryId);

                    // Crear el nuevo registro del salario
                    Salary newSalary = new Salary();
                    newSalary.setEmployee(currentSalary.getEmployee());
                    newSalary.setSalary(promotionRequest.getSalary()); // Usar el nuevo salario
                    newSalary.setFromDate(newFromDate);
                    newSalary.setToDate(currentSalary.getToDate());
                    salaryRepository.save(newSalary); // Guardar el nuevo registro

                    // Manejar el departamento (DeptEmp)
                    DeptEmp currentDeptEmp = deptEmp.get(); // Obtener el registro actual

                    // Eliminar el registro actual (si aplica) para evitar conflictos
                    DeptEmpId deptEmpId = new DeptEmpId(currentDeptEmp.getEmployee().getEmpNo(), currentDeptEmp.getDepartment().getDeptName());
                    deptEmpRepository.deleteById(deptEmpId);

                    // Crear un nuevo registro para el departamento
                    DeptEmp newDeptEmp = new DeptEmp();
                    newDeptEmp.setEmployee(currentDeptEmp.getEmployee());
                    newDeptEmp.setDepartment(currentDeptEmp.getDepartment()); // Usar el nuevo departamento
                    newDeptEmp.setFromDate(newFromDate);
                    newDeptEmp.setToDate(currentDeptEmp.getToDate());
                    deptEmpRepository.save(newDeptEmp); // Guardar el nuevo registro


                }else{
                    throw new RuntimeException();
                }
            }else{
                return "No existe :(";
            }
        } else {
            //no existe alguno de los dos
            return "No existe :(";
        }

        return "Promocionado :D";
    }

}
