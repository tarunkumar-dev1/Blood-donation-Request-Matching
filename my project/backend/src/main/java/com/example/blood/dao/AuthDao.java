package com.example.blood.dao;

import com.example.blood.model.AuthUser;
import com.example.blood.util.Db;
import com.example.blood.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AuthDao {
    // Create table on first use if it doesn't exist (idempotent)
    private static final String ENSURE_TABLE_SQL = 
            "CREATE TABLE IF NOT EXISTS user_credentials (" +
            "number VARCHAR(32) PRIMARY KEY, " +
            "password VARCHAR(128) NOT NULL)";

    private static volatile boolean tableChecked = false;

    private void ensureTable(Connection conn) throws SQLException {
        if (tableChecked) return;
        try (PreparedStatement ps = conn.prepareStatement(ENSURE_TABLE_SQL)) {
            ps.execute();
            tableChecked = true;
        }
    }

    private static String normalize(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() > 10) {
            digits = digits.substring(digits.length() - 10);
        }
        if (digits.length() == 10) {
            return "+91" + digits;
        }
        return "+" + digits;
    }

    public void save(AuthUser user) throws SQLException {
        String normalizedPhone = normalize(user.getPhone());
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            
            // Use MERGE for H2 (standard SQL)
            try (PreparedStatement ps = conn.prepareStatement(
                    "MERGE INTO user_credentials (number, password) KEY(number) VALUES (?, ?)")) {
                ps.setString(1, normalizedPhone);
                ps.setString(2, hashedPassword);
                ps.executeUpdate();
            }
        }
    }

    public Optional<AuthUser> find(String phone, String password) throws SQLException {
        String normalizedPhone = normalize(phone);
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT number, password FROM user_credentials " +
                    "WHERE REGEXP_REPLACE(number, '[^0-9]', '') = REGEXP_REPLACE(?, '[^0-9]', '')")) {
                ps.setString(1, normalizedPhone);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String hashedPassword = rs.getString("password");
                        // Verify the provided password against the stored hash
                        if (PasswordUtil.verifyPassword(password, hashedPassword)) {
                            return Optional.of(new AuthUser(rs.getString("number"), hashedPassword));
                        }
                    }
                    return Optional.empty();
                }
            }
        }
    }
}
