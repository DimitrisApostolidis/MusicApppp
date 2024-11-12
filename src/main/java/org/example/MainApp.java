package org.example;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.Models.Model;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        try {
            // Load the loading screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/LoadingWindow.fxml"));
            Parent loadingRoot = loader.load();

            // Create a scene with transparent background
            Scene loadingScene = new Scene(loadingRoot);
            loadingScene.setFill(Color.TRANSPARENT); // Transparent scene (The background)

            // Initialize the stage with no window decorations
            stage.initStyle(StageStyle.TRANSPARENT);  // Remove window borders and title bar (Removes the white background)

            // Set and show the scene
            stage.setScene(loadingScene);
            stage.show();


            // Fade-in transition: from invisible to visible
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), loadingRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            // After fade-in, wait x seconds, then fade-out the scene
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), loadingRoot);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);

                fadeOut.setOnFinished(e -> {
                    stage.close();

                });
                loadLogIn();
                fadeOut.play();

            }));

            delay.setCycleCount(1);  // Only run once

            // Start the fade-in and then the fade-out with a delay
            fadeIn.setOnFinished(event -> delay.play());
            fadeIn.play();  // Start fade-in

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLogIn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/LoginClient.fxml"));
            Parent loadingLogInRoot = loader.load();
            Scene loadingScene = new Scene(loadingLogInRoot, Color.TRANSPARENT);


            loadingScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
            loadingScene.setFill(Color.TRANSPARENT); // Transparent scene (The background)


            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.TRANSPARENT);  // Remove window borders and title bar (Removes the white background)
            loginStage.setScene(loadingScene);

            loginStage.setOpacity(0);
            loginStage.show();

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(loginStage.opacityProperty(), 0)),
                    new KeyFrame(Duration.seconds(0.3), new KeyValue(loginStage.opacityProperty(), 1))
            );
            timeline.setCycleCount(1); // Play the animation once
            timeline.play(); // Start the timeline


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
