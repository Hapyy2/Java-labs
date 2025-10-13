package org.example.model;

public class CompanyStatistics {
    private final long employeeCount;
    private final double averageSalary;
    private final String highestPaidEmployeeName;

    public CompanyStatistics(long employeeCount, double averageSalary, String highestPaidEmployeeName) {
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary;
        this.highestPaidEmployeeName = highestPaidEmployeeName;
    }

    public long getEmployeeCount() { return employeeCount; }
    public double getAverageSalary() { return averageSalary; }
    public String getHighestPaidEmployeeName() { return highestPaidEmployeeName; }

    @Override
    public String toString() {
        return String.format("Statystyki{Pracowników: %d, Średnia pensja: %.2f, Najwyższa pensja: '%s'}",
                employeeCount, averageSalary, highestPaidEmployeeName);
    }
}