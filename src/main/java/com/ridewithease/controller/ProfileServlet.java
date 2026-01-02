package com.ridewithease.controller;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    @PersistenceUnit(unitName = "ridewitheasePU")
    private EntityManagerFactory emf;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        EntityManager em = emf.createEntityManager();

        try {
            User user = em.find(User.class, userId);

            List<Ride> rides = em.createQuery(
                            "SELECT r FROM Ride r WHERE r.user.id = :uid ORDER BY r.id DESC",
                            Ride.class)
                    .setParameter("uid", userId)
                    .getResultList();

            request.setAttribute("user", user);
            request.setAttribute("rides", rides);

            request.getRequestDispatcher("profile.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }
}

