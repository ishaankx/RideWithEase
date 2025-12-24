package com.ridewithease.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    // Location Text
    private String pickupLoc;
    private String dropLoc;

    // Map Coordinates (Lat/Lng)
    private double pickupLat;
    private double pickupLng;
    private double dropLat;
    private double dropLng;

    // Trip Details (These were missing in your code!)
    private double distanceKm;
    private String duration;

    private double fare;
    private String status; // REQUESTED, ACCEPTED, ONGOING, COMPLETED, CANCELLED

    // Timestamps for Trip History
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    public Ride() {}

    // Constructor used by RideServlet
    public Ride(Customer c, String pick, String drop, double pLat, double pLng, double dLat, double dLng, double dist, double fare, String duration) {
        this.customer = c;
        this.pickupLoc = pick;
        this.dropLoc = drop;
        this.pickupLat = pLat;
        this.pickupLng = pLng;
        this.dropLat = dLat;
        this.dropLng = dLng;
        this.distanceKm = dist;
        this.fare = fare;
        this.duration = duration;
        this.status = "REQUESTED";
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public String getPickupLoc() { return pickupLoc; }
    public String getDropLoc() { return dropLoc; }

    public double getPickupLat() { return pickupLat; }
    public double getPickupLng() { return pickupLng; }
    public double getDropLat() { return dropLat; }
    public double getDropLng() { return dropLng; }

    public double getDistanceKm() { return distanceKm; }
    public String getDuration() { return duration; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
}