package com.unir.employees.services;

import com.unir.employees.dao.PromotionRequest;
import com.unir.employees.dao.PromotionResponse;
import com.unir.employees.data.*;
import com.unir.employees.model.db.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final TitleRepository titleRepository;
    private final DeptEmptRepository deptEmptRepository;
    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public PromotionResponse promote(PromotionRequest request) throws BadRequestException {

        validateRequest(request);

        Employee employee = employeeRepository.findById(request.employeeId()).orElseThrow(BadRequestException::new);
        Department department = departmentRepository.findById(request.newDepartmentId()).orElseThrow(BadRequestException::new);

        Optional<Title> oldTitle = titleRepository.findByEmpNo(employee.getEmpNo());
        Optional<Salary> oldSalary = salaryRepository.findByEmpNo(employee.getEmpNo());
        Optional<DeptEmp> oldDeptEmp = deptEmptRepository.findByEmpNo(employee.getEmpNo());

        if (!oldTitle.isPresent()) {
            throw new BadRequestException();
        }

        if (!oldSalary.isPresent()) {
            throw new BadRequestException();
        }

        if (!oldDeptEmp.isPresent()) {
            throw new BadRequestException();
        }

        Integer oldSalaryAmount = oldSalary.get().getSalary();
        Double maxSalaryAmount = oldSalaryAmount * 1.15;
        if (request.newSalary() > maxSalaryAmount) {
            throw new BadRequestException("Salary exceeds max oldSalary (must be < 15%)");
        }

        Title newTitle = new Title();
        newTitle.setTitle(request.newTitle());
        newTitle.setEmpNo(employee);
        newTitle.setFromDate(request.fromDate());
        newTitle.setToDate(request.fromDate());
        titleRepository.save(newTitle);

        oldTitle.get().setToDate(request.fromDate());
        titleRepository.save(oldTitle.get());

        Salary newSalary = new Salary();
        newSalary.setEmpNo(employee);
        newSalary.setFromDate(request.fromDate());
        newSalary.setToDate(request.fromDate());
        newSalary.setSalary(request.newSalary());
        salaryRepository.save(newSalary);

        Department newDepartment = new Department();
        newDepartment.setDeptNo(department.getDeptNo());
        newDepartment.setDeptName(department.getDeptName());
        departmentRepository.save(newDepartment);

        DeptEmp newDeptEmp = new DeptEmp();
        newDeptEmp.setEmployee(employee);
        newDeptEmp.setDepartment(newDepartment);
        newDeptEmp.setToDate(oldDeptEmp.get().getToDate());
        newDeptEmp.setEmployee(oldDeptEmp.get().getEmployee());
        newDeptEmp.setFromDate(request.fromDate());
        deptEmptRepository.save(newDeptEmp);


        PromotionResponse promotionResponse = new PromotionResponse(request.employeeId(), newDepartment.getDeptNo(),
                newTitle.getTitle(), newSalary.getSalary(), request.fromDate());
        return promotionResponse;
    }

    private void validateRequest(PromotionRequest request) {
        if (request.employeeId() == null) {
            throw new IllegalArgumentException("Employee id cannot be null");
        }

        if (request.fromDate() == null) {
            throw new IllegalArgumentException("From date cannot be null");
        }

        if (request.newDepartmentId() == null) {
            throw new IllegalArgumentException("New department id cannot be null");
        }
    }
}
