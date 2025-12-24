package com.ridewithease.controller;

import com.google.gson.Gson;
import com.ridewithease.model.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.Key;
import java.util.List;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("rideEasePU");
    private Gson gson = new Gson();
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        EntityManager em = emf.createEntityManager();

        try {
            AuthRequest data = gson.fromJson(req.getReader(), AuthRequest.class);

            if ("/register".equals(path)) {
                em.getTransaction().begin();
                User user;
                if ("DRIVER".equalsIgnoreCase(data.role)) {
                    Vehicle v = new Vehicle(data.make, data.model, data.plate, data.color, data.year);
                    user = new Driver(data.fullName, data.email, data.phone, data.password, data.license, v);
                } else {
                    user = new Customer(data.fullName, data.email, data.phone, data.password);
                }
                em.persist(user);
                em.getTransaction().commit();
                resp.getWriter().write(gson.toJson(user));

            } else if ("/login".equals(path)) {
                List<User> users = em.createQuery("SELECT u FROM User u WHERE u.email = :e AND u.password = :p", User.class)
                        .setParameter("e", data.email).setParameter("p", data.password).getResultList();

                if (!users.isEmpty()) {
                    User u = users.get(0);

                    String token = Jwts.builder()
                            .setSubject(u.getEmail())
                            .claim("role", u.getRole())
                            .signWith(KEY)
                            .compact();

                    // --- FIX IS HERE ---
                    // We manually add "role" because Gson ignores getRole() since it's not a field.
                    String userJson = gson.toJson(u);
                    String jsonResponse = "{\"token\": \"" + token + "\", \"role\": \"" + u.getRole() + "\", \"user\": " + userJson + "}";

                    resp.getWriter().write(jsonResponse);
                } else {
                    resp.setStatus(401);
                }
            }
        } finally {
            em.close();
        }
    }

    static class AuthRequest {
        String fullName, email, phone, password, role;
        // Driver specific
        String license, make, model, plate, color;
        int year;
    }
}