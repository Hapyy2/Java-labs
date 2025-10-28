package org.example.service;

import org.example.exception.ApiException;
import org.example.model.Employee;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("Should fetch and parse employees when API returns 200 OK")
    void fetchEmployeesFromApi_shouldSucceed_on200OK() throws IOException, InterruptedException, ApiException {
        String jsonResponse = """
            [
              {
                "id": 1,
                "name": "Leanne Graham",
                "email": "Sincere@april.biz",
                "company": { "name": "Romaguera-Crona" }
              }
            ]
            """;

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        ApiService apiService = new ApiService(mockHttpClient);

        List<Employee> employees = apiService.fetchEmployeesFromApi();

        assertAll("Weryfikacja poprawności zmapowanego pracownika",
                () -> assertFalse(employees.isEmpty(), "Lista nie powinna być pusta"),
                () -> assertEquals(1, employees.size(), "Lista powinna zawierać jednego pracownika"),
                () -> assertEquals("Leanne", employees.get(0).getFirstName(), "Imię jest niepoprawne"),
                () -> assertEquals("Graham", employees.get(0).getLastName(), "Nazwisko jest niepoprawne"),
                () -> assertEquals("Sincere@april.biz", employees.get(0).getEmail(), "Email jest niepoprawny")
        );
    }

    @Test
    @DisplayName("Should throw ApiException when API returns non-200 status")
    void fetchEmployeesFromApi_shouldThrowApiException_onNon200() throws IOException, InterruptedException {
        when(mockHttpResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);

        ApiService apiService = new ApiService(mockHttpClient);

        ApiException exception = assertThrows(ApiException.class, apiService::fetchEmployeesFromApi);
        assertEquals("Błąd HTTP: Status 500", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ApiException on communication error")
    void fetchEmployeesFromApi_shouldThrowApiException_onIOException() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Connection failed"));

        ApiService apiService = new ApiService(mockHttpClient);

        ApiException exception = assertThrows(ApiException.class, apiService::fetchEmployeesFromApi);
        assertEquals("Błąd komunikacji z API.", exception.getMessage());
    }
}