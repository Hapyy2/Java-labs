package org.example.service;

import org.example.model.Position;
import org.example.model.Employee;
import org.example.model.CompanyStatistics;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeService {
    private final List<Employee> employees = new ArrayList<>();

    public boolean addEmployee(Employee employee) {
        boolean emailExists = employees.stream()
                .filter(Objects::nonNull)
                .anyMatch(e -> e.getEmail().equalsIgnoreCase(employee.getEmail()));

        if (emailExists) {
            return false;
        }
        employees.add(employee);
        return true;
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }

    public List<Employee> filterByCompany(String companyName) {
        return employees.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getCompany().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    public List<Employee> sortByLastName() {
        return employees.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Employee::getLastName))
                .collect(Collectors.toList());
    }

    public Map<Position, List<Employee>> groupByPosition() {
        return employees.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Employee::getPosition));
    }

    public Map<Position, Long> countEmployeesByPosition() {
        return employees.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Employee::getPosition,
                        Collectors.counting()
                ));
    }

    public double calculateAverageSalary() {
        return employees.stream()
                .filter(Objects::nonNull)
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    public Optional<Employee> getHighestPaidEmployee() {
        return employees.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(Employee::getSalary));
    }

    // === Metody z Zadania 2 ===

    public List<Employee> validateSalaryConsistency() {
        return employees.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getSalary() < e.getPosition().getSalary())
                .collect(Collectors.toList());
    }

    public Map<String, CompanyStatistics> getCompanyStatistics() {
        return employees.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Employee::getCompany,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    long count = list.size();
                                    double avgSalary = list.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);

                                    String highestPaidName = list.stream()
                                            .max(Comparator.comparingDouble(Employee::getSalary))
                                            .map(Employee::getFullName)
                                            .orElse("Brak");

                                    return new CompanyStatistics(count, avgSalary, highestPaidName);
                                }
                        )
                ));
    }
}