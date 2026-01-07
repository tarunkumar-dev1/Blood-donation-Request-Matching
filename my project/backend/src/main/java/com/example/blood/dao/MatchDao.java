package com.example.blood.dao;

import com.example.blood.model.MatchRequest;
import com.example.blood.util.Db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MatchDao {
    private static final String CREATE_TABLE_SQL = 
        "CREATE TABLE IF NOT EXISTS match_requests (" +
        "id VARCHAR(64) PRIMARY KEY, " +
        "fullname VARCHAR(160) NOT NULL, " +
        "email VARCHAR(160) NOT NULL, " +
        "bloodtype VARCHAR(8) NOT NULL, " +
        "pincode VARCHAR(16), " +
        "location VARCHAR(200), " +
        "note VARCHAR(400), " +
        "status VARCHAR(20) DEFAULT 'pending', " +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static volatile boolean tableChecked = false;

    private void ensureTable(Connection conn) throws SQLException {
        if (tableChecked) return;
        try (PreparedStatement ps = conn.prepareStatement(CREATE_TABLE_SQL)) {
            ps.execute();
            tableChecked = true;
        }
    }

    public void save(MatchRequest req) throws SQLException {
        String sql = "INSERT INTO match_requests (id, fullname, email, bloodtype, pincode, location, note, status, created_at) VALUES (?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP)";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, req.getId());
                ps.setString(2, req.getFullname());
                ps.setString(3, req.getEmail());
                ps.setString(4, req.getBloodtype());
                ps.setString(5, req.getPincode());
                ps.setString(6, req.getLocation());
                ps.setString(7, req.getNote());
                ps.setString(8, req.getStatus());
                ps.executeUpdate();
            }
        }
    }

    public List<MatchRequest> findAll() throws SQLException {
        String sql = "SELECT id, fullname, email, bloodtype, pincode, location, note, status, created_at FROM match_requests ORDER BY created_at DESC";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                List<MatchRequest> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        }
    }

    private MatchRequest map(ResultSet rs) throws SQLException {
        return new MatchRequest(
                rs.getString("id"),
                rs.getString("fullname"),
                rs.getString("email"),
                rs.getString("bloodtype"),
                rs.getString("pincode"),
                rs.getString("location"),
                rs.getString("note"),
                rs.getString("status")
        );
    }

    public void updateStatus(String id, String status) throws SQLException {
        String sql = "UPDATE match_requests SET status = ? WHERE id = ?";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setString(2, id);
                ps.executeUpdate();
            }
        }
    }

    public List<MatchRequest> findByStatus(String status) throws SQLException {
        String sql = "SELECT id, fullname, email, bloodtype, pincode, location, note, status, created_at FROM match_requests WHERE status = ? ORDER BY created_at DESC";
        try (Connection conn = Db.getConnection()) {
            ensureTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                try (ResultSet rs = ps.executeQuery()) {
                    List<MatchRequest> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(map(rs));
                    }
                    return list;
                }
            }
        }
    }
}
