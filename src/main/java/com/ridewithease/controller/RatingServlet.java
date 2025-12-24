package com.ridewithease.controller;

import com.google.gson.Gson;
import com.ridewithease.model.*;
import jakarta.persistence.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/ratings")
public class RatingServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("rideEasePU");
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EntityManager em = emf.createEntityManager();
        try {
            RatingRequest data = gson.fromJson(req.getReader(), RatingRequest.class);
            em.getTransaction().begin();

            Ride ride = em.find(Ride.class, data.rideId);



            Rating rating = new Rating(ride, data.givenBy, data.givenTo, data.score, data.comment);
            em.persist(rating);

            em.getTransaction().commit();
            resp.getWriter().write("{\"status\":\"success\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        } finally {
            em.close();
        }
    }

    static class RatingRequest {
        Long rideId, givenBy, givenTo;
        int score;
        String comment;
    }
}