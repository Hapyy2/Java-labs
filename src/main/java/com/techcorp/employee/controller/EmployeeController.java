package com.techcorp.employee.controller;

import com.techcorp.employee.dto.EmployeeDTO;
import com.techcorp.employee.exception.DuplicateEmailException;
import com.techcorp.employee.exception.EmployeeNotFoundException;
import com.techcorp.employee.exception.InvalidDataException;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.EmploymentStatus;
import com.techcorp.employee.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getEmployees(
            @RequestParam(required = false) String company) {

        List<Employee> employees;
        if (company != null && !company.isBlank()) {
            employees = employeeService.filterByCompany(company);
        } else {
            employees = employeeService.getAllEmployees();
        }

        List<EmployeeDTO> dtos = employees.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{email}")
    public ResponseEntity<EmployeeDTO> getEmployeeByEmail(@PathVariable String email) {
        Employee employee = employeeService.findEmployeeByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Nie znaleziono pracownika o emailu: " + email));
        return ResponseEntity.ok(toDTO(employee));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByStatus(@PathVariable String status) {
        try {
            EmploymentStatus statusEnum = EmploymentStatus.valueOf(status.toUpperCase());
            List<Employee> employees = employeeService.findEmployeesByStatus(statusEnum);
            List<EmployeeDTO> dtos = employees.stream().map(this::toDTO).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Nieprawidłowy status: " + status + ". Dostępne: ACTIVE, ON_LEAVE, TERMINATED");
        }
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) throws DuplicateEmailException {
        Employee employee = toModel(employeeDTO);
        Employee createdEmployee = employeeService.addEmployee(employee);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{email}")
                .buildAndExpand(createdEmployee.getEmail())
                .toUri();

        return ResponseEntity.created(location).body(toDTO(createdEmployee));
    }

    @PutMapping("/{email}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable String email, @RequestBody EmployeeDTO employeeDTO) throws EmployeeNotFoundException {
        if (employeeDTO.getEmail() != null && !email.equalsIgnoreCase(employeeDTO.getEmail())) {
            throw new InvalidDataException("Email w ścieżce (" + email + ") nie zgadza się z emailem w ciele żądania (" + employeeDTO.getEmail() + ").");
        }

        Employee employeeModel = toModel(employeeDTO);
        Employee updatedEmployee = employeeService.updateEmployee(email, employeeModel);

        return ResponseEntity.ok(toDTO(updatedEmployee));
    }

    @PatchMapping("/{email}/status")
    public ResponseEntity<EmployeeDTO> updateEmployeeStatus(@PathVariable String email, @RequestBody Map<String, String> statusUpdate) throws EmployeeNotFoundException {
        String statusStr = statusUpdate.get("status");
        if (statusStr == null) {
            throw new InvalidDataException("Oczekiwano obiektu JSON z kluczem 'status', np: {\"status\":\"ON_LEAVE\"}");
        }

        try {
            EmploymentStatus newStatus = EmploymentStatus.valueOf(statusStr.toUpperCase());
            Employee updatedEmployee = employeeService.updateEmployeeStatus(email, newStatus);
            return ResponseEntity.ok(toDTO(updatedEmployee));
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Nieprawidłowy status: " + statusStr + ". Dostępne: ACTIVE, ON_LEAVE, TERMINATED");
        }
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String email) throws EmployeeNotFoundException {
        employeeService.deleteEmployee(email);
        return ResponseEntity.noContent().build();
    }

    private EmployeeDTO toDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getCompany(),
                employee.getPosition(),
                employee.getSalary(),
                employee.getStatus()
        );
    }

    private Employee toModel(EmployeeDTO dto) {
        Employee emp = new Employee(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getCompany(),
                dto.getPosition(),
                dto.getSalary()
        );
        emp.setStatus(dto.getStatus() != null ? dto.getStatus() : EmploymentStatus.ACTIVE);
        return emp;
    }
}