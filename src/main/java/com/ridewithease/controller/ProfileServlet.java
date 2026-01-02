package com.ridewithease.controller;

import com.google.gson.Gson;
import com.ridewithease.model.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/profile")
public class ProfileServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("rideEasePU");
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");


        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\": \"No token provided\"}");
            return;
        }
        String token = authHeader.substring(7);

        EntityManager em = emf.createEntityManager();
        try {

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(AuthServlet.KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();


            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :e", User.class)
                    .setParameter("e", email)
                    .getSingleResult();

            if (user instanceof Customer) {
                Customer cust = (Customer) user;


                List<Ride> rawRides = em.createQuery(
                                "SELECT r FROM Ride r WHERE r.customer.id = :uid ORDER BY r.id DESC", Ride.class)
                        .setParameter("uid", user.getId())
                        .getResultList();


                List<RideDTO> rideDTOs = rawRides.stream().map(RideDTO::new).collect(Collectors.toList());


                ProfileResponse response = new ProfileResponse(cust, rideDTOs);
                resp.getWriter().write(gson.toJson(response));
            } else {
                resp.setStatus(400);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(401);
            resp.getWriter().write("{\"error\": \"Invalid Token\"}");
        } finally {
            em.close();
        }
    }


    static class ProfileResponse {
        String fullName;
        String email;
        int loyaltyPoints;
        int completedRides;

        boolean usedNewRider;
        boolean usedTenRides;
        boolean usedFiftyRides;
        List<RideDTO> rides;

        public ProfileResponse(Customer c, List<RideDTO> rides) {
            this.fullName = c.getFullName();
            this.email = c.getEmail();
            this.loyaltyPoints = c.getLoyaltyPoints();
            this.completedRides = c.getCompletedRides();
            this.usedNewRider = c.isUsedNewRider();
            this.usedTenRides = c.isUsedTenRides();
            this.usedFiftyRides = c.isUsedFiftyRides();
            this.rides = rides;
        }
    }

    static class RideDTO {
        Long id;
        String pickupLoc;
        String dropLoc;
        double fare;
        String status;
        String date;

        public RideDTO(Ride r) {
            this.id = r.getId();
            this.pickupLoc = r.getPickupLoc();
            this.dropLoc = r.getDropLoc();
            this.fare = r.getFare();
            this.status = r.getStatus();

            this.date = r.getEndTime() != null ? r.getEndTime().toString() :
                    (r.getStartTime() != null ? r.getStartTime().toString() : "N/A");
        }
    }
}