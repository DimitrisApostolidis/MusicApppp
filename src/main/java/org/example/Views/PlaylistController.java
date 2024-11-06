package org.example.Views;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.example.Playlist;
import org.example.Song;
import javafx.stage.StageStyle;


import java.util.ArrayList;
import java.util.List;

public class PlaylistController {
    private final List<Playlist> playlists;

    public PlaylistController() {
        playlists = new ArrayList<>();
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
