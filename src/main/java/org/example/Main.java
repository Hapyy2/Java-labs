package org.example;

import java.util.Optional;
import org.example.exception.ApiException;
import org.example.model.*;
import org.example.service.*;

public class Main {
    public static void main(String[] args) {
        EmployeeService manager = new EmployeeService();
        ImportService importService = new ImportService(manager);
        ApiService apiService = new ApiService();

        // Dodawanie danych początkowych (Zadanie 1)
        manager.addEmployee(new Employee("Anna", "Kowalska", "anna@tc.com", "TechCorp", Position.CEO));
        manager.addEmployee(new Employee("Piotr", "Nowak", "piotr@tc.com", "TechCorp", Position.PROGRAMMER));
        manager.addEmployee(new Employee("Janusz", "Kruk", "janusz@ds.com", "DataSoft", Position.MANAGER));
        manager.addEmployee(new Employee("Adam", "Zyś", "adam@tc.com", "TechCorp", Position.PROGRAMMER));
        manager.addEmployee(new Employee("Ewa", "Lis", "ewa@tc.com", "TechCorp", Position.INTERN));
        manager.addEmployee(new Employee("Test", "Duplikat", "anna@tc.com", "TechCorp", Position.INTERN));

        System.out.println("--- BAZOWE OPERACJE ---");
        manager.getAllEmployees().forEach(System.out::println);

        System.out.println("\n--- Filtr TechCorp ---");
        manager.filterByCompany("TechCorp").forEach(System.out::println);
        System.out.println("\n--- Sortowanie po nazwisku ---");
        manager.sortByLastName().forEach(System.out::println);

        System.out.println("\n--- Statystyki ---");
        manager.countEmployeesByPosition().forEach((pos, count) -> System.out.printf("%s: %d%n", pos.getName(), count));
        System.out.printf("Średnie wynagrodzenie: %.2f PLN%n", manager.calculateAverageSalary());
        Optional<Employee> highestPaid = manager.getHighestPaidEmployee();
        highestPaid.ifPresent(e -> System.out.println("Najlepiej opłacany: " + e.getFullName()));

        System.out.println("\n=======================================");

        // Import CSV (Zadanie 2)
        System.out.println("--- IMPORT CSV ---");
        try {
            ImportSummary summary = importService.importFromCsv("employees.csv");
            System.out.println(summary);
            System.out.printf("Pracowników po CSV: %d%n", manager.getAllEmployees().size());
        } catch (Exception e) {
            System.err.printf("Błąd CSV: %s%n", e.getMessage());
        }

        // Integracja z REST API (Zadanie 2)
        System.out.println("\n--- INTEGRACJA API ---");
        try {
            apiService.fetchEmployeesFromApi().forEach(manager::addEmployee);
            System.out.printf("Pracowników po API: %d%n", manager.getAllEmployees().size());
        } catch (ApiException e) {
            System.err.printf("Błąd API: %s%n", e.getMessage());
        }

        // Analityka rozszerzona (Zadanie 2)
        System.out.println("\n--- ANALITYKA FIRMOWA ---");
        manager.validateSalaryConsistency()
                .forEach(e -> System.out.println("Niespójna pensja: " + e));

        System.out.println("\n--- Statystyki Firm ---");
        manager.getCompanyStatistics()
                .forEach((company, stats) ->
                        System.out.printf("Firma '%s': %s%n", company, stats));
    }
}