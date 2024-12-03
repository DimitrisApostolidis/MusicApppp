package org.example.Controllers.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.DataBase.DataBaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class HistoryController {

    @FXML
    private VBox historyContainer;

    public void initialize() {
        loadHistory();
    }

    private void loadHistory() {
        String query = "SELECT id, title, genre FROM history";
        try (Connection connection = DataBaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

