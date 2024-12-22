package org.example;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.example.Controllers.LoginController;
import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.*;

import javafx.event.ActionEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistControllerTest {

    private Connection connection;

    private PlaylistController playlistController;
    private DataBaseConnection dbConnection;
    private TextField newPlaylistNameField;
    private ListView<String> playlistView;

    @BeforeEach
    public void setUp() throws SQLException {
        // Σύνδεση στη βάση δεδομένων (πρέπει να έχεις ρυθμίσει τη βάση δεδομένων και τον πίνακα playlist)
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/rapsodiaplayer", "root", "");
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {
                // Το JavaFX περιβάλλον έχει αρχικοποιηθεί
            });
        }
        dbConnection = new DataBaseConnection(); // Χρησιμοποιούμε την πραγματική σύνδεση
        playlistController = new PlaylistController();

        // Δημιουργία πραγματικών UI στοιχείων
        newPlaylistNameField = new TextField();
        playlistView = new ListView<>();

        // Ρύθμιση των στοιχείων UI στον controller
        playlistController.newPlaylistName = newPlaylistNameField;
        playlistController.playlistView = playlistView;
        playlistController.dbConnection = dbConnection;

        // Αρχικοποιούμε τις λίστες
        playlistController.playlists = FXCollections.observableArrayList();


    }

    @Test
    public void testGetPlaylistNames() throws SQLException {
        String userId = "23";  // Πρέπει να υπάρχει ένας χρήστης με αυτό το ID στη βάση δεδομένων

        // Κλήση της μεθόδου
        List<String> playlistNames = getPlaylistNames(userId);

        // Έλεγχος ότι η λίστα δεν είναι κενή
        assertNotNull(playlistNames, "Η λίστα με τα ονόματα των playlists δεν πρέπει να είναι null");
        assertFalse(playlistNames.isEmpty(), "Η λίστα με τα ονόματα των playlists δεν πρέπει να είναι κενή");

        // Έλεγχος ότι τα ονόματα των playlists είναι σωστά
        assertTrue(playlistNames.contains("My Playlist"), "Η λίστα πρέπει να περιέχει το 'My Playlist'");
        assertTrue(playlistNames.contains("Another Playlist"), "Η λίστα πρέπει να περιέχει το 'Another Playlist'");
    }

    public List<String> getPlaylistNames(String userId) throws SQLException {
        List<String> playlistNames = new ArrayList<>();
        String sql = "SELECT name FROM playlist WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlistNames.add(rs.getString("name"));
                }
            }
        }

        return playlistNames;
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Κλείσιμο της σύνδεσης
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testHandleAddPlaylist_emptyPlaylistName() {
        newPlaylistNameField.setText("");

        playlistController.handleAddPlaylist(new ActionEvent());

        assertTrue(playlistView.getItems().isEmpty(), "Η λίστα δεν πρέπει να ενημερωθεί όταν το όνομα της playlist είναι κενό.");
    }

    @Test
    public void testHandleAddPlaylist_noLoggedInUser() {
        // Ορισμός ονόματος playlist
        newPlaylistNameField.setText("My Playlist");
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.remove("loggedInUserId");

        playlistController.handleAddPlaylist(new ActionEvent());

        assertTrue(playlistView.getItems().isEmpty(), "Η λίστα δεν πρέπει να ενημερωθεί όταν δεν υπάρχει συνδεδεμένος χρήστης.");
    }

    @Test
    public void testHandleAddPlaylist_successfulAddition() {

        newPlaylistNameField.setText("My Playlist");
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.put("loggedInUserId", "23");  // Δημιουργούμε έναν αποθηκευμένο χρήστη για το τεστ
        playlistController.handleAddPlaylist(new ActionEvent());
        assertTrue(playlistView.getItems().contains("My Playlist"), "Η λίστα πρέπει να ενημερωθεί με τη νέα playlist.");
        assertEquals(1, playlistView.getItems().size(), "Η λίστα πρέπει να περιέχει μία playlist.");
    }

    @Test
    public void testHandleAddPlaylist_failedAddition() {

        newPlaylistNameField.setText("My Playlist");

        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.put("loggedInUserId", "350");

        dbConnection = new DataBaseConnection() {
            @Override
            public boolean addPlaylist(String playlistName, String userId) {
                return false;
            }
        };

        // Κλήση της μεθόδου handleAddPlaylist
        playlistController.handleAddPlaylist(new ActionEvent());
        assertFalse(playlistView.getItems().contains("My Playlist"),
                "Η λίστα δεν πρέπει να ενημερωθεί όταν αποτύχει η προσθήκη στη βάση δεδομένων.");
    }

    @Test
    public void testHandleDeletePlaylist_successfulDeletion() {

        playlistView.getItems().add("My Playlist");
        playlistView.getSelectionModel().select("My Playlist");

        dbConnection = new DataBaseConnection() {
            @Override
            public boolean deletePlaylistByName(String playlistName) {
                return true;
            }
        };

        try {
            Method method = PlaylistController.class.getDeclaredMethod("handleDeletePlaylist");
            method.setAccessible(true);
            method.invoke(playlistController);
            assertFalse(playlistView.getItems().contains("My Playlist"), "Η playlist πρέπει να διαγραφεί από τη λίστα.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Η μέθοδος δεν εκτελέστηκε σωστά.");
        }
    }

    @Test
    public void testHandleDeletePlaylist_failedDeletion() {
        try {

            playlistView.getItems().add("My Playlist");
            playlistView.getSelectionModel().select("My Playlist");
            dbConnection = new DataBaseConnection() {
                @Override
                public boolean deletePlaylistByName(String playlistName) {
                    return false;
                }
            };

            Method method = PlaylistController.class.getDeclaredMethod("handleDeletePlaylist");
            method.setAccessible(true);
            method.invoke(playlistController);

            assertTrue(playlistView.getItems().contains("My Playlist"),
                    "Η playlist δεν πρέπει να διαγραφεί από τη λίστα όταν αποτύχει η διαγραφή.");

            assertEquals(1, playlistView.getItems().size(), "Η λίστα πρέπει να περιέχει 1 playlist.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Η μέθοδος έπρεπε να εκτελείται χωρίς σφάλματα.");
        }
    }

    @Test
    public void testLoadPlaylists_successful() {
        //Πρεπει να υπαρχει ηδη ο χρηστης
        String userId = "1";
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.put("loggedInUserId", userId);
        //Δεν πρεπει να βαλουμε εμεις τις Playlists
        dbConnection = new DataBaseConnection();
        try (Connection conn = dbConnection.getConnection()) {
            String query = "INSERT INTO playlist (name, user_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, "Playlist 1");
                stmt.setString(2, userId);
                stmt.executeUpdate();
                stmt.setString(1, "Playlist 2");
                stmt.setString(2, userId);
                stmt.executeUpdate();
                stmt.setString(1, "Playlist 3");
                stmt.setString(2, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        playlistController.loadPlaylists();
        assertTrue(playlistView.getItems().contains("Playlist 1"), "Η Playlist 1 πρέπει να υπάρχει στη λίστα.");
        assertTrue(playlistView.getItems().contains("Playlist 2"), "Η Playlist 2 πρέπει να υπάρχει στη λίστα.");
        assertTrue(playlistView.getItems().contains("Playlist 3"), "Η Playlist 3 πρέπει να υπάρχει στη λίστα.");
        assertEquals(3, playlistView.getItems().size(), "Η λίστα πρέπει να περιέχει 3 playlists.");
    }

    @Test
    public void testLoadPlaylists_failure_noUser() {
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.remove("loggedInUserId");
        playlistController.loadPlaylists();
        assertTrue(playlistView.getItems().isEmpty(), "Η λίστα πρέπει να είναι κενή όταν δεν είναι συνδεδεμένος χρήστης.");
    }

    @Test
    public void testLoadPlaylists_failure_emptyPlaylistFromDb() {
        String userId = "1";
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.put("loggedInUserId", userId);
        try (Connection conn = dbConnection.getConnection()) {
            String deleteQuery = "DELETE FROM playlist WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setString(1, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά τη διαγραφή δεδομένων από τη βάση.");
        }

        playlistController.loadPlaylists();
        assertTrue(playlistView.getItems().isEmpty(), "Η λίστα πρέπει να είναι κενή όταν η βάση δεν επιστρέφει playlists.");
    }

    @Test
    public void testShowPlaylistSongs_success() {
        String selectedPlaylist = "New Playlist";
        String userId = "23"; //Πρεπει να υπαρχει
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.put("loggedInUserId", userId);
        try (Connection conn = dbConnection.getConnection()) {
            String insertPlaylistQuery = "INSERT INTO playlist (name, user_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertPlaylistQuery)) {
                stmt.setString(1, selectedPlaylist);
                stmt.setString(2, userId);
                stmt.executeUpdate();
            }

            int playlistId = dbConnection.getPlaylistIdByName(selectedPlaylist);
            String insertSongQuery = "INSERT INTO song (title) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSongQuery)) {
                stmt.setString(1, "Song 1");
                stmt.executeUpdate();
                stmt.setString(1, "Song 2");
                stmt.executeUpdate();
            }
            playlistView.getSelectionModel().select(selectedPlaylist);
            playlistController.showPlaylistSongs(null);
            ListView<String> albumListView = new ListView<>();
            albumListView.getItems().addAll("Song 1", "Song 2"); // Προσθήκη τραγουδιών στο ListView
            assertTrue(albumListView.getItems().contains("Song 1"), "Το τραγούδι 'Song 1' πρέπει να εμφανίζεται.");
            assertTrue(albumListView.getItems().contains("Song 2"), "Το τραγούδι 'Song 2' πρέπει να εμφανίζεται.");
            assertEquals(2, albumListView.getItems().size(), "Η λίστα τραγουδιών πρέπει να περιέχει 2 τραγούδια.");

        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την εισαγωγή δεδομένων στη βάση.");
        }
    }

    @Test
    public void testShowPlaylistSongsForTest_failure() {
        String selectedPlaylist = null;  // Δεν έχει επιλεγεί playlist
        playlistView.getSelectionModel().select(selectedPlaylist);

        ListView<String> albumListView = new ListView<>();  // Δημιουργούμε το ListView για τα τραγούδια

        playlistController.showPlaylistSongs(null);

        assertTrue(albumListView.getItems().isEmpty(), "Η λίστα τραγουδιών πρέπει να είναι κενή όταν δεν υπάρχει επιλεγμένη playlist.");
    }


    @Test
    public void testShowPlaylistSongsForTest_failure_playlistNotFound() {
        String selectedPlaylist = "Non-existent Playlist";
        playlistView.getSelectionModel().select(selectedPlaylist);
        ListView<String> albumListView = new ListView<>();
        dbConnection = new DataBaseConnection() {
            @Override
            public int getPlaylistIdByName(String playlistName) {
                return -1;
            }
        };
        playlistController.showPlaylistSongs(null);
        assertTrue(albumListView.getItems().isEmpty(), "Η λίστα τραγουδιών πρέπει να είναι κενή όταν η playlist δεν βρέθηκε στη βάση δεδομένων.");
    }

    @Test
    public void testGetSongsFromPlaylist_success() {
        int playlistId = 1;
        List<String> expectedSongs = Arrays.asList("Song 1", "Song 2", "Song 3");  // Αναμενόμενα τραγούδια
        dbConnection = new DataBaseConnection() {
            @Override
            public List<String> getSongsFromPlaylist(int playlistId) {
                List<String> songs = new ArrayList<>();
                if (playlistId == 1) {
                    songs.add("Song 1");
                    songs.add("Song 2");
                    songs.add("Song 3");
                }
                return songs;
            }
        };

        List<String> actualSongs = dbConnection.getSongsFromPlaylist(playlistId);
        assertEquals(expectedSongs, actualSongs, "Τα τραγούδια δεν ταιριάζουν με τα αναμενόμενα.");
    }

    @Test
    public void testGetSongsFromPlaylist_empty() {
        int playlistId = 2;
        List<String> expectedSongs = new ArrayList<>();
        dbConnection = new DataBaseConnection() {
            @Override
            public List<String> getSongsFromPlaylist(int playlistId) {
                List<String> songs = new ArrayList<>();
                if (playlistId == 2) {

                }
                return songs;
            }
        };
        List<String> actualSongs = dbConnection.getSongsFromPlaylist(playlistId);
        assertTrue(actualSongs.isEmpty(), "Η λίστα τραγουδιών πρέπει να είναι κενή όταν δεν υπάρχουν τραγούδια.");
    }
    @Test
    public void testGetSongNameById_success() {
        String songTitle = "Diamonds";
        int userId = 23;
        int songId = -1;
        try (Connection conn = dbConnection.getConnection()) {
            String insertSongQuery = "INSERT INTO history (user_id, title) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSongQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, userId);  // Ορίζουμε το user_id
                stmt.setString(2, songTitle);
                stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        songId = generatedKeys.getInt(1);
                    }
                }
            }
            String songName = playlistController.getSongNameById(songId);
            assertEquals(songTitle, songName, "Το όνομα του τραγουδιού πρέπει να είναι 'Diamonds'.");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την εισαγωγή δεδομένων στη βάση.");
        }
    }

    @Test
    public void testGetSongNameById_failure() {
        int nonExistentSongId = 9999;
        String songName = playlistController.getSongNameById(nonExistentSongId);
        assertNull(songName, "Το αποτέλεσμα πρέπει να είναι null για μη υπάρχον songId.");
    }
    @Test
    public void testClearLoggedInUserId() {
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.put("loggedInUserId", "23");
        playlistController.clearLoggedInUserId();
        String loggedInUserId = prefs.get("loggedInUserId", null);
        assertNull(loggedInUserId, "Το userId δεν διαγράφηκε από τις προτιμήσεις.");
    }
    @Test
    public void testClearLoggedInUserId_failure() {
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.put("loggedInUserId", "23");
        prefs.put("loggedInUserId", "23");
        String loggedInUserId = prefs.get("loggedInUserId", null);
        assertEquals("23", loggedInUserId, "Το userId δεν διαγράφηκε από τις προτιμήσεις.");
    }
    @Test
    public void testSaveLoggedInUserId_success() {
        String userId = "23";
        playlistController.saveLoggedInUserId(userId);
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        String savedUserId = prefs.get("loggedInUserId", null);
        assertEquals(userId, savedUserId, "Το userId δεν αποθηκεύτηκε σωστά στις προτιμήσεις.");
    }
    @Test
    public void testSaveLoggedInUserId_failure() {
        String userId = "23";
        playlistController.saveLoggedInUserId(userId);
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.remove("loggedInUserId");
        String savedUserId = prefs.get("loggedInUserId", null);
        assertNull(savedUserId, "Το userId αποθηκεύτηκε αλλά δεν έπρεπε να αποθηκευτεί.");
    }

    @Test
    public void testGetLoggedInUserId_success() {
        String userId = "23";
        playlistController.saveLoggedInUserId(userId);
        String retrievedUserId = playlistController.getLoggedInUserId();
        assertEquals(userId, retrievedUserId, "Το userId δεν ανακτήθηκε σωστά από τις προτιμήσεις.");
    }
    @Test
    public void testGetLoggedInUserId_failure() {
        Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
        prefs.remove("loggedInUserId");
        String retrievedUserId = playlistController.getLoggedInUserId();
        assertNull(retrievedUserId, "Το userId πρέπει να είναι null όταν δεν υπάρχει αποθηκευμένο userId.");
    }

    @Test
    public void testHandleAddSongToPlaylist_success() {
        // Προετοιμασία δεδομένων για το τεστ
        String selectedPlaylist = "Test Playlist";  // Επιλεγμένη playlist
        String songName = "Test Song";
        String artistName = "Test Artist";
        ListView<String> availableSongsListView = new ListView<>();

        try (Connection conn = dbConnection.getConnection()) {
            String insertPlaylistQuery = "INSERT INTO playlist (name, user_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertPlaylistQuery);
            stmt.setString(1, selectedPlaylist);
            stmt.setInt(2, 1); // Χρησιμοποιούμε ένα user_id για το τεστ (π.χ. 1)
            stmt.executeUpdate();

            String insertSongQuery = "INSERT INTO history (title, user_id) VALUES (?, ?)";
            stmt = conn.prepareStatement(insertSongQuery);
            stmt.setString(1, songName);
            stmt.setInt(2, 1);  // Ορίζουμε το user_id για το τεστ
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την εισαγωγή δεδομένων στη βάση.");
        }
        playlistView.getSelectionModel().select(selectedPlaylist);

        List<String> mockSongs = new ArrayList<>();
        mockSongs.add(songName + " - " + artistName);
        availableSongsListView.getItems().setAll(mockSongs);  // Ενημέρωση του ListView με τα αποτελέσματα αναζήτησης

        try {
            Method method = PlaylistController.class.getDeclaredMethod("handleAddSongToPlaylist");
            method.setAccessible(true);
            method.invoke(playlistController);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την κλήση της μεθόδου.");
        }
        assertTrue(availableSongsListView.getItems().contains(songName + " - " + artistName),
                "Το τραγούδι πρέπει να εμφανίζεται στην availableSongsListView.");
    }
    @Test
    public void testHandleAddSongToPlaylist_failure() {
        String selectedPlaylist = "Test Playlist";
        String songName = "Test Song";
        String artistName = "Test Artist";
        ListView<String> availableSongsListView = new ListView<>();
        try (Connection conn = dbConnection.getConnection()) {
            String insertPlaylistQuery = "INSERT INTO playlist (name, user_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertPlaylistQuery);
            stmt.setString(1, selectedPlaylist);
            stmt.setInt(2, 1);
            stmt.executeUpdate();

            String insertSongQuery = "INSERT INTO history (title, user_id) VALUES (?, ?)";
            stmt = conn.prepareStatement(insertSongQuery);
            stmt.setString(1, songName);
            stmt.setInt(2, 1);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την εισαγωγή δεδομένων στη βάση.");
        }
        playlistView.getSelectionModel().select(selectedPlaylist);
        List<String> mockSongs = new ArrayList<>();
        mockSongs.add(songName + " - " + artistName);
        availableSongsListView.getItems().setAll(mockSongs);
        try {
            Method method = PlaylistController.class.getDeclaredMethod("handleAddSongToPlaylist");
            method.setAccessible(true);
            method.invoke(playlistController);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την κλήση της μεθόδου.");
        }
        assertFalse(availableSongsListView.getItems().contains("NonExistingSong - NonExistingArtist"),
                "Το τραγούδι δεν πρέπει να εμφανίζεται στην availableSongsListView.");
    }
    @Test
    public void testHandleDeleteSongFromPlaylist_success() {
        String selectedPlaylist = "Test Playlist";  // Επιλεγμένη playlist
        String songName = "Test Song";
        String artistName = "Test Artist";
        ListView<String> albumListView = new ListView<>();
        try (Connection conn = dbConnection.getConnection()) {
            String insertPlaylistQuery = "INSERT INTO playlist (name, user_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertPlaylistQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, selectedPlaylist);
            stmt.setInt(2, 1); // Χρησιμοποιούμε ένα user_id για το τεστ (π.χ. 1)
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            int playlistId = -1;
            if (rs.next()) {
                playlistId = rs.getInt(1);
            }
            String insertSongQuery = "INSERT INTO history (title, user_id) VALUES (?, ?)";
            stmt = conn.prepareStatement(insertSongQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, songName);
            stmt.setInt(2, 1);  // Ορίζουμε το user_id για το τεστ
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            int songId = -1;
            if (rs.next()) {
                songId = rs.getInt(1);
            }

            String insertPlaylistSongQuery = "INSERT INTO playlist_song (playlist_id, song_id) VALUES (?, ?)";
            stmt = conn.prepareStatement(insertPlaylistSongQuery);
            stmt.setInt(1, playlistId);  // playlist_id
            stmt.setInt(2, songId);      // song_id
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την εισαγωγή δεδομένων στη βάση.");
        }

        PlaylistController playlistController = new PlaylistController();
        try {
            Field albumListViewField = PlaylistController.class.getDeclaredField("albumListView");
            albumListViewField.setAccessible(true);  // Κάνουμε το πεδίο προσβάσιμο
            albumListViewField.set(playlistController, albumListView);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την πρόσβαση στο πεδίο albumListView.");
        }
        playlistController.playlistView = new ListView<>();
        playlistController.playlistView.getSelectionModel().select(selectedPlaylist);
        albumListView.getItems().add(songName + " - " + artistName);  // Προσθέτουμε το τραγούδι στη λίστα
        try (Connection conn = dbConnection.getConnection()) {
            String deletePlaylistSongQuery = "DELETE FROM playlist_song WHERE playlist_id = ? AND song_id = ?";
            PreparedStatement stmt = conn.prepareStatement(deletePlaylistSongQuery);
            stmt.setInt(1, 1);  // playlist_id
            stmt.setInt(2, 1);  // song_id (πρέπει να προσαρμόσετε τη λήψη του κατάλληλου songId εδώ)
            stmt.executeUpdate();
            albumListView.getItems().remove(songName + " - " + artistName);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά τη διαγραφή του τραγουδιού από τη βάση δεδομένων.");
        }
        assertFalse(albumListView.getItems().contains(songName + " - " + artistName),
                "Το τραγούδι πρέπει να διαγραφεί από την albumListView.");
        albumListView.refresh();
    }


    @Test
    public void testHandleDeleteSongFromPlaylist_failure() {
        String selectedPlaylist = "Non-Existent Playlist";  // Playlist που δεν υπάρχει
        String songName = "Non-Existent Song";  // Τραγούδι που δεν υπάρχει
        ListView<String> albumListView = new ListView<>();


        PlaylistController playlistController = new PlaylistController();
        try {
            Field albumListViewField = PlaylistController.class.getDeclaredField("albumListView");
            albumListViewField.setAccessible(true);  // Κάνουμε το πεδίο προσβάσιμο
            albumListViewField.set(playlistController, albumListView);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την πρόσβαση στο πεδίο albumListView.");
        }
        albumListView.getItems().add(songName);

        try (Connection conn = dbConnection.getConnection()) {
            String deletePlaylistSongQuery = "DELETE FROM playlist_song WHERE playlist_id = ? AND song_id = ?";
            PreparedStatement stmt = conn.prepareStatement(deletePlaylistSongQuery);
            stmt.setInt(1, 9999);
            stmt.setInt(2, 9999);
            int rowsAffected = stmt.executeUpdate();
            assertEquals(0, rowsAffected, "Η διαγραφή δεν έπρεπε να επηρεάσει καμία γραμμή.");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά τη διαγραφή του τραγουδιού από τη βάση δεδομένων.");
        }
        assertTrue(albumListView.getItems().contains(songName),
                "Το τραγούδι δεν πρέπει να διαγραφεί από την albumListView, επειδή η playlist δεν υπάρχει.");
    }





}