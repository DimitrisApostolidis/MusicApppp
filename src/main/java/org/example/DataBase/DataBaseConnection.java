package org.example.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataBaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/music_apppp";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private int failedAttempts = 0; // Μετρητής αποτυχημένων προσπαθειών

    // Επαλήθευση διαπιστευτηρίων
    public boolean verifyCredentials(String username, String password) {
        String query = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                failedAttempts = 0; // Επαναφορά των αποτυχημένων προσπαθειών στην επιτυχημένη σύνδεση
                return true;
            } else {
                failedAttempts++;
                System.out.println("Λανθασμένος κωδικός. Αποτυχημένες προσπάθειες: " + failedAttempts);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Μέθοδος εγγραφής χρήστη
    public boolean registerUser(String username, String email, String password) {
        String query = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Σύνδεση με τη βάση δεδομένων
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