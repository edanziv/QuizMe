package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBHandler {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/QuizMe";
    private static final String USER = "root";
    private static final String PASSWORD = "edan2000";

    public static boolean checkUserCredentials(String email, String password) throws SQLException {
        String isUserExist = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(isUserExist)) {
            stmt.setString(1, email);
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

    public static boolean registerUser(String email, String password) throws SQLException {
        String isUserExist = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(isUserExist)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // User already exists
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            throw e; // Re-throw the exception to handle it in the calling method
        }

        String insertUser = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(insertUser)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            int res = stmt.executeUpdate();
            return res > 0; // User registered successfully
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            throw e;
        }
    }

    public static String getUserId(String email) throws SQLException {
        String getUserId = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(getUserId)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userId = rs.getString("id");
                    return userId;
                } else {
                    throw new SQLException("User not found: " + email);
                }
            }
        }
    }

    // public static String getFileId(String email) throws SQLException {
    //     String getUserId = "SELECT id FROM users WHERE email = ?";
    //     try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
    //             PreparedStatement stmt = conn.prepareStatement(getUserId)) {
    //         stmt.setString(1, email);
    //         try (ResultSet rs = stmt.executeQuery()) {
    //             if (rs.next()) {
    //                 String userId = rs.getString("id");
    //                 return userId;
    //             } else {
    //                 throw new SQLException("User not found: " + email);
    //             }
    //         }
    //     }
    // }

    public static void insertFile(String fileName, String email, String questions, String answers)
            throws SQLException, TimeoutException {
        String userId;
        try {
            userId = getUserId(email);
            if (userId == null) {
                throw new SQLException("User ID not found for user: " + email);
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

    public static List<Map<String, String>> getFiles(String email) throws SQLException {
        String userId = getUserId(email);
        if (userId == null) {
            throw new SQLException("User ID not found for user: " + email);
        }

        List<Map<String, String>> files = new ArrayList<>();
        String getFilesQuery = "SELECT * FROM files WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(getFilesQuery)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> file = new java.util.HashMap<>();
                    file.put("id", rs.getString("id"));
                    file.put("user_id", rs.getString("user_id"));
                    file.put("file_name", rs.getString("file_name"));
                    file.put("questions", rs.getString("questions"));
                    file.put("answers", rs.getString("answers"));
                    files.add(file);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving files: " + e.getMessage());
            throw e;
        }
        return files;
    }
}
