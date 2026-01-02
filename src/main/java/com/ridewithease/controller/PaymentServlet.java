package com.ridewithease.controller;

import com.google.gson.Gson;
import com.ridewithease.model.*;
import jakarta.persistence.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Date;

@WebServlet("/api/payments")
public class PaymentServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("rideEasePU");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EntityManager em = emf.createEntityManager();
        resp.setContentType("application/json");

        try {
            em.getTransaction().begin();

            PayRequest reqData = new Gson().fromJson(req.getReader(), PayRequest.class);


            Ride ride = em.find(Ride.class, reqData.rideId);
            if (ride == null) {
                resp.setStatus(404);
                resp.getWriter().write("{\"error\": \"Ride not found\"}");
                return;
            }

            Customer customer = ride.getCustomer();


            double couponDiscount = 0;
            String selectedCoupon = reqData.coupon != null ? reqData.coupon : "none";

            switch(selectedCoupon) {
                case "newrider":
                    if (customer.getCompletedRides() == 0 && !customer.isUsedNewRider()) {
                        couponDiscount = 50;
                        customer.setUsedNewRider(true);
                    }
                    break;
                case "10rides":

                    if (customer.getCompletedRides() >= 10 && !customer.isUsedTenRides()) {
                        couponDiscount = 50;
                        customer.setUsedTenRides(true);
                    }
                    break;
                case "50rides":
                    if (customer.getCompletedRides() >= 50 && !customer.isUsedFiftyRides()) {
                        couponDiscount = 70;
                        customer.setUsedFiftyRides(true);
                    }
                    break;
            }


            int points = customer.getLoyaltyPoints();
            int discountPoints = (points / 100) * 100;
            double loyaltyDiscount = discountPoints / 10.0;


            customer.setLoyaltyPoints(points - discountPoints);


            double finalAmount = ride.getFare() - couponDiscount - loyaltyDiscount;
            if (finalAmount < 0) finalAmount = 0;


            int pointsEarned = (int) (finalAmount / 10.0);
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() + pointsEarned);


            customer.setCompletedRides(customer.getCompletedRides() + 1);


            ride.setStatus("COMPLETED");
            ride.setEndTime(new Date());


            if (ride.getDriver() != null) {
                ride.getDriver().setAvailable(true);
            }


            try {
                Query q = em.createQuery("DELETE FROM Payment p WHERE p.ride = :r");
                q.setParameter("r", ride).executeUpdate();
            } catch(Exception e) {}


            Payment payment = new Payment(ride, finalAmount, reqData.method);
            em.persist(payment);

            em.getTransaction().commit();


            resp.getWriter().write("{\"success\": true, \"loyaltyPoints\": " + customer.getLoyaltyPoints() + "}");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\": \"Payment failed: " + e.getMessage() + "\"}");
        } finally {
            em.close();
        }
    }

    static class PayRequest {
        Long rideId;
        double amount;
        String method;
        String coupon;
    }
}