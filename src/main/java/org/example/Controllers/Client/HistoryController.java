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

        historyContainer.getChildren().clear();
            loadHistory(); // Καλείς το loadHistory για να ανανεώσεις το ιστορικό


    }

    public void loadHistory() {
        // Παίρνουμε το userId από το LoginController
        String userId = LoginController.userId; // Πάρε το userId από το LoginController

        if (userId == null || userId.isEmpty()) {
            return; // Αν δεν υπάρχει έγκυρο userId, δεν φορτώνουμε τα δεδομένα
        }

        // Η query τώρα παίρνει το user_id και εμφανίζει το ιστορικό του χρήστη
        String query = "SELECT id, title, genre FROM history WHERE user_id = ?";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId); // Χρησιμοποιούμε το userId για το preparedStatement

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String genre = resultSet.getString("genre");

                // Δημιουργία Label για την καταχώρηση
                Label entryLabel = new Label("ID: " + id + " | Title: " + title + " | Genre: " + genre);
                entryLabel.getStyleClass().add("history-entry"); // Στυλ από CSS

                // Προσθήκη στο VBox
                historyContainer.getChildren().add(entryLabel);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}