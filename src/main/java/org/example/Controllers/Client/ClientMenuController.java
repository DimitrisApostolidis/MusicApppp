package org.example.Controllers.Client;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.example.Controllers.LoginController;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    // Map to cache loaded scenes
    private static final Map<String, Scene> sceneCache = new HashMap<>();

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
        playlist_btn.setOnAction(event -> openPlaylistScene());
        dashboard_btn.setOnAction(event -> openDashboardScene());
        history_btn.setOnAction(event -> openHistoryScene());
    }

    private void openDashboardScene() {
        changeScene("/Fxml/Client/Client.fxml");
    }

    private void openPlaylistScene() {
        changeScene("/Fxml/Client/PlaylistClient.fxml");
    }

    private void openHistoryScene() {
        changeScene("/Fxml/Client/HistoryClient.fxml");
    }

    private void handleLogout() {
        changeScene("/Fxml/Client/LoginClient.fxml");
    }

    private void changeScene(String fxmlPath) {
        // Check if the scene is already cached
        if (sceneCache.containsKey(fxmlPath)) {
            // If cached, use the cached scene
            Stage currentStage = (Stage) dashboard_btn.getScene().getWindow(); // Get the current stage from any button
            currentStage.setScene(sceneCache.get(fxmlPath));
            currentStage.centerOnScreen();
            currentStage.show();
        } else {
            // If not cached, load the scene and cache it
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Scene newScene = new Scene(root);
                newScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());

                // Cache the scene
                sceneCache.put(fxmlPath, newScene);

                // Set the new scene and show it
                Stage currentStage = (Stage) dashboard_btn.getScene().getWindow(); // Get the current stage from any button
                currentStage.setScene(newScene);
                currentStage.centerOnScreen();
                currentStage.show();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load scene: " + fxmlPath, e);
            }
        }
    }
}