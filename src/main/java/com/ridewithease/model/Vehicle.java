package com.ridewithease.model;
import jakarta.persistence.*;

@Entity @Table(name = "vehicles")
public class Vehicle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;
    private String model;
    private String plateNumber;
    private String color;
    private int year;

    public Vehicle() {}
    public Vehicle(String make, String model, String plate, String color, int year) {
        this.make = make; this.model = model; this.plateNumber = plate; this.color = color; this.year = year;
    }
}