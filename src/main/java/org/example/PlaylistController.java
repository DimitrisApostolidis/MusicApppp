package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import org.example.DataBase.DataBaseConnection;
import org.example.Controllers.Client.LastFmApiClient;
import javafx.scene.input.MouseEvent;
import kong.unirest.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import java.util.ArrayList;
import java.util.List;

public class PlaylistController {
    private LastFmApiClient lastFmApiClient;
    private List<Playlist> playlists;
    private DataBaseConnection dbConnection;

    @FXML
    private TextField newPlaylistName;

    @FXML
    private ListView<String> playlistView;

    @FXML
    private ListView<String> albumListView;

    @FXML
    private ListView<String> availableSongsListView;

    @FXML
    private TextField searchTrackField;

    public PlaylistController() {
        playlists = new ArrayList<>();
        dbConnection = new DataBaseConnection();
        lastFmApiClient = new LastFmApiClient(); // Αρχικοποίηση του API client
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

    @FXML
    private void handleDeletePlaylist() {
        // Παίρνουμε την επιλεγμένη playlist από το ListView
        String selectedPlaylist = playlistView.getSelectionModel().getSelectedItem();

        // Ελέγχουμε αν υπάρχει επιλεγμένη playlist
        if (selectedPlaylist != null) {
            // Διαγραφή από τη βάση δεδομένων
            if (dbConnection.deletePlaylistByName(selectedPlaylist)) {
                // Αφαίρεση από το ListView
                playlistView.getItems().remove(selectedPlaylist);
                System.out.println("Η playlist διαγράφηκε επιτυχώς.");
            } else {
                System.out.println("Σφάλμα κατά τη διαγραφή της playlist από τη βάση δεδομένων.");
            }
        } else {
            System.out.println("Δεν έχει επιλεχθεί κάποια playlist για διαγραφή.");
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
    @FXML
    private void handleAddSongToPlaylist() {
        String selectedPlaylist = playlistView.getSelectionModel().getSelectedItem();

        if (selectedPlaylist != null) {
            String searchQuery = newPlaylistName.getText().trim();
            if (searchQuery.isEmpty()) {
                System.out.println("Πληκτρολογήστε όνομα τραγουδιού για αναζήτηση.");
                return;
            }

            // Κλήση στο Last.fm API για αναζήτηση τραγουδιών
            JsonNode response = lastFmApiClient.searchTracks(searchQuery);
            if (response != null) {
                // Αντί να χρησιμοποιήσουμε org.json.JSONObject, χρησιμοποιούμε το JsonNode της Unirest
                kong.unirest.json.JSONObject results = response.getObject();
                if (results != null) {
                    kong.unirest.json.JSONArray tracks = results.optJSONObject("results")
                            .optJSONObject("trackmatches")
                            .optJSONArray("track");
                    if (tracks != null) {
                        List<String> trackNames = new ArrayList<>();
                        for (int i = 0; i < tracks.length(); i++) {
                            kong.unirest.json.JSONObject track = tracks.getJSONObject(i);
                            String trackName = track.getString("name");
                            String artistName = track.getString("artist");
                            trackNames.add(trackName + " - " + artistName);
                        }
                        availableSongsListView.getItems().setAll(trackNames);

                        // Διαχείριση διπλού κλικ για προσθήκη τραγουδιού στην playlist
                        availableSongsListView.setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2) {
                                String selectedSong = availableSongsListView.getSelectionModel().getSelectedItem();
                                if (selectedSong != null) {
                                    // Διάσπαση σε τίτλο και καλλιτέχνη
                                    String[] parts = selectedSong.split(" - ");
                                    String songName = parts[0];
                                    String artistName = parts[1];

                                    // Αποθήκευση στη βάση
                                    int songId = dbConnection.addSongToDatabase(songName, artistName);
                                    if (songId != -1) {
                                        int playlistId = dbConnection.getPlaylistIdByName(selectedPlaylist);
                                        if (dbConnection.addSongToPlaylist(playlistId, songId)) {
                                            System.out.println("Το τραγούδι προστέθηκε στην playlist.");
                                            loadPlaylists();
                                        } else {
                                            System.out.println("Σφάλμα κατά την προσθήκη του τραγουδιού στην playlist.");
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            } else {
                System.out.println("Σφάλμα κατά την αναζήτηση τραγουδιών από το Last.fm.");
            }
        } else {
            System.out.println("Δεν έχει επιλεχθεί κάποια playlist.");
        }
    }

    @FXML
    private void handleDeleteSongFromPlaylist() {
        // Παίρνουμε το επιλεγμένο τραγούδι
        String selectedSong = albumListView.getSelectionModel().getSelectedItem();

        // Έλεγχος αν υπάρχει επιλεγμένο τραγούδι
        if (selectedSong != null) {
            // Παίρνουμε την επιλεγμένη playlist
            String selectedPlaylist = playlistView.getSelectionModel().getSelectedItem();
            if (selectedPlaylist != null) {
                // Παίρνουμε το playlistId από τη βάση δεδομένων
                int playlistId = dbConnection.getPlaylistIdByName(selectedPlaylist);

                // Διαχωρισμός του τραγουδιού σε τίτλο και καλλιτέχνη
                String songTitle = "";
                String songArtist = "";

                try {
                    String[] parts = selectedSong.split(" - "); // Διαχωρισμός στο " - "
                    songTitle = parts[0].replace("Song: ", "").trim(); // Αφαίρεση "Song: "
                    songArtist = parts[1].replace("Artist: ", "").trim(); // Αφαίρεση "Artist: "
                } catch (Exception e) {
                    System.out.println("Σφάλμα στη μορφή του τραγουδιού: " + selectedSong);
                    return;
                }

                // Παίρνουμε το songId από τη βάση δεδομένων χρησιμοποιώντας τίτλο και καλλιτέχνη
                int songId = dbConnection.getSongIdByTitleAndArtist(songTitle, songArtist);

                if (songId == -1) {
                    System.out.println("Το τραγούδι '" + songTitle + "' του καλλιτέχνη '" + songArtist + "' δεν βρέθηκε στη βάση δεδομένων.");
                    return;
                }

                // Διαγραφή τραγουδιού από την playlist
                String deleteQuery = "DELETE FROM playlist_song WHERE playlist_id = ? AND song_id = ?";
                try (Connection conn = DataBaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                    System.out.println("Playlist ID: " + playlistId);
                    System.out.println("Song ID: " + songId);

                    stmt.setInt(1, playlistId);
                    stmt.setInt(2, songId);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Το τραγούδι διαγράφηκε επιτυχώς από την playlist.");
                        albumListView.getItems().remove(selectedSong);
                    } else {
                        System.out.println("Δεν βρέθηκε αντιστοιχία για διαγραφή.");
                    }
                } catch (SQLException e) {
                    System.out.println("Σφάλμα κατά τη διαγραφή του τραγουδιού.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Δεν έχει επιλεχθεί κάποια playlist.");
            }
        } else {
            System.out.println("Δεν έχει επιλεχθεί κάποιο τραγούδι για διαγραφή.");
        }
    }




    public List<Playlist> getPlaylists() {
        return playlists;
    }
}