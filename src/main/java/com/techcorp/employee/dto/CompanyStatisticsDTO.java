package com.techcorp.employee.dto;

public class CompanyStatisticsDTO {
    private String companyName;
    private long employeeCount;
    private double averageSalary;
    private double highestSalary; // Nowe pole
    private String topEarnerName;

    public CompanyStatisticsDTO(String companyName, long employeeCount, double averageSalary, double highestSalary, String topEarnerName) {
        this.companyName = companyName;
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary;
        this.highestSalary = highestSalary;
        this.topEarnerName = topEarnerName;
    }

    public String getCompanyName() { return companyName; }
    public long getEmployeeCount() { return employeeCount; }
    public double getAverageSalary() { return averageSalary; }
    public double getHighestSalary() { return highestSalary; }
    public String getTopEarnerName() { return topEarnerName; }
}