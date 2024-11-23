package org.example.Models;

public class Artist {
    private String name;
    private String imageUrl;

    // Κατασκευαστής, getters και setters
    public Artist(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
