package com.techcorp.employee.service;

import com.google.gson.*;
import com.techcorp.employee.exception.ApiException;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.Position;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiService {

    private static final Position DEFAULT_POSITION = Position.PROGRAMMER;

    private final HttpClient client;
    private final Gson gson;
    private final String apiUrl;

    public ApiService(HttpClient client, Gson gson, @Value("${app.api.url}") String apiUrl) {
        this.client = client;
        this.gson = gson;
        this.apiUrl = apiUrl;
    }

    public List<Employee> fetchEmployeesFromApi() throws ApiException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiException(String.format("Błąd HTTP: Status %d", response.statusCode()));
            }

            return parseJsonToEmployees(response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Błąd komunikacji z API.", e);
        }
    }

    private List<Employee> parseJsonToEmployees(String jsonBody) throws ApiException {
        List<Employee> employees = new ArrayList<>();

        try {
            JsonArray users = gson.fromJson(jsonBody, JsonArray.class);

            for (JsonElement element : users) {
                JsonObject user = element.getAsJsonObject();

                String fullName = user.get("name").getAsString();
                String[] nameParts = fullName.trim().split("\\s+", 2);

                String firstName = nameParts.length > 0 ? nameParts[0] : "N/A";
                String lastName = nameParts.length > 1 ? nameParts[1] : "N/A";

                String email = user.get("email").getAsString();
                String companyName = user.getAsJsonObject("company").get("name").getAsString();

                employees.add(new Employee(
                        firstName,
                        lastName,
                        email,
                        companyName,
                        DEFAULT_POSITION
                ));
            }

        } catch (JsonParseException e) {
            throw new ApiException("Błąd parsowania JSON z odpowiedzi API.", e);
        } catch (Exception e) {
            throw new ApiException("Błąd podczas przetwarzania danych API: " + e.getMessage(), e);
        }

        return employees;
    }
}