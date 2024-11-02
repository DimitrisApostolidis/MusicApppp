package org.example;

import java.util.ArrayList;
import java.util.List;

public class ArtistController {
    private List<Artist> artists;
    private List<Playlist> playlists;

    public ArtistController() {
        this.artists = new ArrayList<>();
        this.playlists = new ArrayList<>();
    }

    // Δηλώνει τη μέθοδο με δύο παραμέτρους
    public void addArtist(String name, String biography) {
        artists.add(new Artist(name, biography));
    }

    public String getArtistName(int index) {
        if (index < 0 || index >= artists.size()) {
            return "Invalid index";
        }
        return artists.get(index).getName();
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
