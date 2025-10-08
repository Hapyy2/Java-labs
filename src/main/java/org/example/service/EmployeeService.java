package org.example.service;

import org.example.model.Position;
import org.example.model.Employee;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Map;

public class EmployeeService {
    private final List<Employee> employees = new ArrayList<>();

    public boolean addEmployee(Employee employee) {
        boolean emailExists = employees.stream()
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
                .filter(e -> e.getCompany().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    public List<Employee> sortByLastName() {
        return employees.stream()
                .sorted(Comparator.comparing(Employee::getSurname))
                .collect(Collectors.toList());
    }

    public Map<Position, List<Employee>> groupByPosition() {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getPosition));
    }

    public Map<Position, Long> countEmployeesByPosition() {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getPosition,
                        Collectors.counting()
                ));
    }

    public double calculateAverageSalary() {
        return employees.stream()
                .collect(Collectors.averagingDouble(Employee::getSalary));
    }

    public Optional<Employee> getHighestPaidEmployee() {
        return employees.stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
    }
}