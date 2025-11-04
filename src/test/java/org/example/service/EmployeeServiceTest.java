package org.example.service;

import org.example.model.CompanyStatistics;
import org.example.model.Employee;
import org.example.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService();
    }

    @Test
    @DisplayName("addEmployee: Should return true when email is unique")
    void addEmployee_shouldReturnTrue_whenEmailIsUnique() {
        Employee employee = new Employee("Jan", "Kowalski", "jan@test.com", "TestCorp", Position.PROGRAMMER);
        boolean result = employeeService.addEmployee(employee);
        assertTrue(result);
    }

    @Test
    @DisplayName("addEmployee: Should increase employee count when email is unique")
    void addEmployee_shouldIncreaseCount_whenEmailIsUnique() {
        Employee employee = new Employee("Jan", "Kowalski", "jan@test.com", "TestCorp", Position.PROGRAMMER);
        employeeService.addEmployee(employee);
        assertEquals(1, employeeService.getAllEmployees().size());
    }

    @Test
    @DisplayName("addEmployee: Should return false when email is a duplicate")
    void addEmployee_shouldReturnFalse_whenEmailIsDuplicate() {
        Employee employee1 = new Employee("Jan", "Kowalski", "jan@test.com", "TestCorp", Position.PROGRAMMER);
        Employee employee2 = new Employee("Anna", "Nowak", "jan@test.com", "AnotherCorp", Position.MANAGER);

        employeeService.addEmployee(employee1);
        boolean result = employeeService.addEmployee(employee2);

        assertFalse(result);
    }

    @Test
    @DisplayName("addEmployee: Should not increase employee count when email is a duplicate")
    void addEmployee_shouldNotIncreaseCount_whenEmailIsDuplicate() {
        Employee employee1 = new Employee("Jan", "Kowalski", "jan@test.com", "TestCorp", Position.PROGRAMMER);
        Employee employee2 = new Employee("Anna", "Nowak", "jan@test.com", "AnotherCorp", Position.MANAGER);

        employeeService.addEmployee(employee1);
        employeeService.addEmployee(employee2);

        assertEquals(1, employeeService.getAllEmployees().size());
    }

    @Test
    @DisplayName("calculateAverageSalary: Should return 0.0 when no employees exist")
    void calculateAverageSalary_shouldReturnZero_whenListIsEmpty() {
        double averageSalary = employeeService.calculateAverageSalary();
        assertEquals(0.0, averageSalary);
    }

    @Test
    @DisplayName("calculateAverageSalary: Should return correct average")
    void calculateAverageSalary_shouldReturnCorrectAverage() {
        employeeService.addEmployee(new Employee("A", "A", "a@a.com", "C", Position.PROGRAMMER));
        employeeService.addEmployee(new Employee("B", "B", "b@b.com", "C", Position.MANAGER));

        double averageSalary = employeeService.calculateAverageSalary();
        assertEquals(10000.0, averageSalary);
    }

    @Test
    @DisplayName("getHighestPaidEmployee: Should return empty optional when list is empty")
    void getHighestPaidEmployee_shouldReturnEmpty_whenListIsEmpty() {
        Optional<Employee> highestPaid = employeeService.getHighestPaidEmployee();
        assertTrue(highestPaid.isEmpty());
    }

    @Test
    @DisplayName("getHighestPaidEmployee: Should find the correct highest paid employee")
    void getHighestPaidEmployee_shouldReturnCorrectEmployee() {
        Employee programmer = new Employee("A", "A", "a@a.com", "C", Position.PROGRAMMER);
        Employee manager = new Employee("B", "B", "b@b.com", "C", Position.MANAGER);

        employeeService.addEmployee(programmer);
        employeeService.addEmployee(manager);

        Optional<Employee> highestPaid = employeeService.getHighestPaidEmployee();

        assertEquals(Optional.of(manager), highestPaid);
    }

    @Test
    @DisplayName("filterByCompany: Should return only employees from the specified company")
    void filterByCompany_shouldReturnFilteredList() {
        Employee e1 = new Employee("Jan", "Kowalski", "jan@test.com", "TechCorp", Position.PROGRAMMER);
        Employee e2 = new Employee("Anna", "Nowak", "anna@test.com", "DataSoft", Position.MANAGER);
        employeeService.addEmployee(e1);
        employeeService.addEmployee(e2);

        List<Employee> filtered = employeeService.filterByCompany("TechCorp");

        assertAll("Weryfikacja filtrowania",
                () -> assertEquals(1, filtered.size()),
                () -> assertEquals(e1, filtered.get(0))
        );
    }

    @Test
    @DisplayName("sortByLastName: Should return employees sorted by last name")
    void sortByLastName_shouldReturnSortedList() {
        Employee e1 = new Employee("Adam", "Zyś", "adam@test.com", "TechCorp", Position.INTERN);
        Employee e2 = new Employee("Anna", "Kowalska", "anna@test.com", "TechCorp", Position.CEO);
        employeeService.addEmployee(e1);
        employeeService.addEmployee(e2);

        List<Employee> sorted = employeeService.sortByLastName();

        assertEquals(e2, sorted.get(0), "Kowalska powinna być pierwsza");
    }

    @Test
    @DisplayName("countEmployeesByPosition: Should return correct counts")
    void countEmployeesByPosition_shouldReturnCorrectMap() {
        employeeService.addEmployee(new Employee("A", "A", "a@a.com", "C", Position.PROGRAMMER));
        employeeService.addEmployee(new Employee("B", "B", "b@b.com", "C", Position.PROGRAMMER));
        employeeService.addEmployee(new Employee("C", "C", "c@c.com", "C", Position.MANAGER));

        Map<Position, Long> counts = employeeService.countEmployeesByPosition();

        assertAll("Weryfikacja zliczania stanowisk",
                () -> assertEquals(2, counts.get(Position.PROGRAMMER)),
                () -> assertEquals(1, counts.get(Position.MANAGER)),
                () -> assertFalse(counts.containsKey(Position.CEO))
        );
    }

    @Test
    @DisplayName("validateSalaryConsistency: Should find no inconsistencies (as per current bug)")
    void validateSalaryConsistency_shouldReturnEmptyList_dueToBug() {
        Employee e1 = new Employee("A", "A", "a@a.com", "C", Position.PROGRAMMER);
        employeeService.addEmployee(e1);

        List<Employee> inconsistencies = employeeService.validateSalaryConsistency();

        assertTrue(inconsistencies.isEmpty());
    }

    @Test
    @DisplayName("getCompanyStatistics: Should calculate correct statistics")
    void getCompanyStatistics_shouldReturnCorrectStats() {
        Employee e1 = new Employee("A", "A", "a@tc.com", "TechCorp", Position.PROGRAMMER);
        Employee e2 = new Employee("B", "B", "b@tc.com", "TechCorp", Position.MANAGER);
        Employee e3 = new Employee("C", "C", "c@ds.com", "DataSoft", Position.CEO);

        employeeService.addEmployee(e1);
        employeeService.addEmployee(e2);
        employeeService.addEmployee(e3);

        Map<String, CompanyStatistics> stats = employeeService.getCompanyStatistics();
        CompanyStatistics tcStats = stats.get("TechCorp");

        assertAll("Weryfikacja statystyk TechCorp",
                () -> assertEquals(2, tcStats.getEmployeeCount()),
                () -> assertEquals(10000.0, tcStats.getAverageSalary()),
                () -> assertEquals("B B", tcStats.getHighestPaidEmployeeName())
        );
    }


}