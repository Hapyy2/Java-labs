package org.example;

import java.util.List;
import java.util.Optional;
import org.example.model.*;
import org.example.service.*;

public class Main {
    public static void main(String[] args) {
        EmployeeService manager = new EmployeeService();

        manager.addEmployee(new Employee("Anna", "Kowalska", "anna@tc.com", "TechCorp", Position.CEO));
        manager.addEmployee(new Employee("Piotr", "Nowak", "piotr@tc.com", "TechCorp", Position.PROGRAMMER));
        manager.addEmployee(new Employee("Janusz", "Kruk", "janusz@ds.com", "DataSoft", Position.MANAGER));
        manager.addEmployee(new Employee("Adam", "Zyś", "adam@tc.com", "TechCorp", Position.PROGRAMMER));
        manager.addEmployee(new Employee("Ewa", "Lis", "ewa@tc.com", "TechCorp", Position.INTERN));
        manager.addEmployee(new Employee("Test", "Duplikat", "anna@tc.com", "TechCorp", Position.INTERN)); // Walidacja niepowodzenie

        System.out.println("--- Wszyscy pracownicy ---");
        manager.getAllEmployees().forEach(System.out::println);

        System.out.println("\n--- Pracownicy TechCorp ---");
        manager.filterByCompany("TechCorp").forEach(System.out::println);

        System.out.println("\n--- Sortowanie po nazwisku ---");
        manager.sortByLastName().forEach(System.out::println);

        System.out.println("\n--- Grupowanie i Zliczanie ---");
        manager.countEmployeesByPosition().forEach((pos, count) ->
                System.out.printf("%s: %d%n", pos.getName(), count)
        );

        System.out.printf("\nŚrednie wynagrodzenie: %.2f PLN%n", manager.calculateAverageSalary());

        Optional<Employee> highestPaid = manager.getHighestPaidEmployee();
        highestPaid.ifPresent(e -> System.out.println("Najlepiej opłacany: " + e.getFullName() + " (" + e.getSalary() + " PLN)"));
    }
}