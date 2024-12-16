package org.example.Views;


import org.example.Playlist;
import org.example.Song;


import java.util.ArrayList;
import java.util.List;

public class PlaylistController {
    private final List<Playlist> playlists;

    public PlaylistController() {
        playlists = new ArrayList<>();
    }

    public void addPlaylist(String name) {
        playlists.add(new Playlist());
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
