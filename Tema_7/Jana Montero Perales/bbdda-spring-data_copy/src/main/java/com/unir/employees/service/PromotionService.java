package com.unir.employees.service;

import com.unir.employees.data.*;
import com.unir.employees.model.db.Department;
import com.unir.employees.model.db.DeptEmp;
import com.unir.employees.model.db.Salary;
import com.unir.employees.model.db.Title;
import com.unir.employees.model.request.PromotionRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final TitleRepository titleRepository;
    private final SalaryRepository salaryRepository;
    private final DeptEmpRepository deptEmpRepository;
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Transactional
    public String promote(PromotionRequest promotionRequest) throws ParseException, BadRequestException {

        // validamos el input
        validatePromotionRequest(promotionRequest);

        // obtemos empleado y departamento
        Integer employeeId = promotionRequest.getEmployeeId();
        employeeRepository.findById(employeeId).orElseThrow(BadRequestException::new);
        Department department = departmentRepository.findById(promotionRequest.getDepartmentId()).orElseThrow(BadRequestException::new);

        // obtenemos departamento, salario y título actuales
        DeptEmp currentDepartment = deptEmpRepository.findFirstByEmployeeEmpNoOrderByFromDateDesc(employeeId).orElseThrow(BadRequestException::new);
        Salary currentSalary = salaryRepository.findFirstByEmployeeEmpNoOrderByFromDateDesc(employeeId).orElseThrow(BadRequestException::new);
        Title currentTitle = titleRepository.findFirstByEmployeeEmpNoOrderByFromDateDesc(employeeId).orElseThrow(BadRequestException::new);

        // creamos nuevo puesto
        DeptEmp newDepartment = createNewDeptEmp(promotionRequest, currentDepartment, department);
        Title newTitle = createNewTitle(promotionRequest, currentTitle);
        Salary newSalary = createNewSalary(promotionRequest, currentSalary);

        // actualizamos fechas
        updateEndDateOfCurrentData(promotionRequest, currentDepartment, currentSalary, currentTitle);

        // guardamos nuevo puesto en BD
        deptEmpRepository.save(newDepartment);
        titleRepository.save(newTitle);
        salaryRepository.save(newSalary);

        return "Promocionado :D";
    }

    @Transactional
    protected void updateEndDateOfCurrentData(PromotionRequest promotionRequest, DeptEmp currentDepartment, Salary currentSalary, Title currentTitle) {

        Date promotionDate = new Date(promotionRequest.getFromDate().getTime());
        if (currentDepartment != null) {
            currentDepartment.setToDate(promotionDate);
            deptEmpRepository.save(currentDepartment);
        }

        if (currentTitle != null) {
            currentTitle.setToDate(promotionDate);
            titleRepository.save(currentTitle);
        }

        if (currentSalary != null) {
            currentSalary.setToDate(promotionDate);
            salaryRepository.save(currentSalary);
        }

        // comprobamos restricción de salario
        if (currentSalary != null && promotionRequest.getSalary() > currentSalary.getSalary() * 1.15) {
            throw new RuntimeException("We are too cheap and greedy for such a promotion");
        }
    }

    private Salary createNewSalary(PromotionRequest promotionRequest, Salary currentSalary) {
        return new Salary(
                currentSalary.getEmployee(),
                new Date(promotionRequest.getFromDate().getTime()),
                currentSalary.getToDate(),
                promotionRequest.getSalary()
        );
    }

    private Title createNewTitle(PromotionRequest promotionRequest, Title currentTitle) {
        return new Title(
                currentTitle.getEmployee(),
                promotionRequest.getTitle(),
                new Date(promotionRequest.getFromDate().getTime()),
                currentTitle.getToDate()
        );
    }

    private DeptEmp createNewDeptEmp(PromotionRequest promotionRequest, DeptEmp currentDepartment, Department newDepartment) {
        return new DeptEmp(
                currentDepartment.getEmployee(),
                newDepartment,
                new Date(promotionRequest.getFromDate().getTime()),
                currentDepartment.getToDate()
        );
    }

    private void validatePromotionRequest(PromotionRequest promotionRequest) throws BadRequestException {
        if (promotionRequest.getEmployeeId() == null)
            throw new BadRequestException("El campo employeeId es obligatorio");
        if (promotionRequest.getDepartmentId() == null)
            throw new BadRequestException("El campo departmentId es obligatorio");
        if (promotionRequest.getFromDate() == null) throw new BadRequestException("El campo fromDate es obligatorio");
    }
}
