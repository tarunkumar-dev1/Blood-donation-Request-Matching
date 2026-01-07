package com.example.blood.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    // Use H2 file-based database for persistence with auto-server mode for multi-access
    // To use Oracle instead, set environment variable DB_TYPE=oracle
    private static final String DB_TYPE = System.getenv().getOrDefault("DB_TYPE", "h2");
    private static final String URL = DB_TYPE.equals("h2") 
        ? "jdbc:h2:./bloodapp;AUTO_SERVER=TRUE;MODE=Oracle"
        : System.getenv().getOrDefault("ORACLE_URL", "jdbc:oracle:thin:@localhost:1521/XEPDB1");
    private static final String USER = DB_TYPE.equals("h2")
        ? "sa"
        : System.getenv().getOrDefault("ORACLE_USER", "bloodapp");
    private static final String PASS = DB_TYPE.equals("h2")
        ? ""
        : System.getenv().getOrDefault("ORACLE_PASS", "bloodpass");

    private Db() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
