package com.musicmanagerapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button signInButton;

    @FXML
    private Button createAccountButton;

    // Στοιχεία σύνδεσης για τη βάση δεδομένων
    private final String DB_URL = "jdbc:mysql://localhost:3306/MusicManagerDB";
    private final String DB_USER = "root"; // το username της βάσης
    private final String DB_PASSWORD = "your_password"; // το password της βάσης

    // Μέθοδος για έλεγχο εγκυρότητας password
    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }

    // Μέθοδος για σύνδεση με τη βάση δεδομένων
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Έλεγχος αν ο χρήστης είναι καταχωρημένος
    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // Θα πρέπει να αποθηκεύεται κρυπτογραφημένος ο κωδικός

            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // true αν βρέθηκε χρήστης

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Διαχείριση Sign In
    @FXML
    private void handleSignIn() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
        } else if (!isPasswordValid(password)) {
            errorLabel.setText("Password must be at least 6 characters.");
        } else {
            if (authenticateUser(username, password)) {
                errorLabel.setText("Login successful!");
                // Μετάβαση στην κύρια οθόνη της εφαρμογής
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        }
    }

    // Διαχείριση Create Account
    @FXML
    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
        } else if (!isPasswordValid(password)) {
            errorLabel.setText("Password must be at least 6 characters.");
        } else {
            // Προσθήκη του νέου χρήστη στη βάση δεδομένων
            String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

                pstmt.setString(1, username);
                pstmt.setString(2, password); // Να αποθηκεύεται κρυπτογραφημένος

                pstmt.executeUpdate();
                errorLabel.setText("Account created successfully!");

            } catch (SQLException e) {
                e.printStackTrace();
                errorLabel.setText("Error: Account creation failed.");
            }
        }
    }
}
