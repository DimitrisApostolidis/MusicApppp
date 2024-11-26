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
import org.example.PlaylistController;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    private static final Map<String, Scene> sceneCache = new HashMap<>();

    public Button dashboard_btn;
    public Button playlist_btn;
    public Button likedsong_btn;
    public Button history_btn;
    public Button profile_btn;
    public Button logout_btn;
    public Button report_btn;
    private PlaylistController playlistController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logout_btn.setOnAction(event -> handleLogout());
        playlist_btn.setOnAction(event -> openPlaylistScene()); // Άνοιξε τη σκηνή των playlists});
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
        PlaylistController playlistController = new PlaylistController();
        playlistController.clearLoggedInUserId();
        changeScene("/Fxml/Client/LoginClient.fxml");

    }






    private void changeScene(String fxmlPath) {
        if (sceneCache.containsKey(fxmlPath)) {
            Stage currentStage = (Stage) dashboard_btn.getScene().getWindow(); // Get the current stage from any button
            currentStage.setScene(sceneCache.get(fxmlPath));
            currentStage.centerOnScreen();
            currentStage.show();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Scene newScene = new Scene(root);
                newScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
                sceneCache.put(fxmlPath, newScene);
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