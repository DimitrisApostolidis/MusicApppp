package org.example;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;



public class MusicManagerApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Music Manager");

        // Layout
        VBox layout = new VBox(10);
        TextField nameInput = new TextField();
        TextField bioInput = new TextField();
        TextField imageUrlInput = new TextField();
        Button addButton = new Button("Add Artist");

        // Add artist button action
        addButton.setOnAction(e -> {
            // Code to add artist to the database
        });

        layout.getChildren().addAll(nameInput, bioInput, imageUrlInput, addButton);
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

