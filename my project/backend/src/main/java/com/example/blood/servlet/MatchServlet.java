package com.example.blood.servlet;

import com.example.blood.dao.DonorDao;
import com.example.blood.dao.MatchDao;
import com.example.blood.dao.AdminDao;
import com.example.blood.model.MatchRequest;
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

public class MatchServlet extends HttpServlet {
    private final MatchDao matchDao = new MatchDao();
    private final DonorDao donorDao = new DonorDao();
    private final AdminDao adminDao = new AdminDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String fullname = req.getParameter("fullname");
        String email = req.getParameter("email");
        String bloodtype = req.getParameter("bloodtype");
        String pincode = req.getParameter("pincode");
        String location = req.getParameter("location");
        String note = req.getParameter("note");

        // Validate all required fields are present and not empty
        if (isBlank(fullname)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"success\":false,\"error\":\"Full name is required\"}");
            }
            return;
        }
        
        if (isBlank(email)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"success\":false,\"error\":\"Email is required\"}");
            }
            return;
        }
        
        if (isBlank(bloodtype)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"success\":false,\"error\":\"Blood type is required\"}");
            }
            return;
        }
        
        // Validate email format
        if (!isValidEmail(email)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"success\":false,\"error\":\"Invalid email format\"}");
            }
            return;
        }

        MatchRequest request = new MatchRequest(UUID.randomUUID().toString(), fullname, email,
                bloodtype, valueOrEmpty(pincode), valueOrEmpty(location), valueOrEmpty(note), "pending");
        try {
            matchDao.save(request);
            
            // Find matching donors
            var matches = donorDao.findMatches(bloodtype, pincode);
            
            // Create admin matches for tracking
            List<Match> adminMatches = adminDao.findMatchesForRequest(request);
            int matchCount = adminMatches.size();
            
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"success\":true,\"request\":" + JsonUtil.matchToJson(request) + 
                         ",\"matches\":" + JsonUtil.donorsToJson(matches) + 
                         ",\"adminMatchesCreated\":" + matchCount + ",\"message\":\"Request submitted successfully\"}");
            }
        } catch (SQLException ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = resp.getWriter()) { out.write("{\"success\":false,\"error\":\"Database error: " + ex.getMessage() + "\"}"); }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            List<MatchRequest> requests = matchDao.findAll();
            try (PrintWriter out = resp.getWriter()) { out.write(JsonUtil.matchesToJson(requests)); }
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
    
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
