package org.example.Controllers.Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.Controllers.LoginController;
import org.example.DataBase.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HistoryController {

    @FXML
    private VBox historyContainer;

    public void initialize() {
        if (historyContainer == null) {
            System.err.println("Το historyContainer είναι null!");
        } else {
            System.out.println("Το historyContainer φορτώθηκε επιτυχώς!");
            loadHistory();
        }
    }


    public void loadHistory() {
        String userId = LoginController.userId; // Πάρε το userId από το LoginController

        if (userId == null || userId.isEmpty()) {
            return; // Αν δεν υπάρχει έγκυρο userId, δεν φορτώνουμε τα δεδομένα
        }

        String query = "SELECT created_at, title, genre FROM history WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            Platform.runLater(() -> {
                if (historyContainer != null) {
                    historyContainer.getChildren().clear(); // Καθαρισμός παλιών εγγραφών
                }
            });

            while (resultSet.next()) {
                String createdAt = resultSet.getString("created_at");
                String title = resultSet.getString("title");
                String genre = resultSet.getString("genre");

                // Δημιουργία ετικέτας για κάθε εγγραφή
                Label entryLabel = new Label(
                        String.format("Date: %s | Title: %s | Genre: %s", createdAt, title, genre)
                );
                entryLabel.getStyleClass().add("history-entry"); // Στυλ από CSS

                Platform.runLater(() -> {
                    if (historyContainer != null) {
                        historyContainer.getChildren().add(entryLabel);
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}