package org.example;

import org.example.DataBase.DataBaseConnection;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String name;
    private List<Song> songs;
    private DataBaseConnection dbConnection;  // Αναφορά στη σύνδεση της βάσης δεδομένων

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
        this.dbConnection = new DataBaseConnection();

        // Αποθήκευση της νέας playlist στη βάση δεδομένων
        if (dbConnection.addPlaylist(name)) {
            System.out.println("Η playlist δημιουργήθηκε και προστέθηκε στη βάση δεδομένων.");
        } else {
            System.out.println("Σφάλμα κατά την προσθήκη της playlist στη βάση δεδομένων.");
        }
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public String getName() {
        return name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public int getTotalDuration() {
        return songs.stream().mapToInt(Song::getDuration).sum();
    }
}
