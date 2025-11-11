package com.techcorp.employee.model;

public enum Position {
    CEO("CEO", 25000.0, 1),
    VICECEO("ViceCEO", 18000.0, 2),
    MANAGER("Manager", 12000.0, 3),
    PROGRAMMER("Programmer", 8000.0, 4),
    INTERN("Intern", 3000.0, 5);

    private String name;
    private double salary;
    private int level;

    private Position(String name, double salary, int level){
        this.name = name;
        this.salary = salary;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    public int getLevel() {
        return level;
    }
}