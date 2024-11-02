package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    @FXML
    public void initialize() {
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

        // Εδώ μπορείς να προσθέσεις λογική για την εγγραφή του χρήστη
        // Π.χ., επικοινωνία με τη βάση δεδομένων για αποθήκευση χρηστών

        // Ακολουθία αποτυχίας
        // if (registrationFailed) {
        //     error_lbl.setText("Registration failed. Please try again.");
        // } else {
        //     // Επιτυχής εγγραφή, μπορείς να μεταβείς στην οθόνη σύνδεσης ή στο κύριο παράθυρο
        // }
    }
}
