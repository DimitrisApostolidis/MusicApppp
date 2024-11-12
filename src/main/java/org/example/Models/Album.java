package org.example.Models;

public class Album {
    private String name;
    private String year;
    private String coverUrl;

    public Album(String name, String year, String coverUrl) {
        this.name = name;
        this.year = year;
        this.coverUrl = coverUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", year='" + year + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }
}