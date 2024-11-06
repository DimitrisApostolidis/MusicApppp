package org.example.Controllers.Client;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    private static final Logger logger = Logger.getLogger(ClientMenuController.class.getName());
    public Button dashboard_btn;
    public Button playlist_btn;
    public Button likedsong_btn;
    public Button history_btn;
    public Button profile_btn;
    @FXML
    public Button logout_btn;
    public Button report_btn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logout_btn.setOnAction(actionEvent -> handleLogout());
    }
    private void handleLogout() {
        try {
            // Φόρτωσε το login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/login.fxml"));
            Parent loginRoot = loader.load();

            // Πάρε το τρέχον Stage και άλλαξε τη σκηνή
            Stage stage = (Stage) logout_btn.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load login.fxml", e);
        }
    }
}
