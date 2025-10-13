package org.example.service;

import com.google.gson.*;
import org.example.exception.ApiException;
import org.example.model.Employee;
import org.example.model.Position;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ApiService {
    private static final String API_URL = "https://jsonplaceholder.typicode.com/users";
    private static final Position DEFAULT_POSITION = Position.PROGRAMMER;

    public List<Employee> fetchEmployeesFromApi() throws ApiException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiException(String.format("Błąd HTTP: Status %d", response.statusCode()));
            }

            return parseJsonToEmployees(response.body());

        } catch (IOException | InterruptedException e) {
            throw new ApiException("Błąd komunikacji z API.", e);
        }
    }

    private List<Employee> parseJsonToEmployees(String jsonBody) throws ApiException {
        List<Employee> employees = new ArrayList<>();
        Gson gson = new Gson();

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