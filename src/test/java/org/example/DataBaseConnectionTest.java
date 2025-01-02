package org.example;

import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataBaseConnectionTest {

    private static DataBaseConnection db;
    private final String URL = "jdbc:mysql://localhost:3306/rapsodiaplayer"; // Βάση δεδομένων για δοκιμή
    private final String USER = "root"; // Χρήστης βάσης δεδομένων
    private final String PASSWORD = ""; // Κωδικός βάσης δεδομένων
    private DataBaseConnection dbConnection;

    @BeforeEach
    void setUp() {
        dbConnection = new DataBaseConnection();  // Αρχικοποιούμε τη μεταβλητή dbConnection
        db = new DataBaseConnection();  // Αρχικοποιούμε το αντικείμενο db

        String url = "jdbc:mysql://localhost:3306/rapsodiaplayer";
        String user = "root";
        String password = "";
        try {
            dbConnection.setConnection(url, user, password);
        } catch (SQLException e) {
            fail("Αποτυχία σύνδεσης στη βάση δεδομένων: " + e.getMessage());
        }
    }

    @Test
    void testVerifyCredentials_Success() {

        String username = "validUser";
        String password = "validPassword";
        assertTrue(db.verifyCredentials(username, password), "Τα διαπιστευτήρια πρέπει να είναι έγκυρα.");
    }

    @Test
    void testVerifyCredentials_Failure() {

        String username = "invalidUser";
        String password = "invalidPassword";
        assertFalse(db.verifyCredentials(username, password), "Τα διαπιστευτήρια δεν πρέπει να είναι έγκυρα.");
    }

    @Test
    public void testGetConnection_Success() {

        Connection connection = db.getConnection();

        // Ελέγξτε αν η σύνδεση είναι έγκυρη
        assertNotNull(connection, "Η σύνδεση δεν είναι έγκυρη!");

        // Προσπαθούμε να εκτελέσουμε μία απλή ερώτηση για να ελέγξουμε την σύνδεση
        try (Statement stmt = connection.createStatement()) {
            assertNotNull(stmt, "Η δήλωση δεν δημιουργήθηκε σωστά!");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την εκτέλεση δήλωσης SQL");
        }
    }

    @Test
    public void testSetConnection_Success() {
        // Σωστά δεδομένα σύνδεσης (προσαρμόστε τα με την πραγματική σας βάση δεδομένων)
        String correctUrl = "jdbc:mysql://localhost:3306/rapsodiaplayer"; // Σωστό URL
        String user = "root"; // Σωστός χρήστης
        String password = "";// Σωστός κωδικός

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            // Προσπαθούμε να κάνουμε σύνδεση με τα σωστά δεδομένα
            dbConnection.setConnection(correctUrl, user, password);
            // Αν φτάσουμε εδώ, η σύνδεση ήταν επιτυχής
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Αναμενόταν επιτυχία σύνδεσης, αλλά προέκυψε σφάλμα: " + e.getMessage());
        }
    }

    @Test
    public void testSetConnection_Failure() {
        // Λανθασμένα δεδομένα σύνδεσης
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db"; // Λάθος URL
        String user = "root"; // Λάθος χρήστης
        String password = "wrongpassword"; // Λάθος κωδικός

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            // Προσπαθούμε να κάνουμε σύνδεση με τα λανθασμένα δεδομένα
            dbConnection.setConnection(wrongUrl, user, password);
            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            // Αν προκύψει SQLException, το τεστ περνάει
            e.printStackTrace();
        }
    }

    @Test
    public void testAddPlaylist_Success() {
        // Κλήση της μεθόδου addPlaylist
        boolean result = db.addPlaylist("New Playlist", "23");

        // Ελέγξτε αν η μέθοδος επιστρέφει true (που σημαίνει ότι η playlist προστέθηκε επιτυχώς)
        assertTrue(result, "Η playlist δεν προστέθηκε σωστά στη βάση δεδομένων");

        // Ελέγξτε αν η playlist έχει προστεθεί στη βάση δεδομένων
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM playlist WHERE name = 'New Playlist'");

            // Αν υπάρχει εγγραφή, το αποτέλεσμα είναι true
            assertTrue(rs.next(), "Η playlist δεν βρέθηκε στη βάση δεδομένων");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την ανάκτηση της playlist από τη βάση δεδομένων");
        }
    }


    @Test
    public void testAddPlaylist_Failure() {
        // Δοκιμή για αποτυχία, όταν παραλείπεται το όνομα της playlist
        boolean result = db.addPlaylist("", "23");

        // Ελέγξτε αν η μέθοδος επιστρέφει false όταν υπάρχει σφάλμα
        assertFalse(result, "Η μέθοδος πρέπει να επιστρέψει false όταν η playlist δεν προστίθεται");

        // Δοκιμή με μη έγκυρο userId
        result = db.addPlaylist("New Playlist", "");
        assertFalse(result, "Η μέθοδος πρέπει να επιστρέψει false όταν το user_id είναι κενό");
    }

    @Test
    public void testAddPlaylist_ExceptionHandling() {
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db";
        String user = "root";
        String password = "wrongpassword";

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            dbConnection.setConnection(wrongUrl, user, password);
            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddSongToPlaylist_Success() throws SQLException {

        DataBaseConnection dbConnection = new DataBaseConnection();


        dbConnection.setConnection(URL, USER, PASSWORD);

        int playlistId = 2363;
        int songId = 1;
        String song_name = "Song: My Song - Artist: John Doe";

        boolean result = dbConnection.addSongToPlaylist(playlistId, songId, song_name);

        assertTrue(result, "Η προσθήκη του τραγουδιού στην λίστα αναπαραγωγής πρέπει να είναι επιτυχής.");
    }

    @Test
    public void testAddSongToPlaylist_SQLException() {
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db";
        String user = "wronguser";
        String password = "wrongpassword";

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {

            dbConnection.setConnection(wrongUrl, user, password);


            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {

            assertTrue(e.getMessage().contains("Δεν μπορεί να γίνει σύνδεση με τη βάση δεδομένων"),
                    "Η SQLException πρέπει να περιέχει το μήνυμα αποτυχίας σύνδεσης.");
        }
    }

    @Test
    public void testGetSongIdByTitle_SongExists() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);

        String songTitle = "Diamonds";

        int expectedSongId = 1;
        int actualSongId = dbConnection.getSongIdByTitle(songTitle);
        assertEquals(expectedSongId, actualSongId, "Το song_id πρέπει να είναι 1 για το τραγούδι 'My Song'.");
    }

    @Test
    public void testGetSongIdByTitle_SongDoesNotExist() throws SQLException {

        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);

        String nonExistentSongTitle = "Non Existent Song";

        int songId = dbConnection.getSongIdByTitle(nonExistentSongTitle);

        assertEquals(-1, songId, "Η μέθοδος πρέπει να επιστρέψει -1 όταν το τραγούδι δεν υπάρχει.");
    }

    @Test
    public void testGetSongIdByTitle_SQLException() {
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db"; // Λάθος βάση δεδομένων
        String user = "wronguser"; // Λάθος χρήστης
        String password = "wrongpassword"; // Λάθος κωδικός

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            dbConnection.setConnection(wrongUrl, user, password);

            dbConnection.getSongIdByTitle("Any Song");

            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("Δεν μπορεί να γίνει σύνδεση με τη βάση δεδομένων"),
                    "Η SQLException πρέπει να περιέχει μήνυμα αποτυχίας σύνδεσης.");
        }
    }

    @Test
    public void testDeletePlaylistByName_PlaylistExists() throws SQLException {
        // Δημιουργία αντικειμένου της κλάσης DatabaseConnection
        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);

        String playlistName = "My Playlist";
        String userId = "23";
        boolean isAdded = dbConnection.addPlaylist(playlistName, userId);
        assertTrue(isAdded, "Η playlist πρέπει να προστεθεί επιτυχώς πριν τη διαγραφή.");

        boolean isDeleted = dbConnection.deletePlaylistByName(playlistName);

        assertTrue(isDeleted, "Η playlist πρέπει να διαγραφεί επιτυχώς.");
    }

    @Test
    public void testDeletePlaylistByName_PlaylistDoesNotExist() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);

        String nonExistentPlaylistName = "Non Existent Playlist";

        boolean isDeleted = dbConnection.deletePlaylistByName(nonExistentPlaylistName);

        assertFalse(isDeleted, "Η μέθοδος πρέπει να επιστρέψει false όταν η playlist δεν υπάρχει.");
    }

    @Test
    public void testDeletePlaylistByName_SQLException() {
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db";
        String user = "wronguser";
        String password = "wrongpassword";

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {

            dbConnection.setConnection(wrongUrl, user, password);
            dbConnection.deletePlaylistByName("Any Playlist");
            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("Δεν μπορεί να γίνει σύνδεση με τη βάση δεδομένων"),
                    "Η SQLException πρέπει να περιέχει μήνυμα αποτυχίας σύνδεσης.");
        }
    }

    @Test
    public void testGetPlaylistSongs_PlaylistHasSongs() throws SQLException {
        // Δημιουργία αντικειμένου της κλάσης DatabaseConnection
        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);

        int playlistId = 2363;

        List<String> songs = dbConnection.getPlaylistSongs(playlistId);

        assertFalse(songs.isEmpty(), "Η playlist πρέπει να περιέχει τραγούδια.");


        String expectedSong = "Song: Diamonds - Artist: rihanna";
        assertEquals(expectedSong, songs.get(0), "Το πρώτο τραγούδι πρέπει να είναι 'My Song' από τον 'John Doe'.");
    }

    @Test
    public void testGetPlaylistSongs_PlaylistHasNoSongs() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);

        int emptyPlaylistId = 2;
        List<String> songs = dbConnection.getPlaylistSongs(emptyPlaylistId);

        assertTrue(songs.isEmpty(), "Η playlist δεν πρέπει να περιέχει τραγούδια.");
    }

    @Test
    public void testGetPlaylistSongs_SQLException() {
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db"; // Λάθος βάση δεδομένων
        String user = "wronguser";
        String password = "wrongpassword";

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            dbConnection.setConnection(wrongUrl, user, password);
            dbConnection.getPlaylistSongs(1);
            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("Δεν μπορεί να γίνει σύνδεση με τη βάση δεδομένων"),
                    "Η SQLException πρέπει να περιέχει μήνυμα αποτυχίας σύνδεσης.");
        }
    }

    @Test
    public void testGetPlaylistIdByName_PlaylistExists() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);

        String playlistName = "My Playlist";

        int playlistId = dbConnection.getPlaylistIdByName(playlistName);

        assertTrue(playlistId > 0, "Πρέπει να επιστραφεί έγκυρο ID για την playlist.");
    }

    @Test
    public void testGetPlaylistIdByName_PlaylistDoesNotExist() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);

        String nonExistentPlaylistName = "Non Existent Playlist";

        int playlistId = dbConnection.getPlaylistIdByName(nonExistentPlaylistName);

        assertEquals(-1, playlistId, "Η μέθοδος πρέπει να επιστρέφει -1 όταν η playlist δεν υπάρχει.");
    }

    @Test
    public void testGetPlaylistIdByName_SQLException() {

        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db";
        String user = "wronguser"; // Λάθος χρήστης
        String password = "wrongpassword"; // Λάθος κωδικός

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            dbConnection.setConnection(wrongUrl, user, password);
            dbConnection.getPlaylistIdByName("Any Playlist");

            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("Δεν μπορεί να γίνει σύνδεση με τη βάση δεδομένων"),
                    "Η SQLException πρέπει να περιέχει μήνυμα αποτυχίας σύνδεσης.");
        }
    }

    @Test
    public void testSaveTrackToDatabase_Success() throws SQLException {
        // Δημιουργία αντικειμένου της κλάσης DatabaseConnection
        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);

        String title = "My Song";
        String artist = "Artist Name";
        String album = "Album Name";


        boolean result = dbConnection.saveTrackToDatabase(title, artist, album);
        assertTrue(result, "Η αποθήκευση του τραγουδιού πρέπει να είναι επιτυχής.");
    }

    @Test
    public void testSaveTrackToDatabase_Failure() {
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db"; // Λάθος βάση δεδομένων
        String user = "wronguser";
        String password = "wrongpassword";

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            dbConnection.setConnection(wrongUrl, user, password);
            boolean result = dbConnection.saveTrackToDatabase("My Song", "Artist Name", "Album Name");

            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {

            assertTrue(e.getMessage().contains("Δεν μπορεί να γίνει σύνδεση με τη βάση δεδομένων"),
                    "Η SQLException πρέπει να περιέχει μήνυμα αποτυχίας σύνδεσης.");
        }
    }

    @Test
    public void testGetSongIdByTitleAndArtist_Success() throws SQLException {
        // Δημιουργία αντικειμένου της κλάσης DatabaseConnection
        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);

        dbConnection.saveTrackToDatabase("Song 1", "Artist 1", "Album 1");

        int songId = dbConnection.getSongIdByTitleAndArtist("Song 1", "Artist 1");

        assertTrue(songId > 0, "Η μέθοδος πρέπει να επιστρέψει θετικό song_id.");
    }

    @Test
    public void testGetSongIdByTitleAndArtist_NotFound() throws SQLException {

        DataBaseConnection dbConnection = new DataBaseConnection();
        dbConnection.setConnection(URL, USER, PASSWORD);


        int songId = dbConnection.getSongIdByTitleAndArtist("Non-Existent Song", "Non-Existent Artist");

        assertEquals(-1, songId, "Η μέθοδος πρέπει να επιστρέψει -1 αν δεν βρεθεί το τραγούδι.");
    }

    @Test
    public void testGetSongIdByTitleAndArtist_Failure() {

        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db";
        String user = "wronguser"; // Λάθος χρήστης
        String password = "wrongpassword"; // Λάθος κωδικός

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {

            dbConnection.setConnection(wrongUrl, user, password);


            int songId = dbConnection.getSongIdByTitleAndArtist("Song 1", "Artist 1");

            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("Δεν μπορεί να γίνει σύνδεση με τη βάση δεδομένων"),
                    "Η SQLException πρέπει να περιέχει μήνυμα αποτυχίας σύνδεσης.");
        }
    }
    @Test
    public void testExecuteQuery_Success() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();
        String testUsername = "marina";
        String testPassword = "pass6";
        String result = dbConnection.executeQuery(testUsername, testPassword);
        assertNotNull(result, "Ο χρήστης με το username και password πρέπει να βρεθεί.");
    }
    @Test
    public void testExecuteQuery_Failure() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();
        String testUsername = "wrongUser";
        String testPassword = "wrongPass";
        String result = dbConnection.executeQuery(testUsername, testPassword);
        assertNull(result, "Ο χρήστης με τα δοθέντα στοιχεία δεν πρέπει να βρεθεί.");
    }

    @Test
    public void testGetUserId_Success() throws SQLException {

        DataBaseConnection dbConnection = new DataBaseConnection();
        String testUsername = "marina";


        String userId = dbConnection.getUserId(testUsername);


        assertNotNull(userId, "Ο χρήστης πρέπει να βρεθεί και να επιστραφεί το user_id.");
    }
    @Test
    public void testGetUserId_Failure() throws SQLException {

        DataBaseConnection dbConnection = new DataBaseConnection();

        String testUsername = "nonExistingUser";

        String userId = dbConnection.getUserId(testUsername);
        assertNull(userId, "Ο χρήστης δεν πρέπει να βρεθεί και να επιστραφεί null.");
    }
    @Test
    public void testAddSongToDatabase_Success() throws SQLException {

        DataBaseConnection dbConnection = new DataBaseConnection();
        String songTitle = "Test Song";
        String artistName = "Test Artist";

        int songId = dbConnection.addSongToDatabase(songTitle, artistName);

        assertTrue(songId > 0, "Το songId πρέπει να είναι θετικός αριθμός.");
    }
    @Test
    public void testAddSongToDatabase_Failure() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();

        String songTitle = "";
        String artistName = "Test Artist";

        int songId = dbConnection.addSongToDatabase(songTitle, artistName);
        assertEquals(-1, songId, "Η εισαγωγή του τραγουδιού πρέπει να αποτύχει.");
    }

    @Test
    void testIsSongCurrentlyPlayingInHistory_Success() {
        try (Connection conn = dbConnection.getConnection()) {
            String insertUser = "INSERT INTO user (user_id, username) VALUES (21, 'testUser')";
            try (PreparedStatement stmt = conn.prepareStatement(insertUser)) {
                stmt.executeUpdate();
            }
            String insertHistory = "INSERT INTO history (user_id, title, is_playing) VALUES (21, 'Song Title', 1)";
            try (PreparedStatement stmt = conn.prepareStatement(insertHistory)) {
                stmt.executeUpdate();
            }
            boolean isPlaying = dbConnection.isSongCurrentlyPlayingInHistory(1, "Song Title");
            assertTrue(isPlaying, "Το τραγούδι θα πρέπει να είναι αυτή τη στιγμή σε αναπαραγωγή.");
        } catch (SQLException e) {
            fail("Αποτυχία κατά τη διάρκεια του τεστ: " + e.getMessage());
        }
    }
    @Test
    void testIsSongCurrentlyPlayingInHistory_Failure() {
        boolean isPlaying = dbConnection.isSongCurrentlyPlayingInHistory(21, "Nonexistent Song");
        assertFalse(isPlaying, "Το τραγούδι δεν θα πρέπει να υπάρχει στην ιστορία.");
    }


    @Test
    void testIsSongInPlaylist_Success() {
        try (Connection conn = dbConnection.getConnection()) {
            String insertPlaylist = "INSERT INTO playlist (playlist_id, name) VALUES (1, 'Test Playlist')";
            try (PreparedStatement stmt = conn.prepareStatement(insertPlaylist)) {
                stmt.executeUpdate();
            }

            String insertSong = "INSERT INTO song(song_id, title) VALUES (100, 'Test Song')";
            try (PreparedStatement stmt = conn.prepareStatement(insertSong)) {
                stmt.executeUpdate();
            }
            String insertPlaylistSong = "INSERT INTO playlist_song (playlist_id, song_id) VALUES (1, 100)";
            try (PreparedStatement stmt = conn.prepareStatement(insertPlaylistSong)) {
                stmt.executeUpdate();
            }
            boolean isInPlaylist = dbConnection.isSongInPlaylist(1, 100);
            assertTrue(isInPlaylist, "Το τραγούδι πρέπει να υπάρχει στην playlist.");
        } catch (SQLException e) {
            fail("Αποτυχία κατά τη διάρκεια του τεστ: " + e.getMessage());
        }
    }

    @Test
    void testIsSongInPlaylist_Failure() {
        boolean isInPlaylist = dbConnection.isSongInPlaylist(1, 999); // playlist_id 1, song_id 999 δεν υπάρχουν
        assertFalse(isInPlaylist, "Το τραγούδι δεν θα πρέπει να υπάρχει στην playlist.");
    }
    @AfterEach
    void tearDown() {
        try (Connection conn = dbConnection.getConnection()) {
            String deleteUser = "DELETE FROM user WHERE user_id = 21";
            try (PreparedStatement stmt = conn.prepareStatement(deleteUser)) {
                stmt.executeUpdate();
            }

            String deleteHistory = "DELETE FROM history WHERE user_id = 21";
            try (PreparedStatement stmt = conn.prepareStatement(deleteHistory)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Αποτυχία καθαρισμού δεδομένων: " + e.getMessage());
        }
        try (Connection conn = dbConnection.getConnection()) {
            String deletePlaylistSong = "DELETE FROM playlist_song WHERE playlist_id = 1 AND song_id = 100";
            try (PreparedStatement stmt = conn.prepareStatement(deletePlaylistSong)) {
                stmt.executeUpdate();
            }

            String deleteSong = "DELETE FROM song WHERE song_id = 100";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSong)) {
                stmt.executeUpdate();
            }

            String deletePlaylist = "DELETE FROM playlist WHERE playlist_id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(deletePlaylist)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Αποτυχία καθαρισμού δεδομένων: " + e.getMessage());
        }
    }
    @Test
    public void testGetSongsFromPlaylist() {
        //Πρεπει να υπαρχει ηδη
        int playlistId = 2363;
        addSongToPlaylist( playlistId, 100);
        addSongToPlaylist( playlistId, 200);
        List<String> song = db.getSongsFromPlaylist(playlistId);
        //πρεπει να υπαρχουν ηδη τα τραγουδια μεσα στο song
        assertNotNull(song, "Η λίστα τραγουδιών δεν πρέπει να είναι null");
        assertEquals(2,song.size(), "Περιμέναμε 2 τραγούδια");
        assertTrue(song.contains("Song 1"), "Περιμέναμε το 'Song 1' στη λίστα");
        assertTrue(song.contains("Song 2"), "Περιμέναμε το 'Song 2' στη λίστα");
    }
    private void addSongToPlaylist(int playlistId, int songId) {
        String query = "INSERT INTO playlist_song (playlist_id, song_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/rapsodiaplayer", "root", "");
    }

    @Test
    public void testGetSongsFromPlaylist_Failure() {
        int playlistId = 2363;
        List<String> songs = db.getSongsFromPlaylist(playlistId); // Δεν περιμένουμε SQLException

        assertNotNull(songs, "Η λίστα τραγουδιών δεν πρέπει να είναι null");

        assertEquals(2, songs.size(), "Περιμέναμε 2 τραγούδια");

        boolean containsSong1 = songs.contains("Song 1");
        boolean containsSong2 = songs.contains("Song 2");
        assertTrue(containsSong1, "Περιμέναμε το 'Song 1' στη λίστα");
        assertTrue(containsSong2, "Περιμέναμε το 'Song 2' στη λίστα");

        if (!containsSong1 || !containsSong2) {
            fail("Τα τραγούδια 'Song 1' και 'Song 2' δεν βρέθηκαν στην playlist.");
        }
    }




}