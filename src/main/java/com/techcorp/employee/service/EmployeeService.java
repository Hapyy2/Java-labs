package com.techcorp.employee.service;

import com.techcorp.employee.dto.CompanyStatisticsDTO;
import com.techcorp.employee.exception.DuplicateEmailException;
import com.techcorp.employee.exception.EmployeeNotFoundException;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.EmploymentStatus;
import com.techcorp.employee.model.Position;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmployeeService {

    private final List<Employee> employees = Collections.synchronizedList(new ArrayList<>());

    public Employee addEmployee(Employee employee) throws DuplicateEmailException {
        if (employee == null || employee.getEmail() == null) {
            throw new IllegalArgumentException("Employee or Email cannot be null");
        }

        if (findEmployeeByEmail(employee.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email " + employee.getEmail() + " already exists.");
        }

        employees.add(employee);
        return employee;
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }

    public Optional<Employee> findEmployeeByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return employees.stream()
                .filter(e -> e.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Employee updateEmployee(String email, Employee employeeDetails) throws EmployeeNotFoundException {
        Employee existingEmployee = findEmployeeByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));

        existingEmployee.setFirstName(employeeDetails.getFirstName());
        existingEmployee.setLastName(employeeDetails.getLastName());
        existingEmployee.setCompany(employeeDetails.getCompany());
        existingEmployee.setPosition(employeeDetails.getPosition());
        existingEmployee.setSalary(employeeDetails.getSalary());
        existingEmployee.setStatus(employeeDetails.getStatus());

        return existingEmployee;
    }

    public void deleteEmployee(String email) throws EmployeeNotFoundException {
        Employee employeeToRemove = findEmployeeByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));

        employees.remove(employeeToRemove);
    }

    public Employee updateEmployeeStatus(String email, EmploymentStatus status) throws EmployeeNotFoundException {
        Employee existingEmployee = findEmployeeByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));

        existingEmployee.setStatus(status);
        return existingEmployee;
    }

    public List<Employee> filterByCompany(String companyName) {
        return employees.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getCompany().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    public List<Employee> findEmployeesByStatus(EmploymentStatus status) {
        return employees.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Employee> sortByLastName() {
        return employees.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Employee::getLastName))
                .collect(Collectors.toList());
    }

    public Map<Position, Long> countEmployeesByPosition() {
        return employees.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Employee::getPosition,
                        Collectors.counting()
                ));
    }

    public Map<EmploymentStatus, Long> countEmployeesByStatus() {
        return employees.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Employee::getStatus,
                        Collectors.counting()
                ));
    }

    public double calculateAverageSalary(String companyName) {
        Stream<Employee> employeeStream = employees.stream().filter(Objects::nonNull);

        if (companyName != null && !companyName.isBlank()) {
            employeeStream = employeeStream.filter(e -> e.getCompany().equalsIgnoreCase(companyName));
        }

        return employeeStream
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public double calculateAverageSalary() {
        return calculateAverageSalary(null);
    }

    public Optional<Employee> getHighestPaidEmployee() {
        return employees.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(Employee::getSalary));
    }

    public List<Employee> validateSalaryConsistency() {
        return employees.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getSalary() < e.getPosition().getSalary())
                .collect(Collectors.toList());
    }

    public Map<String, CompanyStatisticsDTO> getCompanyStatistics() {
        return employees.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Employee::getCompany,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    long count = list.size();
                                    double avgSalary = list.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);

                                    Optional<Employee> topEarnerOpt = list.stream()
                                            .max(Comparator.comparingDouble(Employee::getSalary));

                                    String highestPaidName = topEarnerOpt.map(Employee::getFullName).orElse("Brak");
                                    double highestSalary = topEarnerOpt.map(Employee::getSalary).orElse(0.0);
                                    String companyName = list.isEmpty() ? "N/A" : list.get(0).getCompany();

                                    return new CompanyStatisticsDTO(companyName, count, avgSalary, highestSalary, highestPaidName);
                                }
                        )
                ));
    }

    public Map<String, Long> getEmployeeCountByPositionString() {
        return employees.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        e -> e.getPosition().name(),
                        Collectors.counting()
                ));
    }

    public Map<String, Long> getEmployeeCountByStatusString() {
        return employees.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        e -> e.getStatus().name(),
                        Collectors.counting()
                ));
    }
}