package org.example.Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

import javafx.util.Duration;
import org.example.Controllers.Client.ClientController;
import org.example.Controllers.Client.ClientMenuController;
import org.example.DataBase.DataBaseConnection; // Ensure this class exists
import org.example.Controllers.Client.DashboardController;
import org.example.PlaylistController;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LoginController implements Initializable {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    // Cache for the scene to avoid reloading it multiple times
    private static Scene clientScene;
    PlaylistController playlistController = new PlaylistController();
    @FXML
    private Button createAccount_btn;
    @FXML
    private Label error_lbl;
    @FXML
    private TextField username_fld;
    @FXML
    private TextField password_fld;
    @FXML
    private Button login_btn;

    public Label username_lbl;

    @FXML
    private CheckBox remember_me_chk;  // Το CheckBox για το "Remember me"


    private Preferences prefs = Preferences.userNodeForPackage(LoginController.class); // Preferences για την εφαρμογή
    private List playlistView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        preloadClientScene();
        login_btn.setOnAction(actionEvent -> handleLogin());
        createAccount_btn.setOnAction(actionEvent -> handleCreateAccount());

        // Έλεγχος αν υπάρχουν αποθηκευμένα στοιχεία και αυτόματη συμπλήρωση
        String savedUsername = prefs.get("username", "");
        String savedPassword = prefs.get("password", "");

        if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
            username_fld.setText(savedUsername);
            password_fld.setText(savedPassword);
            remember_me_chk.setSelected(true);  // Αν είναι αποθηκευμένο, επιλέγεται το checkbox
        }
    }

    private void preloadClientScene() {
        try {
            if (clientScene == null) {
                // Load the scene only once
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
                Parent clientRoot = loader.load();

                // Cache the scene
                clientScene = new Scene(clientRoot);
                clientScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to preload Client scene", e);
        }
    }




    private void handleLogin() {
        String username = username_fld.getText();
        String password = password_fld.getText();
        DataBaseConnection db = new DataBaseConnection();

        // playlistController.clearLoggedInUserId();
        // Remember me επιλογή
        if (remember_me_chk.isSelected()) {
            prefs.put("username", username);  // Αποθήκευση του username
            prefs.put("password", password);  // Αποθήκευση του password
        } else {
            prefs.remove("username");
            prefs.remove("password");
            username_fld.clear();
            password_fld.clear();
        }

        // Αν ο χρήστης είναι admin
        if ("admin".equals(username) && "admin".equals(password)) {
            // Καθαρισμός του προηγούμενου userId
            String userId = "admin";
            playlistController.saveLoggedInUserId(userId);
            openClientScene(username);

        } else {
            // Έλεγχος διαπιστευτηρίων χρήστη
            if (db.verifyCredentials(username, password)) {
                String userId = db.getUserId(username); // Λήψη του νέου userId
                playlistController.saveLoggedInUserId(userId);
                openClientScene(username);

            } else {
                error_lbl.setText("Invalid username or password");
                error_lbl.setVisible(true);
            }
        }
    }



    private void openClientScene(String username) {
        try {
            // Φόρτωση του Client.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
            Parent clientRoot = loader.load();

            // Λήψη του controller και ρύθμιση των δεδομένων
            ClientController clientController = loader.getController();
            if (clientController != null) {
                clientController.setUsername(username);
                clientController.setWelcomeText(username);

            }

            // Δημιουργία σκηνής και εφαρμογή στυλ
            Scene clientScene = new Scene(clientRoot);
            clientScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());

            // Κλείσιμο του τρέχοντος παραθύρου
            Stage currentStage = (Stage) username_fld.getScene().getWindow();
            currentStage.close();

            // Δημιουργία νέου παραθύρου και εφαρμογή εφέ
            Stage newStage = new Stage();
            newStage.initStyle(StageStyle.UNDECORATED); // Αφαίρεση default title bar
            newStage.setScene(clientScene);

            // Προσθήκη εφέ fade-in
            newStage.setOpacity(0);
            newStage.show();

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(newStage.opacityProperty(), 0)),
                    new KeyFrame(Duration.seconds(0.3), new KeyValue(newStage.opacityProperty(), 1))
            );
            timeline.play(); // Έναρξη του animation




        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load Client scene", e);
        }
    }

    // Handle creating a new account
    private void handleCreateAccount() {
        try {
            // Load the sign-up scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/SignUpClient.fxml"));
            Parent clientRoot = loader.load();
            SignUpController signUpController = loader.getController();

            // Close the current window and open the sign-up window
            Stage currentStage = (Stage) login_btn.getScene().getWindow();
            Scene newScene = new Scene(clientRoot);
            newScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
            currentStage.setScene(newScene);
            currentStage.show();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load SignUp scene", e);
        }
    }
    public String getUsernameFromDatabase(String userId) {
        DataBaseConnection db = new DataBaseConnection();
        String query = "SELECT username FROM user WHERE user_id = ?";

        try {
            return db.executeQuery(query, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return "Guest";
        }

    }



}