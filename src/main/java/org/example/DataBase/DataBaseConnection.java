package org.example.DataBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import org.example.Controllers.LoginController;
import org.example.Playlist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseConnection {
    public Connection connection;
    public static final String URL = "jdbc:mysql://localhost:3306/rapsodiaplayer";
    public static final String USER = "root";
    public static final String PASSWORD = "";
    public int failedAttempts = 0;

    ObservableList<Playlist> playlists = FXCollections.observableArrayList();



    // Επαλήθευση διαπιστευτηρίων
    public boolean verifyCredentials(String username, String password) {
        String query = "SELECT * FROM user WHERE username = ? AND password = ?";


        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                failedAttempts = 0;
                return true;
            } else {
                failedAttempts++;
                System.out.println("Λανθασμένος κωδικός. Αποτυχημένες προσπάθειες: " + failedAttempts);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    public boolean registerUser(String username, String email, String password) {
        if (username == null || username.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("Όλα τα πεδία είναι υποχρεωτικά.");
            return false;
        }
        String checkUserQuery = "SELECT * FROM user WHERE username = ? OR email = ?";
        String insertUserQuery = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = this.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery)) {
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Ο χρήστης ή το email υπάρχει ήδη.");
                return false;
            }

            // Εισαγωγή νέου χρήστη
            insertStmt.setString(1, username);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);
            insertStmt.executeUpdate();

            System.out.println("Η εγγραφή ήταν επιτυχής!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Σύνδεση με τη βάση δεδομένων
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Σύνδεση με τη βάση δεδομένων επιτυχής!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void setConnection(String url, String user, String password) throws SQLException {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // Χειρισμός σφάλματος
            throw new SQLException("Δεν μπορεί να γίνει σύνδεση με τη βάση δεδομένων.", e);
        }
    }


    public boolean addPlaylist(String playlistName, String userId) {
        // Έλεγχος αν το όνομα της playlist ή το userId είναι κενά
        if (playlistName.isEmpty() || userId.isEmpty()) {
            System.out.println("Όνομα playlist ή user_id δεν μπορεί να είναι κενό.");
            return false;
        }

        // Εντολή SQL για την προσθήκη νέας playlist με user_id
        String query = "INSERT INTO playlist (name, user_id) VALUES (?, ?)";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, playlistName);  // Ρύθμιση του ονόματος της playlist
            stmt.setString(2, userId);  // Ρύθμιση του user_id για τη playlist

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Η νέα playlist προστέθηκε στη βάση δεδομένων.");
                return true;
            } else {
                System.out.println("Σφάλμα κατά την προσθήκη της playlist.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    public ObservableList<Playlist> getPlaylists(String userId) {
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();
        String sql = "SELECT * FROM playlist WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    Playlist playlist = new Playlist();
                    playlists.add(playlist);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlists;
    }


    public List<String> getPlaylistNames(String userId) {
        List<String> playlistNames = new ArrayList<>();
        String sql = "SELECT name FROM playlist WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlistNames.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlistNames;
    }




    public boolean addSongToPlaylist(int playlistId, int songId,String song_name)  {
        String sql = "INSERT INTO playlist_song (playlist_id, song_id,song_name) VALUES (?, ?,?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.setString(3, song_name);


            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getSongIdByTitle(String title) {
        String sql = "SELECT song_id FROM song WHERE title = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("song_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Επιστρέφει -1 αν δεν βρεθεί το τραγούδι
    }


    public boolean deletePlaylistByName(String playlistName) {
        String deleteSongsSql = "DELETE FROM playlist_song WHERE playlist_id = (SELECT playlist_id FROM playlist WHERE name = ? LIMIT 1)";
        String deletePlaylistSql = "DELETE FROM playlist WHERE name = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Απενεργοποιούμε το autocommit για συναλλαγές

            // Διαγραφή των σχετικών τραγουδιών
            try (PreparedStatement deleteSongsStmt = conn.prepareStatement(deleteSongsSql)) {
                deleteSongsStmt.setString(1, playlistName);
                deleteSongsStmt.executeUpdate();
            }

            // Διαγραφή της playlist
            try (PreparedStatement deletePlaylistStmt = conn.prepareStatement(deletePlaylistSql)) {
                deletePlaylistStmt.setString(1, playlistName);
                int rowsAffected = deletePlaylistStmt.executeUpdate();

                conn.commit(); // Επιβεβαίωση της συναλλαγής
                return rowsAffected > 0; // Επιστρέφει true αν διαγράφηκε η playlist
            } catch (SQLException e) {
                conn.rollback(); // Ανάκληση σε περίπτωση σφάλματος
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<String> getPlaylistSongs(int playlist_id) {
        List<String> songs = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Ανακοίνωση για το SQL με join στους πίνακες playlist_song και song
            String sql = "SELECT song.title AS song_name,song.artist FROM playlist_song "
                    + "JOIN song ON playlist_song.song_id = song.song_id WHERE playlist_song.playlist_id = ?";


            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, playlist_id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String song = "Song: " + rs.getString("song_name") + " - Artist: " + rs.getString("artist");
                        songs.add(song);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    public int getPlaylistIdByName(String playlistName) {
        String query = "SELECT playlist_id FROM playlist WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playlistName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("playlist_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Επιστρέφει -1 αν δεν βρεθεί
    }

    public boolean saveTrackToDatabase(String title, String artist, String album) {
        String query = "INSERT INTO song (title, artist, album) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, artist);
            stmt.setString(3, album);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getSongsFromDatabase() {
        List<String> songs = new ArrayList<>();
        String query = "SELECT title FROM song";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                songs.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    public boolean deleteSongById(int songId) {
        String query = "DELETE FROM song WHERE song_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, songId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Επιστρέφει true αν διαγράφηκε τουλάχιστον μία εγγραφή
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Επιστρέφει false σε περίπτωση αποτυχίας
    }

    public int getSongIdByTitleAndArtist(String songTitle, String songArtist) {
        String query = "SELECT song_id FROM song WHERE title = ? AND artist = ?";
        try (Connection conn = getConnection(); // Χρήση της μεθόδου getConnection()
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, songTitle);
            stmt.setString(2, songArtist);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("song_id"); // Επιστροφή του song_id αν βρεθεί
            } else {
                return -1; // Επιστροφή -1 αν δεν βρεθεί
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Επιστροφή -1 σε περίπτωση λάθους
        }
    }


    public String executeQuery(String username, String password) throws SQLException {
        String query = "SELECT * FROM user WHERE username = ? AND password = ?"; // SQL query με παραμέτρους

        try (Connection connection = DataBaseConnection.getConnection(); // Σύνδεση με τη βάση δεδομένων
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Ορίζουμε τις παραμέτρους στην prepared statement
            stmt.setString(1, username);  // Ρύθμιση του πρώτου παραμέτρου (username)
            stmt.setString(2, password);  // Ρύθμιση του δεύτερου παραμέτρου (password)

            try (ResultSet resultSet = stmt.executeQuery()) { // Εκτέλεση της query
                if (resultSet.next()) {
                    return resultSet.getString("username"); // Επιστροφή του username αν υπάρχει
                } else {
                    return null; // Αν δεν βρεθεί ο χρήστης με τα δεδομένα που δώσαμε
                }
            }
        }
    }



    public String getUserId(String username) {
        String query = "SELECT user_id FROM user WHERE username = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Επιστρέφει null αν δεν βρεθεί ο χρήστης
    }


    public int addSongToDatabase(String songTitle, String artistName) {
        // Έλεγχος για κενές παραμέτρους
        if (songTitle == null || songTitle.trim().isEmpty() || artistName == null || artistName.trim().isEmpty()) {
            return -1; // Επιστρέφει -1 αν ο τίτλος ή ο καλλιτέχνης είναι κενός
        }

        String sql = "INSERT INTO song (title, artist) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, songTitle);
            stmt.setString(2, artistName);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Επιστρέφουμε το songId αν η εισαγωγή ήταν επιτυχής
            } else {
                return -1; // Επιστρέφει -1 αν δεν επιστραφεί songId
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Επιστρέφει -1 σε περίπτωση σφάλματος κατά την εκτέλεση
        }
    }


    public boolean isSongCurrentlyPlayingInHistory(int userId, String songTitle) {
        String query = "SELECT is_playing FROM history WHERE user_id = ? AND title = ? ORDER BY created_at  DESC LIMIT 1 ";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setString(2, songTitle);  // Χρησιμοποιούμε το title για αναζήτηση

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("is_playing") == 1;  // Αν is_playing = 1, επιστρέφει true
            }

        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την ανάκτηση του is_playing: " + e.getMessage());
        }
        return false;  // Αν δεν βρεθεί η εγγραφή ή is_playing != 1, επιστρέφει false
    }
    public boolean isSongInPlaylist(int playlistId, int songId) {
        String query = "SELECT COUNT(*) FROM playlist_song WHERE playlist_id = ? AND song_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, playlistId);
            statement.setInt(2, songId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;  // Αν COUNT > 0, το τραγούδι υπάρχει ήδη
                }
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά τον έλεγχο της playlist: " + e.getMessage());
        }
        return false;  // Αν δεν βρεθεί το τραγούδι, επιστρέφει false
    }



    public String getSongByTitle(String title) {
        // Υποθετική υλοποίηση της μεθόδου, θα επιστρέφει το τραγούδι με βάση τον τίτλο
        return "Song " + title;  // Επιστρέφει τον τίτλο του τραγουδιού
    }


    public List<String> getSongsFromPlaylist(int playlistId) {
        List<String> songs = new ArrayList<>();

        // Ερώτημα για να πάρουμε τα song_id από τον πίνακα playlist_song για το συγκεκριμένο playlistId
        String getSongIdsQuery = "SELECT song_id FROM playlist_song WHERE playlist_id = ?";

        try (Connection conn = getConnection();  // Δημιουργούμε μία σύνδεση
             PreparedStatement stmt = conn.prepareStatement(getSongIdsQuery)) {

            stmt.setInt(1, playlistId); // Θέτουμε το playlistId στο query
            try (ResultSet rs = stmt.executeQuery()) { // Εκτελούμε το query και κλείνουμε αυτόματα το ResultSet

                // Για κάθε song_id που επιστρέφεται, παίρνουμε τον τίτλο του τραγουδιού
                while (rs.next()) {
                    int songId = rs.getInt("song_id");

                    // Ερώτημα για να πάρουμε τον τίτλο του τραγουδιού από τον πίνακα songs
                    String getSongTitleQuery = "SELECT title FROM song WHERE song_id = ?";
                    try (PreparedStatement songStmt = conn.prepareStatement(getSongTitleQuery)) {
                        songStmt.setInt(1, songId);

                        try (ResultSet songRs = songStmt.executeQuery()) {
                            if (songRs.next()) {
                                String songTitle = songRs.getString("title");
                                if (songTitle != null && !songTitle.isEmpty()) {
                                    songs.add(songTitle); // Προσθέτουμε το τραγούδι στη λίστα
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Εκτύπωση λάθους, αν υπάρχει
        }

        return songs; // Επιστρέφουμε τη λίστα με τα τραγούδια
    }



}