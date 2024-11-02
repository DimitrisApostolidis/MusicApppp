package org.example.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class DataBaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/music_apppp";
    private static final String USER = "root";
    private static final String PASSWORD = "";


    public boolean verifyCredentials(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Σύνδεση με τη βάση δεδομένων επιτυχής!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
