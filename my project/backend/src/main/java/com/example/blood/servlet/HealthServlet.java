package com.example.blood.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.write("{\"status\":\"ok\",\"ts\":\"" + Instant.now().toString() + "\"}");
        }
    }
}
