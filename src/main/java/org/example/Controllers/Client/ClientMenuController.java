package org.example.Controllers.Client;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.StageStyle;
import org.example.Controllers.LoginController;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    public Button dashboard_btn;
    public Button playlist_btn;
    public Button likedsong_btn;
    public Button history_btn;
    public Button profile_btn;
    public Button logout_btn;
    public Button report_btn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logout_btn.setOnAction(event -> handleLogout());
    }

    

    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/LoginClient.fxml"));
            Parent clientRoot = loader.load();

            // Πάρε το τρέχον Stage και κλείσε το
            Stage currentStage = (Stage) logout_btn.getScene().getWindow();
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
            logger.log(Level.SEVERE, "Failed to load clientmenu.fxml", e);
        }
    }
}