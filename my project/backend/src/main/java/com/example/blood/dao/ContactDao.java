package com.example.blood.dao;

import com.example.blood.model.ContactMessage;
import com.example.blood.util.Db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactDao {
    private static final String CREATE_TABLE_SQL = 
        "CREATE TABLE IF NOT EXISTS contact_messages (" +
        "id VARCHAR(64) PRIMARY KEY, " +
        "name VARCHAR(120) NOT NULL, " +
        "email VARCHAR(160) NOT NULL, " +
        "message VARCHAR(4000) NOT NULL, " +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static volatile boolean tableChecked = false;

    private void ensureTable(Connection conn) throws SQLException {
        if (tableChecked) return;
        try (PreparedStatement ps = conn.prepareStatement(CREATE_TABLE_SQL)) {
            ps.execute();
            tableChecked = true;
        }
    }

    public void save(ContactMessage msg) throws SQLException {
        String sql = "INSERT INTO contact_messages (id, name, email, message, created_at) VALUES (?,?,?,?,CURRENT_TIMESTAMP)";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, msg.getId());
                ps.setString(2, msg.getName());
                ps.setString(3, msg.getEmail());
                ps.setString(4, msg.getMessage());
                ps.executeUpdate();
            }
        }
    }

    public List<ContactMessage> findAll() throws SQLException {
        String sql = "SELECT id, name, email, message, created_at FROM contact_messages ORDER BY created_at DESC";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                List<ContactMessage> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        }
    }

    private ContactMessage map(ResultSet rs) throws SQLException {
        return new ContactMessage(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("message")
        );
    }
}
