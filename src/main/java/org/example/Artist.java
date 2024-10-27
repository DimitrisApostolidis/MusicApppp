


package org.example;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String bio;
    private String imageUrl;

    // Constructors, getters, and setters
    public Artist() {}

    public Artist(String name, String bio, String imageUrl) {
        this.name = name;
        this.bio = bio;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters...
}