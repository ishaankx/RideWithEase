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


        try {
            Query q = em.createQuery("DELETE FROM Payment p WHERE p.ride = :r");
            q.setParameter("r", ride).executeUpdate();
        } catch(Exception e) {}

        Payment payment = new Payment(ride, reqData.amount, reqData.method);
        em.persist(payment);

        em.getTransaction().commit();
        em.close();
    }
    static class PayRequest { Long rideId; double amount; String method; }
}