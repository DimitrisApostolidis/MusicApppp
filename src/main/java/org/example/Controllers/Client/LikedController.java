package org.example.Controllers.Client;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.DataBase.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikedController {

    @FXML
    private ListView<String> likedSongsList;

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        loadLikedSongs();
    }

    @FXML
    public void initialize() {
        likedSongsList.setStyle("-fx-font-size: 14px;");
    }

    private void loadLikedSongs() {
        String query = "SELECT title, artist FROM favourite_songs WHERE user_id = ?";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<String> songs = FXCollections.observableArrayList();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                songs.add(title + " by " + artist);
            }
            likedSongsList.setItems(songs);

        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την ανάκτηση των αγαπημένων τραγουδιών: " + e.getMessage());
        }
    }
}
