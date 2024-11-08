package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import org.example.DataBase.DataBaseConnection;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class PlaylistController {
    private List<Playlist> playlists;
    private DataBaseConnection dbConnection;

    @FXML
    private TextField newPlaylistName;

    @FXML
    private ListView<String> playlistView;

    @FXML
    private ListView<String> albumListView;

    public PlaylistController() {
        playlists = new ArrayList<>();
        dbConnection = new DataBaseConnection();
        playlists = dbConnection.getPlaylists(); // Παίρνει όλες τις playlists από τη βάση δεδομένων
    }

    @FXML
    public void initialize() {
        loadPlaylists();
        // Ορίζουμε τι θα γίνεται όταν κάνουμε κλικ πάνω σε μία playlist
        playlistView.setOnMouseClicked(this::showPlaylistSongs);
    }

    @FXML
    private void handleAddPlaylist(ActionEvent event) {
        // Παίρνουμε το όνομα της νέας playlist
        String playlistName = newPlaylistName.getText();

        // Έλεγχος ότι το πεδίο δεν είναι κενό
        if (!playlistName.trim().isEmpty()) {
            // Δημιουργούμε το αντικείμενο Playlist
            Playlist newPlaylist = new Playlist(playlistName);

            // Προσθέτουμε τη νέα playlist στη βάση δεδομένων
            if (dbConnection.addPlaylist(playlistName)) {
                // Προσθήκη στη λίστα του ListView μόνο εάν επιτυχής η αποθήκευση
                playlistView.getItems().add(playlistName);
                playlists.add(newPlaylist); // Προσθήκη στη λίστα του controller για διαχείριση
                newPlaylistName.clear();
                System.out.println("Η νέα playlist προστέθηκε στη βάση δεδομένων και στη λίστα.");
            } else {
                System.out.println("Σφάλμα κατά την προσθήκη της playlist στη βάση δεδομένων.");
            }
        }
    }

    private void loadPlaylists() {
        playlistView.getItems().clear();
        List<String> playlistNames = dbConnection.getPlaylistNames();
        playlistView.getItems().addAll(playlistNames);
    }

    @FXML
    private void showPlaylistSongs(MouseEvent event) {
        if (event.getClickCount() == 1) {
            String selectedPlaylistName = playlistView.getSelectionModel().getSelectedItem();
            if (selectedPlaylistName != null) {
                int playlistId = dbConnection.getPlaylistIdByName(selectedPlaylistName);
                List<String> songs = dbConnection.getPlaylistSongs(playlistId);
                albumListView.getItems().setAll(songs);
            }
        }
    }

    public void addPlaylist(String name) {
        playlists.add(new Playlist(name));
    }

    public void addSongToPlaylist(String playlistName, Song song) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(playlistName)) {
                playlist.addSong(song);
                break;
            }
        }
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }
}