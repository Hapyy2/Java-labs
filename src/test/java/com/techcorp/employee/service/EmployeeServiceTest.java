package com.techcorp.employee.service;

import com.techcorp.employee.dto.CompanyStatisticsDTO;
import com.techcorp.employee.exception.DuplicateEmailException;
import com.techcorp.employee.exception.EmployeeNotFoundException;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.EmploymentStatus;
import com.techcorp.employee.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    private EmployeeService employeeService;
    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService();
        employee1 = new Employee("Jan", "Kowalski", "jan@test.com", "TechCorp", Position.PROGRAMMER, 8000);
        employee2 = new Employee("Anna", "Nowak", "anna@test.com", "DataSoft", Position.MANAGER, 12000);
    }

    @Test
    void addEmployee_shouldSucceed_whenEmailIsUnique() {
        assertDoesNotThrow(() -> employeeService.addEmployee(employee1));
        assertEquals(1, employeeService.getAllEmployees().size());
    }

    @Test
    void addEmployee_shouldThrowException_whenEmailIsDuplicate() {
        assertDoesNotThrow(() -> employeeService.addEmployee(employee1));
        assertThrows(DuplicateEmailException.class, () -> employeeService.addEmployee(employee1));
    }

    @Test
    void findEmployeeByEmail_shouldReturnEmployee() throws DuplicateEmailException {
        employeeService.addEmployee(employee1);
        Optional<Employee> found = employeeService.findEmployeeByEmail("jan@test.com");
        assertTrue(found.isPresent());
        assertEquals(employee1, found.get());
    }

    @Test
    void updateEmployee_shouldUpdateFields() throws DuplicateEmailException {
        employeeService.addEmployee(employee1);
        Employee updateData = new Employee("Janusz", "Kowalski", "jan@test.com", "NewCorp", Position.MANAGER, 15000);
        updateData.setStatus(EmploymentStatus.ON_LEAVE);

        Employee updated = employeeService.updateEmployee("jan@test.com", updateData);

        assertEquals("Janusz", updated.getFirstName());
        assertEquals("NewCorp", updated.getCompany());
        assertEquals(EmploymentStatus.ON_LEAVE, updated.getStatus());
    }

    @Test
    void deleteEmployee_shouldRemoveEmployee() throws DuplicateEmailException {
        employeeService.addEmployee(employee1);
        employeeService.deleteEmployee("jan@test.com");
        assertTrue(employeeService.findEmployeeByEmail("jan@test.com").isEmpty());
    }

    @Test
    void deleteEmployee_shouldThrow404_whenNotFound() {
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee("unknown@test.com"));
    }

    @Test
    void getEmployeeCountByPositionString_shouldReturnCorrectMap() throws DuplicateEmailException {
        employeeService.addEmployee(employee1);
        employeeService.addEmployee(employee2);

        Map<String, Long> counts = employeeService.getEmployeeCountByPositionString();
        assertEquals(1L, counts.get("PROGRAMMER"));
        assertEquals(1L, counts.get("MANAGER"));
    }

    @Test
    void getEmployeeCountByStatusString_shouldReturnCorrectMap() throws DuplicateEmailException {
        employeeService.addEmployee(employee1);
        employee1.setStatus(EmploymentStatus.ON_LEAVE);

        Map<String, Long> counts = employeeService.getEmployeeCountByStatusString();
        assertEquals(1L, counts.get("ON_LEAVE"));
    }

    @Test
    void getCompanyStatistics_shouldReturnCorrectDTO() throws DuplicateEmailException {
        employeeService.addEmployee(employee1);
        employeeService.addEmployee(employee2);

        Map<String, CompanyStatisticsDTO> stats = employeeService.getCompanyStatistics();

        assertNotNull(stats.get("TechCorp"));
        assertEquals(8000.0, stats.get("TechCorp").getAverageSalary());
        assertNotNull(stats.get("DataSoft"));
    }
}