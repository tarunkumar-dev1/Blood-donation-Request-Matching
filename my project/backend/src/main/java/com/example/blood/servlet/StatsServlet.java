package com.example.blood.servlet;

import jakarta.servlet.http.*;
import java.io.IOException;
import com.example.blood.util.Db;
import com.example.blood.util.JsonUtil;
import java.sql.*;
import java.util.*;

public class StatsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = Db.getConnection()) {
            // Get total approved donors count
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM donors WHERE status = 'approved'")) {
                if (rs.next()) {
                    stats.put("totalDonors", rs.getInt("total"));
                }
            }
            
            // Get total approved match requests (active requests)
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM match_requests WHERE status = 'approved'")) {
                if (rs.next()) {
                    stats.put("activeRequests", rs.getInt("total"));
                }
            }
            
            // Get blood type distribution (only approved donors)
            Map<String, Integer> bloodAvailability = new HashMap<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT blood_type, COUNT(*) as count FROM donors WHERE status = 'approved' GROUP BY blood_type")) {
                while (rs.next()) {
                    bloodAvailability.put(rs.getString("blood_type"), rs.getInt("count"));
                }
            }
            stats.put("bloodAvailability", bloodAvailability);
            
            int totalDonors = (Integer) stats.getOrDefault("totalDonors", 0);

            // Completed matches = successful matches
            int completedMatches = 0;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM matches WHERE LOWER(status) = 'completed'")) {
                if (rs.next()) {
                    completedMatches = rs.getInt("total");
                }
            } catch (SQLException ignored) {
                // matches table may not exist on first startup; ignore and treat as zero
            }

            stats.put("successfulMatches", completedMatches);
            stats.put("livesSaved", completedMatches * 3); // estimate: each completed match saves 3 lives
            
            stats.put("success", true);
            res.getWriter().write(JsonUtil.toJson(stats));
            
        } catch (SQLException e) {
            res.setStatus(500);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Database error: " + e.getMessage());
            res.getWriter().write(JsonUtil.toJson(error));
        }
    }
}
