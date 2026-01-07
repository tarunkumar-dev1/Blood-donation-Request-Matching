package com.example.blood.servlet;

import com.example.blood.dao.DonorDao;
import com.example.blood.dao.MatchDao;
import com.example.blood.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ApprovalServlet extends HttpServlet {
    private final DonorDao donorDao = new DonorDao();
    private final MatchDao matchDao = new MatchDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String type = req.getParameter("type"); // "donor" or "match"
        String id = req.getParameter("id");
        String action = req.getParameter("action"); // "approve" or "reject"

        Map<String, Object> response = new HashMap<>();

        if (isBlank(type) || isBlank(id) || isBlank(action)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("error", "type, id, and action are required");
            resp.getWriter().write(JsonUtil.toJson(response));
            return;
        }

        String status = "approve".equalsIgnoreCase(action) ? "approved" : "rejected";

        try {
            if ("donor".equalsIgnoreCase(type)) {
                donorDao.updateStatus(id, status);
                response.put("success", true);
                response.put("message", "Donor " + status);
            } else if ("match".equalsIgnoreCase(type)) {
                matchDao.updateStatus(id, status);
                response.put("success", true);
                response.put("message", "Match request " + status);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.put("success", false);
                response.put("error", "Invalid type. Must be 'donor' or 'match'");
            }

            resp.getWriter().write(JsonUtil.toJson(response));

        } catch (SQLException ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.put("success", false);
            response.put("error", "Database error: " + ex.getMessage());
            resp.getWriter().write(JsonUtil.toJson(response));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String type = req.getParameter("type"); // "donor" or "match"
        String status = req.getParameter("status"); // "pending", "approved", "rejected"

        Map<String, Object> response = new HashMap<>();

        try {
            if ("donor".equalsIgnoreCase(type)) {
                if (status != null) {
                    response.put("donors", JsonUtil.donorsToJson(donorDao.findByStatus(status)));
                } else {
                    response.put("donors", JsonUtil.donorsToJson(donorDao.findAll()));
                }
            } else if ("match".equalsIgnoreCase(type)) {
                if (status != null) {
                    response.put("matches", JsonUtil.matchesToJson(matchDao.findByStatus(status)));
                } else {
                    response.put("matches", JsonUtil.matchesToJson(matchDao.findAll()));
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.put("success", false);
                response.put("error", "type parameter required: 'donor' or 'match'");
                resp.getWriter().write(JsonUtil.toJson(response));
                return;
            }

            response.put("success", true);
            resp.getWriter().write(JsonUtil.toJson(response));

        } catch (SQLException ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.put("success", false);
            response.put("error", "Database error: " + ex.getMessage());
            resp.getWriter().write(JsonUtil.toJson(response));
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
