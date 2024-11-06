package org.example.Models;

public class Album {
    private String name;
    private String year;

    public Album(String name, String year) {
        this.name = name;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }
}
