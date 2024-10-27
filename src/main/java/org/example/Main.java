package org.example;

public class Main {
    public static void main(String[] args) {
        ArtistController controller = new ArtistController();
        controller.addArtist("John Doe", "Biography of John Doe");

        System.out.println(controller.getArtistName(0)); // Αναμένονται: John Doe
    }
}