package org.example.Models;

import java.sql.Timestamp;

public class Track {
    private String name;
    private String artist;
    private String album;
    private Timestamp createdAt;
    private String genre;
    private String imageUrl;

    // Constructor με έξι παραμέτρους
    public Track(String name, String artist, String album, Timestamp createdAt, String genre, String imageUrl) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.genre = genre != null ? genre : "Unknown";
        this.imageUrl = imageUrl != null ? imageUrl : "";
        this.createdAt = createdAt != null ? createdAt : new Timestamp(System.currentTimeMillis());
    }

    // Constructor με τρεις παραμέτρους
    public Track(String name, String artist, String album) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.genre = "Unknown"; // Προεπιλεγμένη τιμή
        this.imageUrl = "";     // Κενό URL
        this.createdAt = new Timestamp(System.currentTimeMillis()); // Τρέχουσα χρονική στιγμή
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", createdAt=" + createdAt +
                ", genre='" + genre + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
