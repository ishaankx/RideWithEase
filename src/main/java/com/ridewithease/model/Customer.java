package com.ridewithease.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity @Table(name = "customers")
public class Customer extends User {


    private int loyaltyPoints;
    private int completedRides;

    private boolean usedNewRider;
    private boolean usedTenRides;
    private boolean usedFiftyRides;

    public Customer() {}
    public Customer(String name, String email, String phone, String password) {
        super(name, email, phone, password);
        this.loyaltyPoints = 0;
        this.completedRides = 0;
    }



    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public int getCompletedRides() { return completedRides; }
    public void setCompletedRides(int completedRides) { this.completedRides = completedRides; }

    public boolean isUsedNewRider() { return usedNewRider; }
    public void setUsedNewRider(boolean usedNewRider) { this.usedNewRider = usedNewRider; }

    public boolean isUsedTenRides() { return usedTenRides; }
    public void setUsedTenRides(boolean usedTenRides) { this.usedTenRides = usedTenRides; }

    public boolean isUsedFiftyRides() { return usedFiftyRides; }
    public void setUsedFiftyRides(boolean usedFiftyRides) { this.usedFiftyRides = usedFiftyRides; }
}