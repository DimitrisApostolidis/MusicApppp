package org.example;

import org.example.DataBase.DataBaseConnection;
import org.example.Views.PlaylistController;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        // Σύνδεση με τη βάση δεδομένων και εμφάνιση καλλιτεχνών

        Connection connection = DataBaseConnection.getConnection();
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                String sql = "SELECT * FROM artists";
                ResultSet resultSet = statement.executeQuery(sql);

                while (resultSet.next()) {
                    int id = resultSet.getInt("artist_id");
                    String name = resultSet.getString("name");
                    String biography = resultSet.getString("bio");

                    System.out.println("ID: " + id + ", Name: " + name + ", Biography: " + biography);
                }

                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Διαχείριση Playlist και Τραγουδιών
        PlaylistController controller = new PlaylistController();

        Song song1 = new Song("Song A", "Rock", 240);
        Song song2 = new Song("Song B", "Rap", 300);

        controller.addPlaylist("My Favorites");
        controller.addSongToPlaylist("My Favorites", song1);
        controller.addSongToPlaylist("My Favorites", song2);


        for (Playlist playlist : controller.getPlaylists()) {
            System.out.println("Playlist: " + playlist.getName());
            System.out.println("Total Duration: " + playlist.getTotalDuration() + " seconds");
            for (Song song : playlist.getSongs()) {
                System.out.println("- " + song.getTitle() + " (" + song.getGenre() + ")");
            }

            // Κλήση της νέας μεθόδου
            int totalDuration = calculateTotalDuration(playlist);
            System.out.println("Total Duration from method: " + totalDuration + " seconds");
        }
    }


    public static int calculateTotalDuration(Playlist playlist) {
        int total = 0;
        for (Song song : playlist.getSongs()) {
            total += song.getDuration();
        }
        return total;
    }


}


