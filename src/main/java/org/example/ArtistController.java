package org.example;

import java.util.ArrayList;
import java.util.List;

public class ArtistController {
    private List<Artist> artists;

    public ArtistController() {
        this.artists = new ArrayList<>();
    }

    // Δηλώνοντας τη μέθοδο με δύο παραμέτρους
    public void addArtist(String name, String biography) {
        artists.add(new Artist(name, biography));
    }

    public String getArtistName(int index) {
        return artists.get(index).getName();
    }
}
