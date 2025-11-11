package com.techcorp.employee.model;

import java.util.Objects;

public class Employee {
    private String firstName;
    private String lastName;
    private String email;
    private String company;
    private Position position;
    private double salary;
    private EmploymentStatus status; // NOWE POLE

    public Employee(String firstName, String lastName, String email, String company, Position position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
        this.position = position;
        this.salary = this.position.getSalary();
        this.status = EmploymentStatus.ACTIVE; // Domyślny status
    }

    public Employee(String firstName, String lastName, String email, String company, Position position, double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
        this.position = position;
        this.salary = salary;
        this.status = EmploymentStatus.ACTIVE; // Domyślny status
    }

    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getFullName() { return this.firstName + " " + this.lastName; }
    public String getEmail() { return this.email; }
    public String getCompany() { return this.company; }
    public Position getPosition() { return this.position; }
    public double getSalary() { return this.salary; }
    public EmploymentStatus getStatus() { return status; } // Nowy getter

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setCompany(String company) { this.company = company; }
    public void setPosition(Position position) { this.position = position; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setStatus(EmploymentStatus status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return email.equalsIgnoreCase(employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("Employee{name='%s', email='%s', position=%s, salary=%.2f, company=%s, status=%s}",
                this.getFullName(), this.getEmail(), this.getPosition().name(), this.getSalary(), this.getCompany(), this.getStatus());
    }
}