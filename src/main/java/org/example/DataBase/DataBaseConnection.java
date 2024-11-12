package org.example.DataBase;

import org.example.Playlist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseConnection {
    private Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/rapsodiaplayer";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private int failedAttempts = 0;

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
    public boolean addPlaylist(String playlistName) {
        String query = "INSERT INTO playlist (name) VALUES (?)";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, playlistName);
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

    public List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM playlist";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                Playlist playlist = new Playlist(name);
                playlists.add(playlist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlists;
    }

    public List<String> getPlaylistNames() {
        List<String> playlistNames = new ArrayList<>();
        String sql = "SELECT name FROM playlist";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                playlistNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlistNames;
    }

    public boolean addSongToPlaylist(int playlistId, int songId) {
        String sql = "INSERT INTO playlist_song (playlist_id, song_id) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getSongIdByTitle(String title) {
        String sql = "SELECT song_id FROM song WHERE name = ?";
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
            String sql = "SELECT playlist_song.song_name, song.artist FROM playlist_song JOIN song ON playlist_song.song_id = song.song_id WHERE playlist_song.playlist_id = ?";


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

    public boolean saveTrackToDatabase(String songName, String artist, String album) {
        String query = "INSERT INTO song (name, artist, album) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, songName);
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
        String query = "SELECT name FROM song";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                songs.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }



}