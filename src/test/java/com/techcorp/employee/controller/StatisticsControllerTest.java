package com.techcorp.employee.controller;

import com.techcorp.employee.dto.CompanyStatisticsDTO;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.service.ApiService;
import com.techcorp.employee.service.EmployeeService;
import com.techcorp.employee.service.ImportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    // --- DODANE MOCKI ---
    @MockBean
    private ImportService importService;

    @MockBean
    private ApiService apiService;

    @MockBean(name = "xmlEmployees")
    private List<Employee> xmlEmployees;
    // --------------------

    @Test
    void getAverageSalary_shouldReturnGlobalAverage() throws Exception {
        when(employeeService.calculateAverageSalary(null)).thenReturn(10000.0);

        mockMvc.perform(get("/api/statistics/salary/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSalary", is(10000.0)));
    }

    @Test
    void getAverageSalary_shouldReturnCompanyAverage() throws Exception {
        when(employeeService.calculateAverageSalary("TechCorp")).thenReturn(9000.0);

        mockMvc.perform(get("/api/statistics/salary/average").param("company", "TechCorp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageSalary", is(9000.0)));
    }

    @Test
    void getPositionCounts_shouldReturnMap() throws Exception {
        when(employeeService.getEmployeeCountByPositionString()).thenReturn(Map.of(
                "PROGRAMMER", 2L,
                "MANAGER", 1L
        ));

        mockMvc.perform(get("/api/statistics/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PROGRAMMER", is(2)))
                .andExpect(jsonPath("$.MANAGER", is(1)));
    }

    @Test
    void getStatusCounts_shouldReturnMap() throws Exception {
        when(employeeService.getEmployeeCountByStatusString()).thenReturn(Map.of(
                "ACTIVE", 5L,
                "ON_LEAVE", 1L
        ));

        mockMvc.perform(get("/api/statistics/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ACTIVE", is(5)))
                .andExpect(jsonPath("$.ON_LEAVE", is(1)));
    }

    @Test
    void getCompanyStatistics_shouldReturnCorrectDTO() throws Exception {
        CompanyStatisticsDTO stats = new CompanyStatisticsDTO("TechCorp", 2, 8500.0, 9000.0, "Jan Kowalski");
        when(employeeService.getCompanyStatistics()).thenReturn(Map.of("TechCorp", stats));

        mockMvc.perform(get("/api/statistics/company/TechCorp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName", is("TechCorp")))
                .andExpect(jsonPath("$.employeeCount", is(2)))
                .andExpect(jsonPath("$.averageSalary", is(8500.0)))
                .andExpect(jsonPath("$.highestSalary", is(9000.0)))
                .andExpect(jsonPath("$.topEarnerName", is("Jan Kowalski")));
    }

    @Test
    void getCompanyStatistics_shouldReturn404_whenCompanyNotFound() throws Exception {
        when(employeeService.getCompanyStatistics()).thenReturn(Map.of());

        mockMvc.perform(get("/api/statistics/company/NonExistent"))
                .andExpect(status().isNotFound());
    }
}