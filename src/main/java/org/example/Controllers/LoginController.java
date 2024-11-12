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
import org.example.DataBase.DataBaseConnection; // Ensure this class exists
import org.example.Controllers.Client.DashboardController;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    // Cache for the scene to avoid reloading it multiple times
    private static Scene clientScene;

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

    // Handle login
    private void handleLogin() {
        String username = username_fld.getText();
        String password = password_fld.getText();
        DataBaseConnection db = new DataBaseConnection();

        // If admin login
        if ("admin".equals(username) && "admin".equals(password)) {
            openClientScene(username);
        } else {
            // Verify credentials
            if (db.verifyCredentials(username, password)) {
                openClientScene(username);
            } else {
                error_lbl.setText("Invalid username or password");
                error_lbl.setVisible(true);
            }
        }
    }

    // Open the client scene, either cached or newly loaded
    private void openClientScene(String username) {
        try {
            // If the scene is not already cached, load it
            if (clientScene == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
                Parent clientRoot = loader.load();

                // Set the username for the DashboardController
                DashboardController dashboardController = loader.getController();
                if (dashboardController != null) {
                    dashboardController.setUsername(username); // Set username
                } else {
                    logger.log(Level.SEVERE, "Failed to load Client from FXML");
                }

                // Cache the scene
                clientScene = new Scene(clientRoot);
                clientScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
            }

            // Close the current window and open the client window
            Stage currentStage = (Stage) login_btn.getScene().getWindow();
            currentStage.close();
            username_fld.clear();
            password_fld.clear();
            Stage newStage = new Stage();
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.setScene(clientScene); // Use the cached scene
            newStage.show();

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
}