package com.example.blood.servlet;

import com.example.blood.dao.DonorDao;
import com.example.blood.dao.AdminDao;
import com.example.blood.model.Donor;
import com.example.blood.model.Match;
import com.example.blood.util.JsonUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DonorServlet extends HttpServlet {
    private final DonorDao donorDao = new DonorDao();
    private final AdminDao adminDao = new AdminDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String name = req.getParameter("name");
        String dob = req.getParameter("dob");
        String bloodType = req.getParameter("bloodType");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String address = req.getParameter("address");
        String pincode = req.getParameter("pincode");
        String location = req.getParameter("location");
        String center = req.getParameter("center");
        String history = req.getParameter("history");
        String surgeries = req.getParameter("surgeries");

        if (isBlank(name) || isBlank(phone) || isBlank(email) || isBlank(bloodType)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"error\":\"name, phone, email, and bloodType are required\"}");
            }
            return;
        }

        Donor donor = new Donor(UUID.randomUUID().toString(), name, valueOrEmpty(dob), valueOrEmpty(bloodType),
            phone, email, valueOrEmpty(address), valueOrEmpty(pincode), valueOrEmpty(location), valueOrEmpty(center),
            valueOrEmpty(history), valueOrEmpty(surgeries), "approved");
        try {
            donorDao.save(donor);
            
            // Automatically find matching requests for this donor
            List<Match> matches = adminDao.findMatchesForDonor(donor);
            int matchCount = matches.size();
            
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"message\":\"Donor saved\",\"donor\":" + JsonUtil.donorToJson(donor) + 
                         ",\"matchesFound\":" + matchCount + "}");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // Log the actual error
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = resp.getWriter()) { 
                out.write("{\"error\":\"Database error: " + ex.getMessage() + "\"}"); 
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            List<Donor> donors = donorDao.findAll();
            try (PrintWriter out = resp.getWriter()) { out.write(JsonUtil.donorsToJson(donors)); }
        } catch (SQLException ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = resp.getWriter()) { out.write("{\"error\":\"Database error\"}"); }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String valueOrEmpty(String v) {
        return v == null ? "" : v.trim();
    }
}
