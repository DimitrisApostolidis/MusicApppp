package org.example.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataBaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/Rapsodiaplayer";
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

    // Μέθοδος εγγραφής χρήστη με έλεγχο για ύπαρξη
    public boolean registerUser(String username, String email, String password) {
        String checkUserQuery = "SELECT * FROM user WHERE username = ? OR email = ?";
        String insertUserQuery = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = this.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery)) {

            // Έλεγχος αν ο χρήστης ή το email υπάρχει ήδη
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Ο χρήστης ή το email υπάρχει ήδη
                System.out.println("Ο χρήστης ή το email υπάρχει ήδη.");
                return false; // Δηλώνει αποτυχία εγγραφής
            }

            // Εισαγωγή νέου χρήστη
            insertStmt.setString(1, username);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password); // Σημείωση: Προσθέστε κρυπτογράφηση κωδικού εδώ για λόγους ασφαλείας
            insertStmt.executeUpdate();

            System.out.println("Η εγγραφή ήταν επιτυχής!");
            return true; // Επιτυχής εγγραφή
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

    public boolean insertArtist(String artistName, String bio, String genre) {
        String insertArtistQuery = "INSERT INTO artist (name, bio, genre) VALUES (?, ?, ?)";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertArtistQuery)) {
            stmt.setString(1, artistName);
            stmt.setString(2, bio);
            stmt.setString(3, genre);
            stmt.executeUpdate();
            System.out.println("Καλλιτέχνης προστέθηκε με επιτυχία!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
