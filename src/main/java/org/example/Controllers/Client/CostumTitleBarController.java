package org.example.Controllers.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.Models.Model;

public class CostumTitleBarController {

    @FXML
    private AnchorPane customTitleBar;

    private double xOffset = 0;
    private double yOffset = 0;
    private int flag = 0;

    @FXML
    private Button closeButton; // Link to the close button in FXML

    @FXML
    private Button minimizeButton; // Link to the minimize button in FXML

    @FXML
    private Button enlargeButton; // Link to the enlarge button in FXML

    @FXML
    public void initialize() {
            customTitleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        customTitleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) customTitleBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
            System.out.println(xOffset);
            System.out.println(yOffset);
        });

    }

    @FXML
    private void handleCloseButtonAction() {
        // Close the application
        Stage stage = (Stage) closeButton.getScene().getWindow(); // Get the current stage
        stage.close();
    }

    @FXML
    private void handleMinimizeButtonAction() {
        // Minimize the application
        Stage stage = (Stage) minimizeButton.getScene().getWindow(); // Get the current stage
        stage.setIconified(true);
    }

    @FXML
    private void handleEnlargeButtonAction() {
        Stage stage = (Stage) enlargeButton.getScene().getWindow();
        if (flag == 1) {            //if maximized
            stage.setWidth(1083);
            stage.setHeight(826);
            flag =0;
        } else {                    //set it full screen
            stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
            flag =1;
        }
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - stage.getWidth()) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - stage.getHeight()) / 2);
    }
}

