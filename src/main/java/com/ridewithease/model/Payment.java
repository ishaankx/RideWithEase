package com.ridewithease.model;
import jakarta.persistence.*;
import java.util.Date;

@Entity @Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne @JoinColumn(name = "ride_id")
    private Ride ride;

    private double amount;
    private String method; // CASH, CARD, WALLET
    private String status; // COMPLETED, FAILED
    private Date paymentDate = new Date();

    public Payment() {}
    public Payment(Ride ride, double amount, String method) {
        this.ride = ride; this.amount = amount; this.method = method; this.status = "COMPLETED";
    }
}