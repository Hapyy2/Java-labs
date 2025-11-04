package org.example;

import org.example.exception.ApiException;
import org.example.model.Employee;
import org.example.model.ImportSummary;
import org.example.service.ApiService;
import org.example.service.EmployeeService;
import org.example.service.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.List;

@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class EmployeeManagementApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(EmployeeManagementApplication.class);

    private final EmployeeService employeeService;
    private final ImportService importService;
    private final ApiService apiService;
    private final List<Employee> xmlEmployees;

    public EmployeeManagementApplication(EmployeeService employeeService,
                                         ImportService importService,
                                         ApiService apiService,
                                         @Qualifier("xmlEmployees") List<Employee> xmlEmployees) {
        this.employeeService = employeeService;
        this.importService = importService;
        this.apiService = apiService;
        this.xmlEmployees = xmlEmployees;
    }

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("--- START APLIKACJI ---");
        int initialCount = employeeService.getAllEmployees().size();
        log.info("Początkowa liczba pracowników: {}", initialCount);

        log.info("\n--- IMPORT CSV ---");
        try {
            ImportSummary summary = importService.importFromCsv();
            log.info(summary.toString());
            log.info("Pracowników po CSV: {}", employeeService.getAllEmployees().size());
            if (!summary.getErrors().isEmpty()) {
                summary.getErrors().forEach(log::warn);
            }
        } catch (Exception e) {
            log.error("Krytyczny błąd importu CSV: {}", e.getMessage(), e);
        }

        log.info("\n--- IMPORT XML BEANS ---");
        int xmlImportCount = 0;
        for (Employee emp : xmlEmployees) {
            if (employeeService.addEmployee(emp)) {
                xmlImportCount++;
            } else {
                log.warn("Duplikat emaila z XML: {}", emp.getEmail());
            }
        }
        log.info("Dodano {} pracowników zdefiniowanych w XML.", xmlImportCount);
        log.info("Pracowników po XML: {}", employeeService.getAllEmployees().size());


        log.info("\n--- INTEGRACJA API ---");
        try {
            List<Employee> apiEmployees = apiService.fetchEmployeesFromApi();
            int apiImportCount = 0;
            for(Employee emp : apiEmployees) {
                if(employeeService.addEmployee(emp)) {
                    apiImportCount++;
                } else {
                    log.warn("Duplikat emaila z API: {}", emp.getEmail());
                }
            }
            log.info("Dodano {} pracowników z API.", apiImportCount);
            log.info("Łączna liczba pracowników: {}", employeeService.getAllEmployees().size());
        } catch (ApiException e) {
            log.error("Błąd pobierania danych z API: {}", e.getMessage(), e);
        }

        log.info("\n--- ANALITYKA FIRMOWA ---");

        log.info("\n--- Walidacja pensji (poniżej bazowej) ---");
        List<Employee> inconsistentSalaries = employeeService.validateSalaryConsistency();
        if (inconsistentSalaries.isEmpty()) {
            log.info("Wszystkie pensje są zgodne (równe lub wyższe niż bazowe).");
        } else {
            inconsistentSalaries.forEach(e -> log.warn(
                    "Niespójna pensja: {} (Stanowisko: {}, Bazowa: {}, Posiada: {})",
                    e.getFullName(), e.getPosition().getName(), e.getPosition().getSalary(), e.getSalary()
            ));
        }


        log.info("\n--- Statystyki Firm ---");
        employeeService.getCompanyStatistics()
                .forEach((company, stats) ->
                        log.info("Firma '{}': {}", company, stats.toString())
                );

        log.info("\n--- KONIEC DEMONSTRACJI ---");
    }
}

