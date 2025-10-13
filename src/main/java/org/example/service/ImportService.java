package org.example.service;

import org.example.exception.InvalidDataException;
import org.example.model.Employee;
import org.example.model.ImportSummary;
import org.example.model.Position;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportService {
    private final EmployeeService employeeService;

    public ImportService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public ImportSummary importFromCsv(String filePath) throws IOException {
        int importedCount = 0;
        List<String> errors = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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

                    Employee employee = new Employee(firstName, lastName, email, company, position);

                    if (employeeService.addEmployee(employee)) {
                        importedCount++;
                    } else {
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