package com.techcorp.employee.controller;

import com.techcorp.employee.dto.CompanyStatisticsDTO;
import com.techcorp.employee.exception.EmployeeNotFoundException;
import com.techcorp.employee.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final EmployeeService employeeService;

    public StatisticsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/salary/average")
    public ResponseEntity<Map<String, Double>> getAverageSalary(
            @RequestParam(required = false) String company) {

        double avg = employeeService.calculateAverageSalary(company);
        return ResponseEntity.ok(Map.of("averageSalary", avg));
    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<CompanyStatisticsDTO> getCompanyStatistics(@PathVariable String companyName) {
        CompanyStatisticsDTO stats = employeeService.getCompanyStatistics().get(companyName);

        if (stats == null) {
            throw new EmployeeNotFoundException("Nie znaleziono statystyk dla firmy: " + companyName);
        }
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/positions")
    public ResponseEntity<Map<String, Long>> getPositionCounts() {
        return ResponseEntity.ok(employeeService.getEmployeeCountByPositionString());
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Long>> getStatusCounts() {
        return ResponseEntity.ok(employeeService.getEmployeeCountByStatusString());
    }
}