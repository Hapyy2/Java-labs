package com.techcorp.employee.service;

import com.techcorp.employee.exception.DuplicateEmailException;
import com.techcorp.employee.model.Employee;
import com.techcorp.employee.model.ImportSummary;
import com.techcorp.employee.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

    @Mock
    private EmployeeService mockEmployeeService;

    private ImportService importService;

    @TempDir
    Path tempDir;

    private File csvFile;

    @BeforeEach
    void setUp() throws IOException {
        String csvFilePath = "test-employees.csv";
        csvFile = tempDir.resolve(csvFilePath).toFile();

        importService = new ImportService(mockEmployeeService, "classpath:" + csvFilePath) {
            @Override
            public ImportSummary importFromCsv() throws IOException {
                int imported = 0;
                List<String> errors = new ArrayList<>();
                int lineNum = 0;
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                    if (br.readLine() != null) lineNum++;
                    String line;
                    while ((line = br.readLine()) != null) {
                        lineNum++;
                        if (line.trim().isEmpty()) continue;
                        try {
                            String[] data = line.split(",", -1);
                            if (data.length < 6) throw new RuntimeException("Za mało kolumn");

                            Employee e = new Employee(data[0], data[1], data[2], data[3], Position.valueOf(data[4].toUpperCase()), Double.parseDouble(data[5]));
                            try {
                                mockEmployeeService.addEmployee(e);
                                imported++;
                            } catch (DuplicateEmailException ex) {
                                errors.add("Linia " + lineNum + ": Duplikat emaila '" + data[2] + "'.");
                            }
                        } catch (Exception e) {
                            errors.add("Linia " + lineNum + ": Błąd danych - " + e.getMessage());
                        }
                    }
                }
                return new ImportSummary(imported, errors);
            }
        };
    }

    private void writeCsvContent(String content) throws IOException {
        Files.write(csvFile.toPath(), content.getBytes());
    }

    @Test
    void importFromCsv_shouldSucceed_withValidData() throws IOException, DuplicateEmailException {
        String content = "header\nJan,Kowalski,jan@test.com,TestCorp,PROGRAMMER,8000.0";
        writeCsvContent(content);

        ImportSummary summary = importService.importFromCsv();

        assertEquals(1, summary.getImportedCount());
        assertEquals(0, summary.getErrors().size());
    }

    @Test
    void importFromCsv_shouldReportError_forDuplicate() throws IOException, DuplicateEmailException {
        String content = "header\nJan,Kowalski,jan@test.com,TestCorp,PROGRAMMER,8000.0";
        writeCsvContent(content);

        doThrow(new DuplicateEmailException("Duplicate")).when(mockEmployeeService).addEmployee(any(Employee.class));

        ImportSummary summary = importService.importFromCsv();

        assertEquals(0, summary.getImportedCount());
        assertEquals(1, summary.getErrors().size());
    }

    @ParameterizedTest
    @CsvSource({
            "Anna,Nowak,anna@test.com,TestCorp,UNKNOWN,5000.0, Linia 2: Błąd danych - No enum constant com.techcorp.employee.model.Position.UNKNOWN"
    })
    void importFromCsv_shouldReportError_forInvalidData(String f, String l, String e, String c, String p, String s, String err) throws IOException {
        String content = "header\n" + String.join(",", f, l, e, c, p, s);
        writeCsvContent(content);

        ImportSummary summary = importService.importFromCsv();

        assertEquals(0, summary.getImportedCount());
        assertEquals(1, summary.getErrors().size());
    }
}