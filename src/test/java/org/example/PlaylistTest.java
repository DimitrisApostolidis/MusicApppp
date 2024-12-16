package org.example;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistTest {

    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {
        // Σύνδεση στη βάση δεδομένων (πρέπει να έχεις ρυθμίσει τη βάση δεδομένων και τον πίνακα playlist)
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/rapsodiaplayer", "root", "");
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







}
