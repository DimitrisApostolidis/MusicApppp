package org.example;

public class Song {
    private String imageUrl;
    private String title;
    private String artist;
    private String genre;
    private String duration;

    public Song(String imageUrl, String title, String artist, String genre, String duration) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
    }

    // Getters for each field
    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getGenre() { return genre; }
    public String getDuration() { return duration; }
}
