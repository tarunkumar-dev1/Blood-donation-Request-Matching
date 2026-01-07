package com.example.blood.servlet;

import com.example.blood.dao.ContactDao;
import com.example.blood.model.ContactMessage;
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

public class ContactServlet extends HttpServlet {
    private final ContactDao contactDao = new ContactDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String message = req.getParameter("message");

        if (isBlank(name) || isBlank(email) || isBlank(message)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"error\":\"name, email and message are required\"}");
            }
            return;
        }

        ContactMessage contact = new ContactMessage(UUID.randomUUID().toString(), name, email, message);
        try {
            contactDao.save(contact);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"message\":\"Received\",\"data\":" + JsonUtil.contactToJson(contact) + "}");
            }
        } catch (SQLException ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = resp.getWriter()) { out.write("{\"error\":\"Database error\"}"); }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            List<ContactMessage> messages = contactDao.findAll();
            try (PrintWriter out = resp.getWriter()) { out.write(JsonUtil.contactsToJson(messages)); }
        } catch (SQLException ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = resp.getWriter()) { out.write("{\"error\":\"Database error\"}"); }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
