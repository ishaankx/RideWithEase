package com.ridewithease.controller;
import com.google.gson.Gson;
import com.ridewithease.model.*;
import jakarta.persistence.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/payments")
public class PaymentServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("rideEasePU");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        PayRequest reqData = new Gson().fromJson(req.getReader(), PayRequest.class);
        Ride ride = em.find(Ride.class, reqData.rideId);

        Customer customer = ride.getCustomer();

        // Apply selected coupon discount
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
                if (customer.getCompletedRides() == 10 && !customer.isUsedTenRides()) {
                    couponDiscount = 50;
                    customer.setUsedTenRides(true);
                }
                break;
            case "50rides":
                if (customer.getCompletedRides() == 50 && !customer.isUsedFiftyRides()) {
                    couponDiscount = 70;
                    customer.setUsedFiftyRides(true);
                }
                break;
        }

        // Apply loyalty discount
        int points = customer.getLoyaltyPoints();
        int discountPoints = (points / 100) * 100;
        double loyaltyDiscount = discountPoints / 10.0;
        customer.setLoyaltyPoints(points - discountPoints);

        double finalAmount = ride.getFare() - couponDiscount - loyaltyDiscount;

        try {
            Query q = em.createQuery("DELETE FROM Payment p WHERE p.ride = :r");
            q.setParameter("r", ride).executeUpdate();
        } catch(Exception e) {}

        Payment payment = new Payment(ride, finalAmount, reqData.method);
        em.persist(payment);

        em.getTransaction().commit();
        resp.getWriter().write("{\"success\": true, \"loyaltyPoints\": " + customer.getLoyaltyPoints() + "}");
    }
    static class PayRequest { Long rideId; double amount; String method; String coupon; }
}
