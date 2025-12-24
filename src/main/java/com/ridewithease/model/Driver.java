package com.ridewithease.model;
import jakarta.persistence.*;

@Entity @Table(name = "drivers")
public class Driver extends User {
    private String licenseNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private boolean available = true;

    public Driver() {}
    public Driver(String name, String email, String phone, String pass, String license, Vehicle vehicle) {
        super(name, email, phone, pass);
        this.licenseNumber = license;
        this.vehicle = vehicle;
    }


    public void setAvailable(boolean b) { this.available = b; }
    public Vehicle getVehicle() { return vehicle; }
}