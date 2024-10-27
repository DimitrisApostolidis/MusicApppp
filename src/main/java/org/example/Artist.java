package org.example;

public class Artist {
    private String name;
    private String biography;

    // Constructor, getters, and setters
    public Artist(String name, String biography) {
        this.name = name;
        this.biography = biography;
    }

    public String getName() {
        return name;
    }

    public String getBiography() {
        return biography;
    }
}