package org.example.model;

public class Employee {
    private String name;
    private String surname;
    private String email;
    private String company;
    private Position position;
    private double salary;

    public Employee(String name, String surname, String email, String company, Position position) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.company = company;
        this.position = position;
        this.salary = this.position.getSalary();
    }

    public String getName() {
        return this.name;
    }
    public String getSurname() {
        return this.surname;
    }
    public String getFullName() {
        return this.name + " " + this.surname;
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
        return email.equals(employee.email);
    }

    @Override
    public int hashCode(){
        return (this.getEmail()).hashCode();
    }

    @Override
    public String toString() {
        return String.format("Employee{name='%s', email='%s', position=%s, salary=%.2f, company=%s}",
                this.getFullName(), this.getEmail(), this.getPosition().getName(), this.getSalary(), this.getCompany());
    }

}