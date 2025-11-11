package com.techcorp.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.employee.dto.EmployeeDTO;
import com.techcorp.employee.exception.DuplicateEmailException;
import com.techcorp.employee.exception.EmployeeNotFoundException;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.EmploymentStatus;
import com.techcorp.employee.model.Position;
import com.techcorp.employee.service.ApiService;
import com.techcorp.employee.service.EmployeeService;
import com.techcorp.employee.service.ImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    // --- DODANE MOCKI, ABY APLIKACJA MOGŁA WSTAĆ ---
    @MockBean
    private ImportService importService;

    @MockBean
    private ApiService apiService;

    @MockBean(name = "xmlEmployees")
    private List<Employee> xmlEmployees;
    // -----------------------------------------------

    private Employee employee1;
    private Employee employee2;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employee1 = new Employee("Jan", "Kowalski", "jan@test.com", "TechCorp", Position.PROGRAMMER, 8000);
        employee2 = new Employee("Anna", "Nowak", "anna@test.com", "DataSoft", Position.MANAGER, 12000);
        employeeDTO = new EmployeeDTO("Jan", "Kowalski", "jan@test.com", "TechCorp", Position.PROGRAMMER, 8000, EmploymentStatus.ACTIVE);
    }

    @Test
    void getEmployees_shouldReturnAllEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee1, employee2));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is("jan@test.com")))
                .andExpect(jsonPath("$[1].email", is("anna@test.com")));
    }

    @Test
    void getEmployees_shouldFilterByCompany() throws Exception {
        when(employeeService.filterByCompany("DataSoft")).thenReturn(List.of(employee2));

        mockMvc.perform(get("/api/employees").param("company", "DataSoft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("anna@test.com")));
    }

    @Test
    void getEmployeeByEmail_shouldReturnEmployee_whenExists() throws Exception {
        when(employeeService.findEmployeeByEmail("jan@test.com")).thenReturn(Optional.of(employee1));

        mockMvc.perform(get("/api/employees/jan@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("jan@test.com")));
    }

    @Test
    void getEmployeeByEmail_shouldReturn404_whenNotExists() throws Exception {
        when(employeeService.findEmployeeByEmail("nie@istnieje.com"))
                .thenReturn(Optional.empty());

        // Zmiana: Serwis zwraca Optional.empty(), więc kontroler rzuci wyjątek wewnątrz.
        // W teście musimy symulować, że kontroler wywoła serwis, ten zwróci empty, a kontroler rzuci wyjątek.
        // Ale ponieważ mockujemy serwis, możemy wymusić rzucenie wyjątku bezpośrednio,
        // LUB (lepiej) pozwolić kontrolerowi rzucić wyjątek na podstawie pustego Optionala.
        // W Twoim kontrolerze jest .orElseThrow(), więc jeśli zwrócimy Optional.empty(), wyjątek poleci.
        // Jednak MockMvcTest oczekuje, że wyjątek zostanie obsłużony przez GlobalExceptionHandler.

        // Aby test przeszedł poprawnie z Twoim kontrolerem:
        when(employeeService.findEmployeeByEmail("nie@istnieje.com")).thenReturn(Optional.empty());

        // ALE: Twój kontroler w kodzie ma: .orElseThrow(() -> new EmployeeNotFoundException(...))
        // Więc to zadziała automatycznie.

        mockMvc.perform(get("/api/employees/nie@istnieje.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Nie znaleziono pracownika o emailu: nie@istnieje.com")));
    }

    @Test
    void getEmployeesByStatus_shouldReturnFilteredList() throws Exception {
        when(employeeService.findEmployeesByStatus(EmploymentStatus.ACTIVE)).thenReturn(List.of(employee1));

        mockMvc.perform(get("/api/employees/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("jan@test.com")));
    }

    @Test
    void createEmployee_shouldReturn201_whenDataIsValid() throws Exception {
        when(employeeService.addEmployee(any(Employee.class))).thenReturn(employee1);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/employees/jan@test.com"))
                .andExpect(jsonPath("$.email", is("jan@test.com")));
    }

    @Test
    void createEmployee_shouldReturn409_whenEmailExists() throws Exception {
        when(employeeService.addEmployee(any(Employee.class)))
                .thenThrow(new DuplicateEmailException("Email jan@test.com already exists."));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    void updateEmployee_shouldReturn200_whenSuccess() throws Exception {
        when(employeeService.updateEmployee(eq("jan@test.com"), any(Employee.class))).thenReturn(employee1);

        mockMvc.perform(put("/api/employees/jan@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("jan@test.com")));
    }

    @Test
    void updateEmployee_shouldReturn400_whenEmailMismatch() throws Exception {
        employeeDTO.setEmail("other@mail.com");

        mockMvc.perform(put("/api/employees/jan@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteEmployee_shouldReturn204_whenSuccess() throws Exception {
        doNothing().when(employeeService).deleteEmployee("jan@test.com");

        mockMvc.perform(delete("/api/employees/jan@test.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEmployee_shouldReturn404_whenNotExists() throws Exception {
        doThrow(new EmployeeNotFoundException("Not found"))
                .when(employeeService).deleteEmployee("nie@istnieje.com");

        mockMvc.perform(delete("/api/employees/nie@istnieje.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEmployeeStatus_shouldReturn200_whenSuccess() throws Exception {
        employee1.setStatus(EmploymentStatus.ON_LEAVE);
        when(employeeService.updateEmployeeStatus(eq("jan@test.com"), eq(EmploymentStatus.ON_LEAVE)))
                .thenReturn(employee1);

        Map<String, String> statusUpdate = Map.of("status", "ON_LEAVE");

        mockMvc.perform(patch("/api/employees/jan@test.com/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ON_LEAVE")));
    }
}