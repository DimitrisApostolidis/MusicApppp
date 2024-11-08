package org.example.DataBase;

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

    public boolean addSongToPlaylist(int playlist_id, String song_name) {
        try (Connection conn = DriverManager.getConnection(URL,USER,PASSWORD)) {
            String sql = "INSERT INTO playlist_song (playlist_id, song_name) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, playlist_id);
                stmt.setString(2, song_name);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
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


}
