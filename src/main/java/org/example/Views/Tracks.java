package org.example.Views;

import java.util.ArrayList;
import java.util.List;

class Track {
    private String name;
    private String artist;
    private int duration; // Duration in seconds
    private String album;
    private int year;

    // Constructor
    public Track(String name, String artist, int duration, String album, int year) {
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.album = album;
        this.year = year;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    public String getAlbum() {
        return album;
    }

    public int getYear() {
        return year;
    }

    // Display track details
    public void displayDetails() {
        System.out.println("Track: " + name + ", Artist: " + artist + ", Album: " + album +
                ", Year: " + year + ", Duration: " + duration + " seconds");
    }
}

public class Tracks {
    private List<Track> trackList;

    // Constructor
    public Tracks() {
        trackList = new ArrayList<>();
    }

    // Add a new track
    public void addTrack(String name, String artist, int duration, String album, int year) {
        Track track = new Track(name, artist, duration, album, year);
        trackList.add(track);
    }

    // Remove a track by name
    public void removeTrack(String name) {
        trackList.removeIf(track -> track.getName().equalsIgnoreCase(name));
    }

    // Get track by name
    public Track getTrack(String name) {
        for (Track track : trackList) {
            if (track.getName().equalsIgnoreCase(name)) {
                return track;
            }
        }
        return null;
    }

    // Display all tracks
    public void displayAllTracks() {
        if (trackList.isEmpty()) {
            System.out.println("No tracks available.");
        } else {
            for (Track track : trackList) {
                track.displayDetails();
            }
        }
    }

    // Count the number of tracks
    public int countTracks() {
        return trackList.size();
    }

    // Get total duration of all tracks
    public int getTotalDuration() {
        int totalDuration = 0;
        for (Track track : trackList) {
            totalDuration += track.getDuration();
        }
        return totalDuration;
    }

    // Dummy methods to artificially expand the code
    public void dummyMethod1() {
        System.out.println("This is a dummy method 1.");
    }

    public void dummyMethod2() {
        System.out.println("This is a dummy method 2.");
    }

    public void dummyMethod3() {
        System.out.println("This is a dummy method 3.");
    }

    public void dummyMethod4() {
        System.out.println("This is a dummy method 4.");
    }

    public void dummyMethod5() {
        for (int i = 0; i < 100; i++) {
            System.out.println("Dummy print line " + i);
        }
    }

    public void dummyMethod6() {
        System.out.println("Dummy method 6 doing some repetitive task.");
        for (int i = 0; i < 50; i++) {
            System.out.println("Iteration: " + i);
        }
    }

    // More artificial complexity
    public void simulateDataProcessing() {
        System.out.println("Simulating data processing...");
        for (int i = 0; i < 200; i++) {
            System.out.println("Processing track " + (i + 1));
        }
        System.out.println("Data processing completed.");
    }

    // Generate random tracks (dummy)
    public void generateRandomTracks(int count) {
        for (int i = 1; i <= count; i++) {
            addTrack("Track " + i, "Artist " + i, i * 60, "Album " + i, 2000 + i);
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        Tracks tracks = new Tracks();

        // Add some tracks
        tracks.addTrack("Song A", "Artist A", 210, "Album A", 2020);
        tracks.addTrack("Song B", "Artist B", 180, "Album B", 2019);
        tracks.addTrack("Song C", "Artist C", 240, "Album C", 2021);

        // Display all tracks
        System.out.println("All Tracks:");
        tracks.displayAllTracks();

        // Display total duration
        System.out.println("Total Duration: " + tracks.getTotalDuration() + " seconds");

        // Generate random tracks
        tracks.generateRandomTracks(10);

        // Display the updated list of tracks
        System.out.println("\nAfter generating random tracks:");
        tracks.displayAllTracks();

        // Simulate data processing
        tracks.simulateDataProcessing();
    }
}
