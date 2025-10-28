package org.example.service;

import org.example.model.ImportSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

    @Mock
    private EmployeeService mockEmployeeService;

    @InjectMocks
    private ImportService importService;

    @TempDir
    Path tempDir;

    private File csvFile;

    @BeforeEach
    void setUp() throws IOException {
        csvFile = tempDir.resolve("employees.csv").toFile();
    }

    @Test
    @DisplayName("Should return correct summary for a valid import")
    void importFromCsv_shouldSucceed_withValidData() throws IOException {
        String content = "imie,nazwisko,email,firma,stanowisko,pensja\n" +
                "Jan,Kowalski,jan@test.com,TestCorp,PROGRAMMER,8000.0";
        Files.write(csvFile.toPath(), content.getBytes());

        when(mockEmployeeService.addEmployee(any())).thenReturn(true);

        ImportSummary summary = importService.importFromCsv(csvFile.getAbsolutePath());

        assertAll("Sprawdzenie podsumowania udanego importu",
                () -> assertEquals(1, summary.getImportedCount(), "Liczba zaimportowanych powinna być 1"),
                () -> assertEquals(0, summary.getErrors().size(), "Lista błędów powinna być pusta")
        );
    }

    @DisplayName("Should report correct error for invalid data rows")
    @ParameterizedTest(name = "Błąd: {1}")
    @CsvSource({
            "Anna,Nowak,anna@test.com,TestCorp,UNKNOWN_POSITION,5000.0, Linia 2: Błąd danych - Nieznane stanowisko: UNKNOWN_POSITION",
            "Piotr,Zieliński,piotr@test.com,TestCorp,MANAGER,-12000.0, Linia 2: Błąd danych - Wynagrodzenie musi być dodatnie: -12000.0",
            "Marek,Bąk,marek@test.com,TestCorp,PROGRAMMER,invalid_salary, Linia 2: Błąd formatu liczby dla wynagrodzenia."
    })
    void importFromCsv_shouldReportError_forInvalidData(String fname, String lname, String email, String company, String pos, String salary, String expectedError) throws IOException {

        String content = "imie,nazwisko,email,firma,stanowisko,pensja\n" +
                String.join(",", fname, lname, email, company, pos, salary);
        Files.write(csvFile.toPath(), content.getBytes());

        ImportSummary summary = importService.importFromCsv(csvFile.getAbsolutePath());

        assertAll("Sprawdzenie podsumowania błędu importu",
                () -> assertEquals(0, summary.getImportedCount(), "Nie powinno być zaimportowanych wierszy"),
                () -> assertEquals(1, summary.getErrors().size(), "Powinien być dokładnie jeden błąd"),
                () -> assertEquals(expectedError, summary.getErrors().get(0), "Komunikat błędu jest nieprawidłowy")
        );
    }
}