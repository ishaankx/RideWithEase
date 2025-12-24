package com.ridewithease.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String fullName;
    @Column(unique = true) protected String email;
    @Column(unique = true) protected String phone;
    protected String password;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt = new Date();

    @Transient // This field is NOT stored in DB, just calculated for UI
    private double averageRating;

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public User() {}
    public User(String fullName, String email, String phone, String password) {
        this.fullName = fullName; this.email = email; this.phone = phone; this.password = password;
    }


    public Long getId() { return id; }
    public String getRole() { return this instanceof Customer ? "RIDER" : "DRIVER"; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }  // <--- This fixes your error
    public String getPhone() { return phone; }

}
