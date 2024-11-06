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
import org.example.DataBase.DataBaseConnection; // Βεβαιωθείτε ότι αυτή η κλάση υπάρχει

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


    private void handleLogin() {
        String username = username_fld.getText();
        String password = password_fld.getText();

        if ("admin".equals(username) && "admin".equals(password)) {
            try {
                // Φόρτωσε το clientmenu.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
                Parent clientRoot = loader.load();

                // Πάρε το τρέχον Stage και κλείσε το
                Stage currentStage = (Stage) login_btn.getScene().getWindow();
                currentStage.close();

                // Δημιούργησε ένα νέο Stage για το client menu
                Stage newStage = new Stage();
                Scene newScene = new Scene(clientRoot);
                newStage.initStyle(StageStyle.UNDECORATED);
                // Link the CSS file to the new scene
                newScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
                // Set the scene to the new stage
                newStage.setScene(newScene);
                newStage.show();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load 1clientmenu.fxml", e);
            }
            return;
        }

        DataBaseConnection db = new DataBaseConnection();
        if (db.verifyCredentials(username, password)) {
            try {
                // Φόρτωσε το clientmenu.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
                Parent clientRoot = loader.load();

                // Πάρε το τρέχον Stage και κλείσε το
                Stage currentStage = (Stage) login_btn.getScene().getWindow();
                currentStage.close();

                // Δημιούργησε ένα νέο Stage για το client menu
                Stage newStage = new Stage();
                newStage.setScene(new Scene(clientRoot));
                newStage.show();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load clientmenu.fxml", e);
            }
        } else {
            error_lbl.setText("Invalid username or password");
            error_lbl.setVisible(true);
        }
    }
    private void handleCreateAccount() {
        try {
            // Φόρτωσε το signup.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/SignUp.fxml"));
            Parent signupRoot = loader.load();

            // Πάρε την τρέχουσα σκηνή (παράθυρο) και ορίσε μια νέα σκηνή με το signupRoot
            Stage stage = (Stage) createAccount_btn.getScene().getWindow();
            stage.setScene(new Scene(signupRoot));
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load signup.fxml", e); // Log the exception
    }
    }
}