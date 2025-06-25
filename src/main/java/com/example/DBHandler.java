package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBHandler {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/QuizMe";
    private static final String USER = "root";
    private static final String PASSWORD = "edan2000";

    public static boolean checkUserCredentials(String username, String password) throws SQLException {
        String isUserExist = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(isUserExist)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { // A RuleSet cursor is positioned before the first row, so we need to call
                                 // next() to move to the first row
                    return rs.getInt(1) > 0; // retreives the first column of the first row
                }
            }
        }
        return false;
    }

    public static boolean registerUser(String username, String password) throws SQLException {
        String isUserExist = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(isUserExist)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // User already exists
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            throw e; // Re-throw the exception to handle it in the calling method
        }

        String insertUser = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(insertUser)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            int res = stmt.executeUpdate();
            return res > 0; // User registered successfully
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            throw e;
        }
    }

    public static String getUserId(String userName) throws SQLException {
        String getUserId = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(getUserId)) {
            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userId = rs.getString("id");
                    return userId;
                } else {
                    throw new SQLException("User not found: " + userName);
                }
            }
        }
    }

    public static String getFileId(String userName) throws SQLException {
        String getUserId = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(getUserId)) {
            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userId = rs.getString("id");
                    return userId;
                } else {
                    throw new SQLException("User not found: " + userName);
                }
            }
        }
    }

    public static void insertFile(String fileName, String userName, String questions, String answers)
            throws SQLException, TimeoutException {
        String userId;
        try {
            userId = getUserId(userName);
            if (userId == null) {
                throw new SQLException("User ID not found for user: " + userName);
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving user ID: " + e.getMessage(), e);
        }
        try {
            String insertFile = "INSERT INTO files (file_name, user_id, questions, answers) VALUES (?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement(insertFile)) { // try with resources to ensure
                                                                                  // connection is closed
                stmt.setString(1, fileName);
                stmt.setString(2, userId);
                stmt.setString(3, questions);
                stmt.setString(4, answers);
                stmt.executeUpdate();
            }
        } catch (SQLException err) {
            System.err.println("Error saving file: " + err.getMessage());
            throw err;
        }

    }
}
