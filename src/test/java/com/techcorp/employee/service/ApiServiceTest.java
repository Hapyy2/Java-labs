package com.techcorp.employee.service;

import com.google.gson.Gson;
import com.techcorp.employee.exception.ApiException;
import com.techcorp.employee.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiServiceTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private ApiService apiService;

    @BeforeEach
    void setUp() {
        apiService = new ApiService(mockHttpClient, new Gson(), "http://test-api.url");
    }

    @Test
    void fetchEmployeesFromApi_shouldSucceed_on200OK() throws IOException, InterruptedException, ApiException {
        String jsonResponse = "[{\"name\": \"Leanne Graham\", \"email\": \"Sincere@april.biz\", \"company\": { \"name\": \"Romaguera-Crona\" }}]";

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        List<Employee> employees = apiService.fetchEmployeesFromApi();

        assertFalse(employees.isEmpty());
        assertEquals("Leanne", employees.get(0).getFirstName());
    }

    @Test
    void fetchEmployeesFromApi_shouldThrowApiException_onNon200() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        assertThrows(ApiException.class, apiService::fetchEmployeesFromApi);
    }
}