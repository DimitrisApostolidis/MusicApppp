package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.example.DataBase.DataBaseConnection;// Βεβαιωθείτε ότι αυτή η κλάση υπάρχει
import org.example.Controllers.Client.DashboardController;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(actionEvent -> handleLogin());
        createAccount_btn.setOnAction(actionEvent -> handleCreateAccount());
    }



    // Στο LoginController
    private void handleLogin() {
        String username = username_fld.getText();
        String password = password_fld.getText();
        DataBaseConnection db = new DataBaseConnection();
        if ("admin".equals(username) && "admin".equals(password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
                Parent clientRoot = loader.load();

                DashboardController dashboardController = loader.getController();
                if (dashboardController != null) {
                    dashboardController.setUsername(username); // Θέτουμε το όνομα του χρήστη
                } else {
                    logger.log(Level.SEVERE, "Failed to load Client from FXML");
                }

                // Κλείσιμο του τρέχοντος παραθύρου και δημιουργία νέας σκηνής
                Stage currentStage = (Stage) login_btn.getScene().getWindow();
                Scene newScene = new Scene(clientRoot);
                newScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
                currentStage.setScene(newScene);
                currentStage.centerOnScreen();
                currentStage.show();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load FXML", e);
            }

        } else {
            if (db.verifyCredentials(username, password)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
                    Parent clientRoot = loader.load();

                    DashboardController dashboardController = loader.getController();
                    if (dashboardController != null) {
                        dashboardController.setUsername(username); // Θέτουμε το όνομα του χρήστη
                    } else {
                        logger.log(Level.SEVERE, "Failed to load Client from FXML");
                    }

                    // Κλείσιμο του τρέχοντος παραθύρου και δημιουργία νέας σκηνής
                    Stage currentStage = (Stage) login_btn.getScene().getWindow();
                    currentStage.close();

                    Stage newStage = new Stage();
                    Scene newScene = new Scene(clientRoot);
                    newScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
                    newStage.initStyle(StageStyle.UNDECORATED);
                    newStage.setScene(newScene);
                    newStage.show();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to load FXML", e);
                }
            } else {
                error_lbl.setText("Invalid username or password");
                error_lbl.setVisible(true);
            }
        }
    }

    private void handleCreateAccount() {
        try {
            // Φόρτωσε το signup.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/SignUpClient.fxml"));
            Parent clientRoot = loader.load();
            SignUpController SignUpController = loader.getController();

            // Κλείσιμο του τρέχοντος παραθύρου και δημιουργία νέας σκηνής
            Stage currentStage = (Stage) login_btn.getScene().getWindow();

            Scene newScene = new Scene(clientRoot);
            newScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load FXML", e);
        }
    }
    }
