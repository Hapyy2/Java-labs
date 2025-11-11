package com.techcorp.employee.dto;

import com.techcorp.employee.model.EmploymentStatus;
import com.techcorp.employee.model.Position;

public class EmployeeDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String company;
    private Position position;
    private double salary;
    private EmploymentStatus status;

    public EmployeeDTO() {}

    public EmployeeDTO(String firstName, String lastName, String email, String company, Position position, double salary, EmploymentStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
        this.position = position;
        this.salary = salary;
        this.status = status;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getCompany() { return company; }
    public Position getPosition() { return position; }
    public double getSalary() { return salary; }
    public EmploymentStatus getStatus() { return status; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setCompany(String company) { this.company = company; }
    public void setPosition(Position position) { this.position = position; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setStatus(EmploymentStatus status) { this.status = status; }
}