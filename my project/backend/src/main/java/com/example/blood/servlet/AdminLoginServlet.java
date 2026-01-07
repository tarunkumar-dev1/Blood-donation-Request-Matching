package com.example.blood.servlet;

import jakarta.servlet.http.*;
import java.io.IOException;
import com.example.blood.util.JsonUtil;
import java.util.*;

public class AdminLoginServlet extends HttpServlet {
    
    // Admin credentials from environment variables - MUST be set before deployment
    private static final String ADMIN_USERNAME = System.getenv().getOrDefault("ADMIN_USER", "admin");
    private static final String ADMIN_PASSWORD = System.getenv().getOrDefault("ADMIN_PASS", "admin007");
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate credentials
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            // Create session
            HttpSession session = req.getSession(true);
            session.setAttribute("adminLoggedIn", true);
            session.setAttribute("adminUsername", username);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            
            response.put("success", true);
            response.put("message", "Login successful");
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.put("success", false);
            response.put("error", "Invalid username or password");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        res.getWriter().write(JsonUtil.toJson(response));
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        
        Map<String, Object> response = new HashMap<>();
        HttpSession session = req.getSession(false);
        
        if (session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"))) {
            response.put("authenticated", true);
            response.put("loggedIn", true);
            response.put("username", session.getAttribute("adminUsername"));
        } else {
            response.put("authenticated", false);
            response.put("loggedIn", false);
        }
        
        res.getWriter().write(JsonUtil.toJson(response));
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        
        Map<String, Object> response = new HashMap<>();
        HttpSession session = req.getSession(false);
        
        if (session != null) {
            session.invalidate();
            response.put("success", true);
            response.put("message", "Logged out successfully");
        } else {
            response.put("success", false);
            response.put("message", "No active session");
        }
        
        res.getWriter().write(JsonUtil.toJson(response));
    }
}
