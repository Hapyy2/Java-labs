package com.techcorp.employee.model;

import java.util.List;

public class ImportSummary {
    private final int importedCount;
    private final List<String> errors;

    public ImportSummary(int importedCount, List<String> errors) {
        this.importedCount = importedCount;
        this.errors = errors;
    }

    public int getImportedCount() { return importedCount; }
    public List<String> getErrors() { return errors; }

    @Override
    public String toString() {
        return String.format("Podsumowanie importu{zaimportowanych=%d, błędów=%d, błędy=%s}",
                importedCount, errors.size(), errors.isEmpty() ? "Brak" : errors);
    }
}