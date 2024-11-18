package org.example.Controllers.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class ClientController {

    @FXML
    private Text welcomeText; // Label για το καλωσόρισμα

    // Μέθοδος για να ενημερώνουμε το welcomeText με το username
    public void setWelcomeText(String username) {
        welcomeText.setText("Welcome back, " + username + "!");
    }

    public void setUsername(String username) {
        welcomeText.setText("Welcome back, " + username + "!");
        System.out.printf("Username is: %s", username);
    }
}
