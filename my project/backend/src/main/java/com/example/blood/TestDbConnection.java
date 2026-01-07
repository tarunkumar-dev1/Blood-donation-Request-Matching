package com.example.blood;

import com.example.blood.util.Db;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDbConnection {
    public static void main(String[] args) {
        try {
            System.out.println("Testing Oracle Database Connection...");
            System.out.println("ORACLE_URL: " + System.getenv().getOrDefault("ORACLE_URL", "jdbc:oracle:thin:@localhost:1521/XEPDB1"));
            System.out.println("ORACLE_USER: " + System.getenv().getOrDefault("ORACLE_USER", "bloodapp"));
            System.out.println();
            
            Connection conn = Db.getConnection();
            System.out.println("✓ Connection Successful!");
            
            // Test 1: Count user_credentials
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM user_credentials");
            if (rs.next()) {
                System.out.println("✓ user_credentials table: " + rs.getInt("cnt") + " records");
            }
            
            // Test 2: Count donors
            rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM donors");
            if (rs.next()) {
                System.out.println("✓ donors table: " + rs.getInt("cnt") + " records");
            }
            
            // Test 3: Count match_requests
            rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM match_requests");
            if (rs.next()) {
                System.out.println("✓ match_requests table: " + rs.getInt("cnt") + " records");
            }
            
            // Test 4: Count contact_messages
            rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM contact_messages");
            if (rs.next()) {
                System.out.println("✓ contact_messages table: " + rs.getInt("cnt") + " records");
            }
            
            // Test 5: Sample data verification
            rs = stmt.executeQuery("SELECT number FROM user_credentials WHERE rownum = 1");
            if (rs.next()) {
                System.out.println("✓ Sample auth user: " + rs.getString("number"));
            }
            
            stmt.close();
            conn.close();
            System.out.println("\n✓✓✓ All Database Tests Passed! ✓✓✓");
            
        } catch (Exception e) {
            System.err.println("✗ Connection Failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
