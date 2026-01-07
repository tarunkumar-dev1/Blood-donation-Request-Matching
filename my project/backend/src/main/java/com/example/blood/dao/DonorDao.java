package com.example.blood.dao;

import com.example.blood.model.Donor;
import com.example.blood.util.Db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DonorDao {
    private static final String CREATE_TABLE_SQL = 
        "CREATE TABLE IF NOT EXISTS donors (" +
        "id VARCHAR(64) PRIMARY KEY, " +
        "name VARCHAR(120) NOT NULL, " +
        "dob VARCHAR(20), " +
        "blood_type VARCHAR(8) NOT NULL, " +
        "phone VARCHAR(32) NOT NULL, " +
        "email VARCHAR(160) NOT NULL, " +
        "address VARCHAR(400), " +
        "pincode VARCHAR(16), " +
        "location VARCHAR(200), " +
        "center VARCHAR(160), " +
        "history VARCHAR(4000), " +
        "surgeries VARCHAR(4000), " +
        "status VARCHAR(20) DEFAULT 'approved', " +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static volatile boolean tableChecked = false;

    private void ensureTable(Connection conn) throws SQLException {
        if (tableChecked) return;
        try (PreparedStatement ps = conn.prepareStatement(CREATE_TABLE_SQL)) {
            ps.execute();
            // Make sure older rows default to approved and pending ones are auto-approved
            try (PreparedStatement update = conn.prepareStatement("UPDATE donors SET status='approved' WHERE status IS NULL OR status='pending'")) {
                update.executeUpdate();
            }
            tableChecked = true;
        }
    }

    public void save(Donor donor) throws SQLException {
        String sql = "INSERT INTO donors (id, name, dob, blood_type, phone, email, address, pincode, location, center, history, surgeries, status, created_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP)";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, donor.getId());
                ps.setString(2, donor.getName());
                ps.setString(3, donor.getDob());
                ps.setString(4, donor.getBloodType());
                ps.setString(5, donor.getPhone());
                ps.setString(6, donor.getEmail());
                ps.setString(7, donor.getAddress());
                ps.setString(8, donor.getPincode());
                ps.setString(9, donor.getLocation());
                ps.setString(10, donor.getCenter());
                ps.setString(11, donor.getHistory());
                ps.setString(12, donor.getSurgeries());
                ps.setString(13, donor.getStatus());
                ps.executeUpdate();
            }
        }
    }

    public List<Donor> findAll() throws SQLException {
        String sql = "SELECT id, name, dob, blood_type, phone, email, address, pincode, location, center, history, surgeries, status, created_at FROM donors ORDER BY created_at DESC";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                List<Donor> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        }
    }

    public List<Donor> findMatches(String bloodType, String pincode) throws SQLException {
        String sql = "SELECT id, name, dob, blood_type, phone, email, address, pincode, location, center, history, surgeries, status, created_at FROM donors WHERE ( ? IS NULL OR ? = '' OR LOWER(blood_type) = LOWER(?) ) AND ( ? IS NULL OR ? = '' OR pincode = ? ) ORDER BY created_at DESC";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, bloodType);
                ps.setString(2, bloodType);
                ps.setString(3, bloodType);
                ps.setString(4, pincode);
                ps.setString(5, pincode);
                ps.setString(6, pincode);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Donor> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(map(rs));
                    }
                    return list;
                }
            }
        }
    }

    private Donor map(ResultSet rs) throws SQLException {
        return new Donor(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("dob"),
                rs.getString("blood_type"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("pincode"),
                rs.getString("location"),
                rs.getString("center"),
                rs.getString("history"),
                rs.getString("surgeries"),
                rs.getString("status")
        );
    }

    public void updateStatus(String id, String status) throws SQLException {
        String sql = "UPDATE donors SET status = ? WHERE id = ?";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setString(2, id);
                ps.executeUpdate();
            }
        }
    }

    public List<Donor> findByStatus(String status) throws SQLException {
        String sql = "SELECT id, name, dob, blood_type, phone, email, address, pincode, location, center, history, surgeries, status, created_at FROM donors WHERE status = ? ORDER BY created_at DESC";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Donor> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(map(rs));
                    }
                    return list;
                }
            }
        }
    }
}
