package org.example;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import org.example.Controllers.LoginController;
import org.example.DataBase.DataBaseConnection;
import org.example.Controllers.Client.LastFmApiClient;
import javafx.scene.input.MouseEvent;
import kong.unirest.JsonNode;

import java.sql.*;


import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static org.example.DataBase.DataBaseConnection.getConnection;

public class PlaylistController {
    private LastFmApiClient lastFmApiClient;
    List<Playlist> playlists;
    DataBaseConnection dbConnection;
    private String userId;


    @FXML
    TextField newPlaylistName;

    @FXML
    ListView<String> playlistView;

    @FXML
    private ListView<String> albumListView;

    @FXML
    private ListView<String> availableSongsListView;

    @FXML
    private TextField searchTrackField;

    private ObservableList<String> playlistData;


    public PlaylistController() {

        playlists = new ArrayList<>();
        dbConnection = new DataBaseConnection();
        lastFmApiClient = new LastFmApiClient(); // Αρχικοποίηση του API client
        playlists = dbConnection.getPlaylists(userId);

    }

    @FXML
    public void initialize() {
        getLoggedInUserId();

        loadPlaylists();
        playlistView.setOnMouseClicked(this::showPlaylistSongs);// Φορτώνει τις playlists
    }


    @FXML
    public void handleAddPlaylist(ActionEvent event) {
        // Παίρνουμε το όνομα της νέας playlist
        String playlistName = newPlaylistName.getText();

        // Έλεγχος ότι το πεδίο δεν είναι κενό
        if (!playlistName.trim().isEmpty()) {
            String userId = getLoggedInUserId();

            if (userId == null || userId.trim().isEmpty()) {
                System.out.println("Σφάλμα: Δεν είναι συνδεδεμένος χρήστης.");
                return;  // Δεν προχωράμε στην προσθήκη αν δεν υπάρχει userId
            }

            // Δημιουργούμε το αντικείμενο Playlist
            Playlist newPlaylist = new Playlist();

            // Προσθήκη στη βάση δεδομένων μόνο αν είναι επιτυχής
            boolean isAdded = false;
            try {
                isAdded = dbConnection.addPlaylist(playlistName, userId);  // Προσπαθούμε να προσθέσουμε την playlist
            } catch (Exception e) {
                // Αν προκύψει κάποιο σφάλμα, εμφανίζουμε μήνυμα και ορίζουμε ότι η προσθήκη απέτυχε
                System.out.println("Σφάλμα κατά την προσθήκη στη βάση δεδομένων: " + e.getMessage());
            }

            if (isAdded) {
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

    public void loadPlaylists() {
        String userId = getLoggedInUserId();
        playlistView.getItems().clear();  // Εκκαθάριση των στοιχείων στη λίστα

        if (userId != null && !userId.trim().isEmpty()) {
            List<String> playlistNamesFromDb = dbConnection.getPlaylistNames(userId);
            if (playlistNamesFromDb != null && !playlistNamesFromDb.isEmpty()) {
                playlistView.getItems().addAll(playlistNamesFromDb);
            }
            playlistView.refresh();
        } else {
            System.out.println("Δεν είναι συνδεδεμένος χρήστης.");
        }
    }


    @FXML
    public void showPlaylistSongs(MouseEvent event) {
        if (event != null && event.getClickCount() == 1) {
            String selectedPlaylistName = playlistView.getSelectionModel().getSelectedItem();
            if (selectedPlaylistName != null) {
                // Παίρνουμε το playlistId από τη βάση δεδομένων
                int playlistId = dbConnection.getPlaylistIdByName(selectedPlaylistName);
                if (playlistId != -1) {
                    // Ανάκτηση των τραγουδιών για αυτή την playlist μέσω του πίνακα playlist_song
                    List<String> songs = getSongsFromPlaylist(playlistId);  // Μέθοδος που καλεί τη βάση
                    albumListView.getItems().setAll(songs);  // Ενημέρωση του ListView με τα τραγούδια
                }
            }
        }
    }






    List<String> getSongsFromPlaylist(int playlistId) {
        List<String> songs = new ArrayList<>();

        // 1. Ερώτημα για να πάρεις όλα τα song_id από τον πίνακα playlist_song με το συγκεκριμένο playlist_id
        String getSongIdsQuery = "SELECT song_id FROM playlist_song WHERE playlist_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(getSongIdsQuery)) {
            stmt.setInt(1, playlistId); // Ορίζουμε το playlist_id στο query
            ResultSet rs = stmt.executeQuery(); // Εκτελούμε το query για να πάρουμε τα song_id

            // 2. Για κάθε song_id, παίρνουμε το αντίστοιχο όνομα από τον πίνακα history
            while (rs.next()) {
                int songId = rs.getInt("song_id"); // Παίρνουμε το song_id
                String songName = getSongNameById(songId); // Παίρνουμε το όνομα του τραγουδιού με βάση το song_id από τον πίνακα history
                if (songName != null) {
                    songs.add(songName); // Προσθέτουμε το τραγούδι στη λίστα
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Εάν υπάρχει σφάλμα, το εκτυπώνουμε
        }
        return songs; // Επιστρέφουμε τη λίστα με τα τραγούδια
    }

    String getSongNameById(int songId) {
        String songName = null;
        String query = "SELECT title FROM history WHERE id = ?"; // Ερώτημα στον πίνακα History αντί για song
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, songId); // Ορίζουμε το song_id στο query
            ResultSet rs = stmt.executeQuery(); // Εκτελούμε το query για να πάρουμε το όνομα του τραγουδιού

            if (rs.next()) {
                songName = rs.getString("title"); // Παίρνουμε το όνομα του τραγουδιού από το History
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Εάν υπάρχει σφάλμα, το εκτυπώνουμε
        }
        return songName; // Επιστρέφουμε το όνομα του τραγουδιού από το History
    }


    public void addPlaylist(String playlistName, String userId) throws SQLException {
        String sql = "INSERT INTO playlist (name, user_id) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/rapsodiaplayer", "root", "");
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Ρύθμιση των παραμέτρων της SQL
            pstmt.setString(1, playlistName);
            pstmt.setString(2, userId);

            // Εκτέλεση της προσθήκης
            int rowsAffected = pstmt.executeUpdate();

            // Εάν δεν προστέθηκαν γραμμές, ρίχνουμε εξαίρεση
            if (rowsAffected == 0) {
                throw new SQLException("Αποτυχία κατά την προσθήκη της playlist στη βάση δεδομένων.");
            }
        }
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
                                    String songName = parts[0].trim();
                                    String artistName = parts[1].trim();

                                    // Έλεγχος αν το τραγούδι υπάρχει ήδη στη βάση δεδομένων
                                    int songId = dbConnection.getSongIdByTitle(songName);

                                    if (songId == -1) {
                                        // Αν το τραγούδι δεν υπάρχει, το προσθέτουμε στη βάση
                                        songId = dbConnection.addSongToDatabase(songName, artistName);
                                        if (songId == -1) {
                                            System.out.println("Σφάλμα κατά την προσθήκη του τραγουδιού στη βάση δεδομένων.");
                                            return;
                                        }
                                    }

                                    // Παίρνει το playlistId από τη βάση δεδομένων
                                    int playlistId = dbConnection.getPlaylistIdByName(selectedPlaylist);

                                    // Προσθέτει το τραγούδι στην playlist με το υπάρχον songId
                                    if (dbConnection.addSongToPlaylist(playlistId, songId,songName)) {
                                        System.out.println("Το τραγούδι προστέθηκε στην playlist.");
                                        loadPlaylists(); // Ανανεώνει τις playlists
                                    } else {
                                        System.out.println("Σφάλμα κατά την προσθήκη του τραγουδιού στην playlist.");
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
                try (Connection conn = getConnection();
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

    public void clearLoggedInUserId() {
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.remove("loggedInUserId");
        System.out.println("Το userId διαγράφηκε.");

    }
    public void saveLoggedInUserId(String userId) {
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.put("loggedInUserId", userId);  // Αποθήκευση του νέου userId

    }
    public String getLoggedInUserId() {
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        return prefs.get("loggedInUserId", null);
    }


}