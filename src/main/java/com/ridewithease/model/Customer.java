package com.ridewithease.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity @Table(name = "customers")
public class Customer extends User {
    public Customer() {}
    public Customer(String name, String email, String phone, String password) {
        super(name, email, phone, password);
    }
}
