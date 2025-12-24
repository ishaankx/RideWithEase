package com.ridewithease.model;
import jakarta.persistence.*;

@Entity @Table(name = "ratings")
public class Rating {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "ride_id")
    private Ride ride;

    private Long givenBy; // User ID
    private Long givenTo; // User ID
    private int score; // 1-5
    private String comment;

    public Rating() {}
    public Rating(Ride ride, Long from, Long to, int score, String comment) {
        this.ride = ride; this.givenBy = from; this.givenTo = to; this.score = score; this.comment = comment;
    }
}