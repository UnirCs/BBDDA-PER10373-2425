package com.unir.employees.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unir.employees.data.DepartmentRepository;
import com.unir.employees.data.DeptEmptRepository;
import com.unir.employees.data.EmployeeRepository;
import com.unir.employees.data.SalaryRepository;
import com.unir.employees.data.TitleRepository;
import com.unir.employees.model.db.Department;
import com.unir.employees.model.db.DeptEmp;
import com.unir.employees.model.db.Salaries;
import com.unir.employees.model.db.Titles;
import com.unir.employees.model.request.PromotionRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionsService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final TitleRepository titleRepository;
    private final SalaryRepository salaryRepository;
    private final DeptEmptRepository deptEmpRepository;
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Transactional
    public String promote(PromotionRequest promotionRequest) throws Exception {
        //Logica de negocio...
        if (promotionRequest.getEmployeeId() == null || promotionRequest.getDepartmentId() == null) {
            throw new Exception("Los datos de entrada no son correctos");
        }

        Optional<Titles> title = titleRepository.getTitleById(promotionRequest.getEmployeeId());
        Optional<Salaries> salary = salaryRepository.getSalaryById(promotionRequest.getEmployeeId());
        Optional<DeptEmp> deptEmp = deptEmpRepository.getDeptById(promotionRequest.getEmployeeId());

        if (title.isEmpty() || salary.isEmpty() || deptEmp.isEmpty()) {
            throw new Exception("Los datos de entrada no son correctos");
        }

        Titles newTitle = new Titles();
        newTitle.setFromDate(promotionRequest.getFromDate());
        newTitle.setEmpNo(title.get().getEmpNo());
        newTitle.setFromDate(title.get().getFromDate());
        newTitle.setToDate(title.get().getToDate());

        Salaries newSalary = new Salaries();
        newSalary.setEmpNo(salary.get().getEmpNo());
        newSalary.setFromDate(salary.get().getFromDate());
        newSalary.setSalary(salary.get().getSalary());
        newSalary.setFromDate(promotionRequest.getFromDate());
        newSalary.setToDate(salary.get().getToDate());

        DeptEmp newDeptEmp = new DeptEmp();
        Department newDepartment = new Department();
        newDepartment.setDeptNo(deptEmp.get().getDepartment().getDeptNo());
        newDepartment.setDeptEmps(deptEmp.get().getDepartment().getDeptEmps());
        newDepartment.setDeptManagers(deptEmp.get().getDepartment().getDeptManagers());
        newDepartment.setDeptName(deptEmp.get().getDepartment().getDeptName());

        newDeptEmp.setDepartment(newDepartment);
        newDeptEmp.setToDate(deptEmp.get().getToDate());
        newDeptEmp.setEmployee(deptEmp.get().getEmployee());
        newDeptEmp.setFromDate(promotionRequest.getFromDate());

        int incremento = (newSalary.getSalary() * 15) / 100; // Cálculo del 15%
        int valorFinal = newSalary.getSalary() + incremento;

        if(promotionRequest.getSalary() > valorFinal){
            throw new RuntimeException();
        }

        newTitle.setTitle(promotionRequest.getTitle());
        newSalary.setSalary(promotionRequest.getSalary());
        Department department = newDeptEmp.getDepartment();
        department.setDeptNo(promotionRequest.getDepartmentId());
        newDeptEmp.setDepartment(department);

        titleRepository.save(newTitle);
        salaryRepository.save(newSalary);
        deptEmpRepository.save(newDeptEmp);

        return "Promocionado :D";
    }
}