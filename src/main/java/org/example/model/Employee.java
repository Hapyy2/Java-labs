package org.example.model;

public class Employee {
    private String firstName;
    private String lastName;
    private String email;
    private String company;
    private Position position;
    private double salary;

    public Employee(String firstName, String lastName, String email, String company, Position position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
        this.position = position;
        this.salary = this.position.getSalary();
    }

    public String getFirstName() {
        return this.firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
    public String getEmail() {
        return this.email;
    }
    public String getCompany() {
        return this.company;
    }
    public Position getPosition() {
        return this.position;
    }
    public double getSalary() {
        return this.salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return email.equalsIgnoreCase(employee.email);
    }

    @Override
    public int hashCode(){
        return (this.getEmail()).toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Employee{name='%s', email='%s', position=%s, salary=%.2f, company=%s}",
                this.getFullName(), this.getEmail(), this.getPosition().getName(), this.getSalary(), this.getCompany());
    }
}