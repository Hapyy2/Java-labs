package com.techcorp.employee.service;

import com.techcorp.employee.exception.DuplicateEmailException;
import com.techcorp.employee.exception.InvalidDataException;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.ImportSummary;
import com.techcorp.employee.model.Position;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {
    private final EmployeeService employeeService;
    private final String csvFilePath;

    public ImportService(EmployeeService employeeService,
                         @Value("${app.import.csv-file}") String csvFilePath) {
        this.employeeService = employeeService;
        this.csvFilePath = csvFilePath;
    }

    public ImportSummary importFromCsv() throws IOException {
        int importedCount = 0;
        List<String> errors = new ArrayList<>();
        int lineNumber = 0;

        ClassPathResource resource = new ClassPathResource(csvFilePath.replace("classpath:", ""));

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            if (br.readLine() != null) {
                lineNumber++;
            }

            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] data = line.split(",", -1);
                    if (data.length < 6) {
                        throw new InvalidDataException("Za mało kolumn w wierszu (wymagane 6).");
                    }

                    String firstName = data[0].trim();
                    String lastName = data[1].trim();
                    String email = data[2].trim();
                    String company = data[3].trim();
                    String positionName = data[4].trim().toUpperCase();
                    double salaryFromCsv = Double.parseDouble(data[5].trim());

                    Position position;
                    try {
                        position = Position.valueOf(positionName);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidDataException("Nieznane stanowisko: " + positionName);
                    }

                    if (salaryFromCsv <= 0) {
                        throw new InvalidDataException("Wynagrodzenie musi być dodatnie: " + salaryFromCsv);
                    }

                    Employee employee = new Employee(firstName, lastName, email, company, position, salaryFromCsv);

                    if (employee.getSalary() < employee.getPosition().getSalary()) {
                        errors.add(String.format(
                                "Linia %d: Ostrzeżenie - pensja (%.2f) niższa niż bazowa (%.2f) dla %s. Pracownik dodany.",
                                lineNumber, employee.getSalary(), employee.getPosition().getSalary(), positionName
                        ));
                    }

                    try {
                        employeeService.addEmployee(employee);
                        importedCount++;
                    } catch (DuplicateEmailException e) {
                        errors.add(String.format("Linia %d: Duplikat emaila '%s'.", lineNumber, email));
                    }

                } catch (InvalidDataException e) {
                    errors.add(String.format("Linia %d: Błąd danych - %s", lineNumber, e.getMessage()));
                } catch (NumberFormatException e) {
                    errors.add(String.format("Linia %d: Błąd formatu liczby dla wynagrodzenia.", lineNumber));
                } catch (Exception e) {
                    errors.add(String.format("Linia %d: Nieoczekiwany błąd - %s", lineNumber, e.getMessage()));
                }
            }
        }
        return new ImportSummary(importedCount, errors);
    }
}