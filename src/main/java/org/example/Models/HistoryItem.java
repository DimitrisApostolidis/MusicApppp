package org.example.Models;

public class HistoryItem {
    private String title;
    private String artist;
    private String genre;
    private String imageUrl;

    public HistoryItem(String title, String artist, String genre, String imageUrl) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
