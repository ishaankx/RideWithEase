package com.ridewithease.controller;

import com.google.gson.Gson;
import com.ridewithease.model.*;
import jakarta.persistence.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/rides/*")
public class RideServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("rideEasePU");
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EntityManager em = emf.createEntityManager();
        String path = req.getPathInfo();
        resp.setContentType("application/json");

        try {
            if ("/available".equals(path)) {

                List<Ride> rides = em.createQuery(
                        "SELECT r FROM Ride r JOIN FETCH r.customer WHERE r.status = 'REQUESTED'",
                        Ride.class
                ).getResultList();

                for (Ride r : rides) {
                    if (r.getCustomer() != null) {
                        r.getCustomer().setAverageRating(getAvgRating(em, r.getCustomer().getId()));
                    }

                    r.setOtp(null);
                }
                resp.getWriter().write(gson.toJson(rides));

            } else if ("/calculate".equals(path)) {

                double dist = Double.parseDouble(req.getParameter("distance"));
                resp.getWriter().write("{\"fare\": " + (dist * 10.0) + "}");

            } else if ("/status".equals(path)) {

                String idParam = req.getParameter("id");
                if (idParam != null) {
                    Long id = Long.parseLong(idParam);
                    Ride ride = em.find(Ride.class, id);
                    if (ride != null) {
                        if (ride.getDriver() != null) {
                            ride.getDriver().setAverageRating(getAvgRating(em, ride.getDriver().getId()));
                        }

                        resp.getWriter().write(gson.toJson(ride));
                    } else {
                        resp.setStatus(404);
                        resp.getWriter().write("{\"error\": \"Ride not found\"}");
                    }
                }
            }
        } finally {
            em.close();
        }
    }

    private double getAvgRating(EntityManager em, Long userId) {
        try {
            Double avg = em.createQuery("SELECT AVG(r.score) FROM Rating r WHERE r.givenTo = :uid", Double.class)
                    .setParameter("uid", userId)
                    .getSingleResult();
            return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        EntityManager em = emf.createEntityManager();
        resp.setContentType("application/json");

        try {
            RideRequest data = gson.fromJson(req.getReader(), RideRequest.class);
            em.getTransaction().begin();

            if ("/book".equals(path)) {

                if (data.customerId == null) {
                    resp.setStatus(400);
                    resp.getWriter().write("{\"error\": \"User not logged in or invalid ID\"}");
                    return;
                }

                Customer c = em.find(Customer.class, data.customerId);
                if (c == null) {
                    resp.setStatus(400);
                    resp.getWriter().write("{\"error\": \"Customer account not found in DB\"}");
                    return;
                }

                Ride ride = new Ride(
                        c,
                        data.pickup, data.drop,
                        data.pickupLat, data.pickupLng,
                        data.dropLat, data.dropLng,
                        data.distance, data.fare, data.duration
                );
                em.persist(ride);
                em.getTransaction().commit();
                resp.getWriter().write(gson.toJson(ride));

            } else if ("/accept".equals(path)) {

                Ride ride = em.find(Ride.class, data.rideId);
                Driver driver = em.find(Driver.class, data.driverId);

                if (ride != null && driver != null) {
                    ride.setDriver(driver);
                    ride.setStatus("ACCEPTED");
                    driver.setAvailable(false);
                    em.getTransaction().commit();
                    resp.getWriter().write(gson.toJson(ride));
                } else {
                    em.getTransaction().rollback();
                    resp.setStatus(400);
                    resp.getWriter().write("{\"error\": \"Ride or Driver not found\"}");
                }

            } else if ("/updateStatus".equals(path)) {

                Ride ride = em.find(Ride.class, data.rideId);
                if (ride != null) {


                    if ("ONGOING".equals(data.status)) {

                        if (data.otp == null || !data.otp.equals(ride.getOtp())) {
                            em.getTransaction().rollback();
                            resp.setStatus(403);
                            resp.getWriter().write("{\"error\": \"Invalid OTP\"}");
                            return;
                        }
                        ride.setStartTime(new java.util.Date());
                    }

                    ride.setStatus(data.status);

                    if ("COMPLETED".equals(data.status)) {
                        ride.setEndTime(new java.util.Date());
                        if (ride.getDriver() != null) {
                            ride.getDriver().setAvailable(true);
                        }
                    }
                    em.getTransaction().commit();
                    resp.getWriter().write(gson.toJson(ride));
                } else {
                    em.getTransaction().rollback();
                    resp.setStatus(404);
                }
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } finally {
            em.close();
        }
    }

    static class RideRequest {
        Long customerId, driverId, rideId;
        String pickup, drop, duration, status;


        String otp;

        double distance, fare;
        double pickupLat, pickupLng, dropLat, dropLng;
    }
}