package com.example.blood.servlet;

import com.example.blood.dao.AuthDao;
import com.example.blood.model.AuthUser;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthServlet extends HttpServlet {
    private final AuthDao authDao = new AuthDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        if (isBlank(phone) || isBlank(password)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"error\":\"Phone and password are required\"}");
            }
            return;
        }

        boolean isLogin = Boolean.parseBoolean(req.getParameter("login"));
        try {
            if (isLogin) {
                var user = authDao.find(phone, password);
                if (user.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    try (PrintWriter out = resp.getWriter()) {
                        out.write("{\"error\":\"Invalid credentials\"}");
                    }
                    return;
                }
            } else {
                authDao.save(new AuthUser(phone, password));
            }
        } catch (SQLException ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"error\":\"Database error\"}");
            }
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("userPhone", phone);
        String token = UUID.randomUUID().toString();
        session.setAttribute("token", token);

        try (PrintWriter out = resp.getWriter()) {
            out.write("{\"phone\":\"" + phone + "\",\"token\":\"" + token + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userPhone") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"authenticated\":false}");
            }
            return;
        }
        String phone = String.valueOf(session.getAttribute("userPhone"));
        String token = String.valueOf(session.getAttribute("token"));
        try (PrintWriter out = resp.getWriter()) {
            out.write("{\"authenticated\":true,\"phone\":\"" + phone + "\",\"token\":\"" + token + "\"}");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
