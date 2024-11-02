package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.DataBase.DataBaseConnection;

public class SignUpController {
    @FXML
    private TextField username_fld;
    @FXML
    private TextField email_fld;
    @FXML
    private TextField password_fld;
    @FXML
    private Button createAccount_btn;
    @FXML
    private Label error_lbl;

    private DataBaseConnection dataBaseConnection;

    @FXML
    public void initialize() {
        dataBaseConnection = new DataBaseConnection();
        createAccount_btn.setOnAction(actionEvent -> handleSignUp());
    }

    private void handleSignUp() {
        String username = username_fld.getText().trim();
        String email = email_fld.getText().trim();
        String password = password_fld.getText().trim();

        // Έλεγχος εγκυρότητας των πεδίων
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            error_lbl.setText("Please fill in all fields.");
            return;
        }

        boolean registrationSuccessful = dataBaseConnection.registerUser(username, email, password);
        if (registrationSuccessful) {
            error_lbl.setText("Registration successful!"); // Επιτυχής εγγραφή
            // Μπορείτε να μεταβείτε σε άλλη οθόνη εδώ αν χρειάζεται
        } else {
            error_lbl.setText("Registration failed. Username may already exist."); // Αποτυχία εγγραφής
        }
    }
}