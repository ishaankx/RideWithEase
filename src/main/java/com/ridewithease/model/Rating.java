package com.ridewithease.model;
import jakarta.persistence.*;

@Entity @Table(name = "ratings")
public class Rating {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "ride_id")
    private Ride ride;

    private Long givenBy;
    private Long givenTo;
    private int score;
    private String comment;

    public Rating() {}
    public Rating(Ride ride, Long from, Long to, int score, String comment) {
        this.ride = ride; this.givenBy = from; this.givenTo = to; this.score = score; this.comment = comment;
    }
}