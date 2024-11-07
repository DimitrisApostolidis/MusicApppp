package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.DataBase.DataBaseConnection;

import java.sql.*;
import java.util.List;

public class PlaylistController {
    private static final String URL = "jdbc:mysql://localhost:3306/rapsodia_player";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML
    private ListView<String> playlistView;

    @FXML
    private TextField newPlaylistName;

    @FXML
    private ListView<String> playlistDetailsView;

    @FXML
    private Button addSongButton;

    private DataBaseConnection dbConnection;

    public PlaylistController() {
        dbConnection = new DataBaseConnection();
    }

    @FXML
    private void initialize() {
        loadPlaylists();
        playlistView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showPlaylistDetails(newValue);
            }
        });
    }

    private void loadPlaylists() {
        List<String> playlists = dbConnection.getPlaylists();
        playlistView.getItems().clear();

        for (String playlist : playlists) {
            int playlistId = getPlaylistId(playlist);
            List<String> songs = dbConnection.getPlaylistSongs(playlistId);
            String songsList = String.join(", ", songs);

            playlistView.getItems().add(playlist);
        }
    }

    private int getPlaylistId(String playlistName) {
        int playlist_id = 0;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT playlist_id FROM playlist WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, playlistName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        playlist_id = rs.getInt("playlist_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlist_id;
    }

    private void showPlaylistDetails(String playlistName) {
        int playlistId = getPlaylistId(playlistName);
        List<String> songs = dbConnection.getPlaylistSongs(playlistId);

        playlistDetailsView.getItems().clear();
        playlistDetailsView.getItems().addAll(songs);
    }

    @FXML
    private void handleAddPlaylist() {
        String name = newPlaylistName.getText().trim();

        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Playlist Name is Empty");
            alert.setContentText("Please enter a name for the new playlist.");
            alert.showAndWait();
        } else {
            boolean success = dbConnection.addPlaylist(name);
            if (success) {
                playlistView.getItems().add(name);
                newPlaylistName.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to Add Playlist");
                alert.setContentText("There was an error adding the playlist to the database.");
                alert.showAndWait();
            }
        }
    }
}
