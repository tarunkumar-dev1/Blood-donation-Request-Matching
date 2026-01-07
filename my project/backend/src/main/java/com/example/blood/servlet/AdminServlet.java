package com.example.blood.servlet;

import jakarta.servlet.http.*;
import java.io.IOException;
import com.example.blood.dao.AdminDao;
import com.example.blood.model.Match;
import com.example.blood.util.JsonUtil;

import java.sql.SQLException;
import java.util.*;

public class AdminServlet extends HttpServlet {
    private final AdminDao adminDao = new AdminDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        Map<String, Object> response = new HashMap<>();

        try {
            List<Match> matches = adminDao.getAllMatches();

            List<Map<String, Object>> matchList = new ArrayList<>();
            for (Match match : matches) {
                Map<String, Object> matchData = new HashMap<>();
                matchData.put("matchId", match.getMatchId());
                matchData.put("donorId", match.getDonorId());
                matchData.put("donorName", match.getDonorName());
                matchData.put("donorPhone", match.getDonorPhone());
                matchData.put("donorEmail", match.getDonorEmail());
                matchData.put("requestId", match.getRequestId());
                matchData.put("requestName", match.getRequestName());
                matchData.put("requestEmail", match.getRequestEmail());
                matchData.put("bloodType", match.getBloodType());
                matchData.put("locationMatch", match.isLocationMatch());
                matchData.put("requestNote", match.getRequestNote());
                matchData.put("status", match.getStatus());
                matchData.put("priority", match.getPriority());
                matchData.put("matchedAt", match.getMatchedAt());
                matchData.put("updatedAt", match.getUpdatedAt());

                matchList.add(matchData);
            }

            response.put("matches", matchList);
            response.put("availableDonors", adminDao.getTotalAvailableDonors());
            response.put("success", true);

        } catch (SQLException e) {
            res.setStatus(500);
            response.put("success", false);
            response.put("error", "Database error: " + e.getMessage());
        }

        res.getWriter().write(JsonUtil.toJson(response));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String uri = req.getRequestURI();
        String matchId = req.getParameter("matchId");
        Map<String, Object> response = new HashMap<>();

        if (matchId == null || matchId.isBlank()) {
            res.setStatus(400);
            response.put("success", false);
            response.put("error", "matchId is required");
            res.getWriter().write(JsonUtil.toJson(response));
            return;
        }

        try {
            Match match = adminDao.findMatchById(matchId);
            if (match == null) {
                res.setStatus(404);
                response.put("success", false);
                response.put("error", "Match not found");
                res.getWriter().write(JsonUtil.toJson(response));
                return;
            }

            if (uri.endsWith("/approve")) {
                adminDao.updateMatchStatus(matchId, "pending");
                response.put("message", "Match approved successfully");
            } else if (uri.endsWith("/complete")) {
                adminDao.updateMatchStatus(matchId, "completed");
                if (match.getRequestId() != null) {
                    adminDao.deleteRequestById(match.getRequestId());
                }
                response.put("message", "Match marked as completed");
            } else {
                res.setStatus(400);
                response.put("success", false);
                response.put("error", "Unknown action");
                res.getWriter().write(JsonUtil.toJson(response));
                return;
            }

            response.put("success", true);
            res.getWriter().write(JsonUtil.toJson(response));

        } catch (SQLException e) {
            res.setStatus(500);
            response.put("success", false);
            response.put("error", "Database error: " + e.getMessage());
            res.getWriter().write(JsonUtil.toJson(response));
        }
    }
}
