package com.unir.employees.controller;

import com.unir.employees.data.EmployeeRepository;
import com.unir.employees.model.db.Employee;
import com.unir.employees.model.db.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    /**
     * Obtener empleados por apellido.
     * Si no se especifica apellido, se devuelven los primeros 20 empleados.
     *
     * @param lastName - apellido.
     * @return lista de empleados.
     */
    @GetMapping
    public List<Employee> getEmployees(
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "hireDate", required = false) String hireDate,
            @RequestParam(value = "hiredAfter", required = false) String hiredAfter,
            @RequestParam(value = "hiredBefore", required = false) String hiredBefore,
            @RequestParam(value = "empNoStart", required = false) String empNoStart) {

        List<String> top3DistinctFirstNameBy = employeeRepository.findTop3DistinctFirstNameBy();
        top3DistinctFirstNameBy.forEach(employee -> log.info("Top Name: {}", employee));

        if (StringUtils.hasText(firstName)) {
            int countByFirstName = employeeRepository.countByFirstName(firstName);
            log.info("Count by firstName: {}", countByFirstName);
        }

        if (StringUtils.hasText(hireDate)) {
            return employeeRepository.findByHireDate(java.sql.Date.valueOf(hireDate));
        } else if (StringUtils.hasText(hiredAfter) && StringUtils.hasText(hiredBefore)) {
            return employeeRepository.findByHireDateBetween(java.sql.Date.valueOf(hiredAfter), java.sql.Date.valueOf(hiredBefore));
        } else if (StringUtils.hasText(firstName) && StringUtils.hasText(lastName)) {
            return employeeRepository.findFirst5ByFirstNameContainingAndLastNameContaining(firstName, lastName);
        } else if (StringUtils.hasText(lastName)) {
            return employeeRepository.findByLastName(lastName);
        } else if (StringUtils.hasText(firstName)) {
            return employeeRepository.findByFirstNameContaining(firstName);
        } else {
            return employeeRepository.findAll().subList(0, 20);
        }
    }

    /**
     * Obtener el empleado mejor pagado de un departamento
     *
     * @param departmentName
     * @return
     */
    @GetMapping("/highestPaid")
    public Employee getHighestPaid(@RequestParam String departmentName) {
        return employeeRepository.findHighestPaidInDepartment(departmentName);
    }

    /**
     * Obtener el numero de empleados contratados entre dos fechas
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    @GetMapping("/count")
    public int countEmployees(
            @RequestParam(name = "fromDate", required = false) Date fromDate,
            @RequestParam(name = "toDate", required = false) Date toDate
    ) {
        // cogemos las fechas de los params, o las seteamos para comprender los ultimos 30 dias
        Date date1 = fromDate != null ? fromDate : Date.valueOf(LocalDate.now().minusDays(30));
        Date date2 = toDate != null ? toDate : (new Date(System.currentTimeMillis()));

        return employeeRepository.countByHireDateBetween(date1, date2);
    }

    /**
     * Obtener el primer empleado o empleada contratado
     *
     * @param gender
     * @return
     */
    @GetMapping("/senior")
    public Employee getSeniorEmployee(
            @RequestParam(name = "gender", required = false) String gender
    ) {

        Gender genderToSearch;

        try {
            genderToSearch = Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            Random rd = new Random();
            genderToSearch = Gender.values()[rd.nextInt(0, 2)];
        }

        return employeeRepository.findFirstByGenderOrderByHireDate(genderToSearch);
    }
}

