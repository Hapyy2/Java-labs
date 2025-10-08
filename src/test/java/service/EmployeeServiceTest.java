package service;

import org.example.model.Employee;
import org.example.model.Position;
import org.example.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {

    private EmployeeService service;
    private Employee anna;
    private Employee piotr;
    private Employee janusz;

    @BeforeEach
    void setUp() {
        service = new EmployeeService();

        anna = new Employee("Anna", "Kowalska", "anna@tc.com", "TechCorp", Position.CEO);
        piotr = new Employee("Piotr", "Nowak", "piotr@tc.com", "TechCorp", Position.PROGRAMMER);
        janusz = new Employee("Janusz", "Kruk", "janusz@ds.com", "DataSoft", Position.MANAGER);

        service.addEmployee(anna);
        service.addEmployee(piotr);
        service.addEmployee(janusz);
    }

    @Test
    void shouldAddEmployeeAndPreventDuplicates() {
        assertEquals(3, service.getAllEmployees().size(), "Powinny być 3 pracownicy po setUp.");

        Employee duplicate = new Employee("Test", "Duplikat", "anna@tc.com", "Corp", Position.INTERN);
        boolean isAdded = service.addEmployee(duplicate);

        assertFalse(isAdded, "Duplikat nie powinien zostać dodany.");
        assertEquals(3, service.getAllEmployees().size(), "Rozmiar listy powinien pozostać 3.");
    }

    @Test
    void shouldSortEmployeesByLastName() {
        List<Employee> sortedList = service.sortByLastName();

        assertEquals(3, sortedList.size());
        assertEquals("Kowalska", sortedList.get(0).getSurname(), "Pierwsza powinna być Kowalska.");
        assertEquals("Kruk", sortedList.get(1).getSurname(), "Druga powinna być Kruk.");
        assertEquals("Nowak", sortedList.get(2).getSurname(), "Trzeci powinien być Nowak.");
    }

    @Test
    void shouldFilterEmployeesByCompany() {
        List<Employee> techCorpList = service.filterByCompany("TechCorp");
        List<Employee> dataSoftList = service.filterByCompany("DataSoft");

        assertEquals(2, techCorpList.size());
        assertEquals(1, dataSoftList.size());

        assertTrue(techCorpList.contains(anna));
        assertFalse(dataSoftList.contains(anna));
    }

    @Test
    void shouldCalculateCorrectAverageSalary() {
        double expectedAverage = (25000.0 + 8000.0 + 12000.0) / 3.0; // 45000 / 3 = 15000.0

        assertEquals(15000.0, service.calculateAverageSalary(), 0.001);
    }

    @Test
    void shouldFindHighestPaidEmployee() {
        Optional<Employee> highestPaid = service.getHighestPaidEmployee();

        assertTrue(highestPaid.isPresent(), "Optional powinien zawierać pracownika.");
        assertEquals(anna, highestPaid.get(), "Anna Kowalska powinna być najlepiej opłacana.");
    }
}