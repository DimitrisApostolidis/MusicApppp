package org.example.Models;

public class Song {

    private String title;
    private String artist;
    private String genre;
    private String imageUrl;

    // Constructor
    public Song(String title, String artist, String genre, String imageUrl) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.imageUrl = imageUrl;
    }

    // Getter methods
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

    // Optional: Setter methods if needed
    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

