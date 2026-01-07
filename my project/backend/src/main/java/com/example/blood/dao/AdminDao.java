package com.example.blood.dao;

import com.example.blood.model.Match;
import com.example.blood.model.Donor;
import com.example.blood.model.MatchRequest;
import com.example.blood.util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminDao {

    public AdminDao() {
        try { 
            ensureMatchesTable();
            ensureMatchRequestsTable();
            ensureDonorsTable();
        } catch (SQLException ignored) { /* table creation best-effort */ }
    }

    private void ensureMatchesTable() throws SQLException {
        final String ddl = "CREATE TABLE IF NOT EXISTS matches (" +
                "match_id VARCHAR(64) PRIMARY KEY," +
                "donor_id VARCHAR(64)," +
                "donor_name VARCHAR(160)," +
                "donor_phone VARCHAR(32)," +
                "donor_email VARCHAR(160)," +
                "donor_pincode VARCHAR(16)," +
                "request_id VARCHAR(64)," +
                "request_name VARCHAR(160)," +
                "request_email VARCHAR(160)," +
                "request_pincode VARCHAR(16)," +
                "blood_type VARCHAR(8)," +
                "location_match NUMBER(1)," +
                "request_note VARCHAR(400)," +
                "status VARCHAR(32)," +
                "priority VARCHAR(32)," +
                "matched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(ddl)) {
            ps.execute();
        }
    }

    private void ensureMatchRequestsTable() throws SQLException {
        final String ddl = "CREATE TABLE IF NOT EXISTS match_requests (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "fullname VARCHAR(160) NOT NULL, " +
                "email VARCHAR(160) NOT NULL, " +
                "bloodtype VARCHAR(8) NOT NULL, " +
                "pincode VARCHAR(16), " +
                "location VARCHAR(200), " +
                "note VARCHAR(400), " +
                "status VARCHAR(20) DEFAULT 'pending', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(ddl)) {
            ps.execute();
        }
    }

    private void ensureDonorsTable() throws SQLException {
        final String ddl = "CREATE TABLE IF NOT EXISTS donors (" +
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
                "status VARCHAR(20) DEFAULT 'pending', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(ddl)) {
            ps.execute();
        }
    }

    private Match mapMatch(ResultSet rs) throws SQLException {
        Match match = new Match();
        match.setMatchId(rs.getString("match_id"));
        match.setDonorId(rs.getString("donor_id"));
        match.setDonorName(rs.getString("donor_name"));
        match.setDonorPhone(rs.getString("donor_phone"));
        match.setDonorEmail(rs.getString("donor_email"));
        match.setDonorPincode(rs.getString("donor_pincode"));
        match.setRequestId(rs.getString("request_id"));
        match.setRequestName(rs.getString("request_name"));
        match.setRequestEmail(rs.getString("request_email"));
        match.setRequestPincode(rs.getString("request_pincode"));
        match.setRequestNote(rs.getString("request_note"));
        match.setBloodType(rs.getString("blood_type"));
        match.setLocationMatch(rs.getInt("location_match") == 1);
        match.setPriority(rs.getString("priority"));
        match.setStatus(rs.getString("status"));
        Timestamp matchedAt = rs.getTimestamp("matched_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (matchedAt != null) { match.setMatchedAt(matchedAt.toLocalDateTime()); }
        if (updatedAt != null) { match.setUpdatedAt(updatedAt.toLocalDateTime()); }
        return match;
    }

    private boolean upsertMatch(Match match) throws SQLException {
        ensureMatchesTable();
        String existingId = null;
        String existingStatus = null;
        final String checkSql = "SELECT match_id, status FROM matches WHERE donor_id = ? AND request_id = ?";
        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, match.getDonorId());
            ps.setString(2, match.getRequestId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    existingId = rs.getString("match_id");
                    existingStatus = rs.getString("status");
                }
            }
        }

        if (existingId != null) {
            match.setMatchId(existingId);
            if (existingStatus != null) {
                match.setStatus(existingStatus);
            }
            final String updateSql = "UPDATE matches SET priority = ?, location_match = ?, request_note = ?, updated_at = CURRENT_TIMESTAMP WHERE match_id = ?";
            try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, match.getPriority());
                ps.setInt(2, match.isLocationMatch() ? 1 : 0);
                ps.setString(3, match.getRequestNote());
                ps.setString(4, match.getMatchId());
                ps.executeUpdate();
            }
            return false;
        }

        if (match.getMatchId() == null || match.getMatchId().isEmpty()) {
            match.setMatchId("M-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        match.setStatus(match.getStatus() == null ? "new" : match.getStatus());

        final String insertSql = "INSERT INTO matches (match_id, donor_id, donor_name, donor_phone, donor_email, donor_pincode, request_id, request_name, request_email, request_pincode, blood_type, location_match, request_note, status, priority, matched_at, updated_at) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)";
        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, match.getMatchId());
            ps.setString(2, match.getDonorId());
            ps.setString(3, match.getDonorName());
            ps.setString(4, match.getDonorPhone());
            ps.setString(5, match.getDonorEmail());
            ps.setString(6, match.getDonorPincode());
            ps.setString(7, match.getRequestId());
            ps.setString(8, match.getRequestName());
            ps.setString(9, match.getRequestEmail());
            ps.setString(10, match.getRequestPincode());
            ps.setString(11, match.getBloodType());
            ps.setInt(12, match.isLocationMatch() ? 1 : 0);
            ps.setString(13, match.getRequestNote());
            ps.setString(14, match.getStatus());
            ps.setString(15, match.getPriority());
            ps.executeUpdate();
        }
        return true;
    }

    private void seedMatchesIfEmpty() throws SQLException {
        ensureMatchesTable();
        boolean hasMatches = false;
        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM matches FETCH FIRST 1 ROWS ONLY"); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) { hasMatches = true; }
        }
        if (hasMatches) return;

        // Seed from existing requests and donors
        ensureDonorsTable();
        String requestsSql = "SELECT id, fullname, email, bloodtype, pincode, location, note, status FROM match_requests";
        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(requestsSql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MatchRequest req = new MatchRequest(
                        rs.getString("id"),
                        rs.getString("fullname"),
                        rs.getString("email"),
                        rs.getString("bloodtype"),
                        rs.getString("pincode"),
                        rs.getString("location"),
                        rs.getString("note"),
                        rs.getString("status")
                );
                findMatchesForRequest(req);
            }
        }
    }

    // Find matching donors for a blood request
    public List<Match> findMatchesForRequest(MatchRequest request) throws SQLException {
        ensureMatchesTable();
        ensureMatchRequestsTable();
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM donors WHERE blood_type = ? ORDER BY created_at DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, request.getBloodtype());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Match match = new Match();
                match.setDonorId(rs.getString("id"));
                match.setDonorName(rs.getString("name"));
                match.setDonorPhone(rs.getString("phone"));
                match.setDonorEmail(rs.getString("email"));
                match.setDonorPincode(rs.getString("pincode"));

                match.setRequestId(request.getId());
                match.setRequestName(request.getFullname());
                match.setRequestEmail(request.getEmail());
                match.setRequestPincode(request.getPincode());
                match.setRequestNote(request.getNote());

                match.setBloodType(request.getBloodtype());

                boolean locationMatch = rs.getString("pincode") != null &&
                                       rs.getString("pincode").equals(request.getPincode());
                match.setLocationMatch(locationMatch);

                if (locationMatch) {
                    match.setPriority("high");
                } else if (request.getNote() != null &&
                          (request.getNote().toLowerCase().contains("urgent") ||
                           request.getNote().toLowerCase().contains("emergency"))) {
                    match.setPriority("high");
                } else {
                    match.setPriority("medium");
                }

                upsertMatch(match);
                matches.add(match);
            }
        }

        return matches;
    }

    // Find matching requests for a new donor
    public List<Match> findMatchesForDonor(Donor donor) throws SQLException {
        ensureMatchesTable();
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM match_requests WHERE bloodtype = ? ORDER BY created_at DESC";

        try (Connection conn = Db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, donor.getBloodType());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Match match = new Match();
                match.setDonorId(donor.getId());
                match.setDonorName(donor.getName());
                match.setDonorPhone(donor.getPhone());
                match.setDonorEmail(donor.getEmail());
                match.setDonorPincode(donor.getPincode());

                match.setRequestId(rs.getString("id"));
                match.setRequestName(rs.getString("fullname"));
                match.setRequestEmail(rs.getString("email"));
                match.setRequestPincode(rs.getString("pincode"));
                match.setRequestNote(rs.getString("note"));

                match.setBloodType(donor.getBloodType());

                boolean locationMatch = donor.getPincode() != null &&
                                       donor.getPincode().equals(rs.getString("pincode"));
                match.setLocationMatch(locationMatch);

                String note = rs.getString("note");
                if (locationMatch) {
                    match.setPriority("high");
                } else if (note != null &&
                          (note.toLowerCase().contains("urgent") ||
                           note.toLowerCase().contains("emergency"))) {
                    match.setPriority("high");
                } else {
                    match.setPriority("medium");
                }

                upsertMatch(match);
                matches.add(match);
            }
        }

        return matches;
    }

    // Get all matches (in a real system, these would be stored in a matches table)
    public List<Match> getAllMatches() throws SQLException {
        ensureMatchesTable();

        // Always regenerate/upsert matches for current requests so manual DB inserts surface in UI
        String requestsSql = "SELECT id, fullname, email, bloodtype, pincode, location, note, status FROM match_requests";
        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(requestsSql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MatchRequest req = new MatchRequest(
                        rs.getString("id"),
                        rs.getString("fullname"),
                        rs.getString("email"),
                        rs.getString("bloodtype"),
                        rs.getString("pincode"),
                        rs.getString("location"),
                        rs.getString("note"),
                        rs.getString("status")
                );
                findMatchesForRequest(req); // upsert matches for this request against all donors
            }
        }

        List<Match> allMatches = new ArrayList<>();
        String sql = "SELECT * FROM matches ORDER BY matched_at DESC";
        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allMatches.add(mapMatch(rs));
            }
        }
        return allMatches;
    }

    // Get total available donors
    public int getTotalAvailableDonors() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM donors";
        try (Connection conn = Db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public boolean updateMatchStatus(String matchId, String status) throws SQLException {
        ensureMatchesTable();
        String sql = "UPDATE matches SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE match_id = ?";
        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, matchId);
            return ps.executeUpdate() > 0;
        }
    }

    public Match findMatchById(String matchId) throws SQLException {
        ensureMatchesTable();
        String sql = "SELECT * FROM matches WHERE match_id = ?";
        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMatch(rs);
                }
            }
        }
        return null;
    }

    public void deleteRequestById(String requestId) throws SQLException {
        String sql = "DELETE FROM match_requests WHERE id = ?";
        try (Connection conn = Db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, requestId);
            ps.executeUpdate();
        }
    }
}
