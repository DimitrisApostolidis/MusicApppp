package org.example;

public class Song {
    private String title;
    private String genre;
    private int duration; // διάρκεια σε δευτερόλεπτα

    public Song(String title, String genre, int duration) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }
}