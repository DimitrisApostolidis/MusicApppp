package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.DataBase.DataBaseConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignUpController {
    private static final Logger logger = Logger.getLogger(SignUpController.class.getName());
    @FXML
    private TextField username_fld;
    @FXML
    private TextField email_fld;
    @FXML
    private TextField password_fld;
    @FXML
    private Button createAccount_btn;
    @FXML
    private Button backToLogin_btn;
    @FXML
    private Label error_lbl;

    private DataBaseConnection dataBaseConnection;

    @FXML
    public void initialize() {
        dataBaseConnection = new DataBaseConnection();
        createAccount_btn.setOnAction(actionEvent -> handleSignUp());
        backToLogin_btn.setOnAction(actionEvent -> goBackToLogin());
    }

    private void handleSignUp() {
        String username = username_fld.getText().trim();
        String email = email_fld.getText().trim();
        String password = password_fld.getText().trim();

        // Έλεγχος εγκυρότητας των πεδίων
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            error_lbl.setText("Please fill in all fields.");
            error_lbl.setVisible(true);
            return;
        }

        boolean registrationSuccessful = dataBaseConnection.registerUser(username, email, password);
        if (registrationSuccessful) {
            try {
                // Κλείσε το τρέχον παράθυρο εγγραφής
                Stage currentStage = (Stage) createAccount_btn.getScene().getWindow();
                currentStage.close();

                // Φόρτωσε το client.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/client.fxml"));
                Parent clientRoot = loader.load();

                // Δημιούργησε νέο Stage για το client.fxml
                Stage newStage = new Stage();
                newStage.setScene(new Scene(clientRoot));
                newStage.show();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load client.fxml", e);
            }
        } else {
            // Εμφανίζει μήνυμα λάθους αν το όνομα χρήστη ή το email υπάρχουν ήδη
            error_lbl.setText("Registration failed. Username or email may already exist.");
            error_lbl.setVisible(true);
        }
    }

    private void goBackToLogin() {
        try {
            // Φόρτωσε το login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Login.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) backToLogin_btn.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load login.fxml", e);
        }
    }
}
