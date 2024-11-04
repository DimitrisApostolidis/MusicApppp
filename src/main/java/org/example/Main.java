package org.example;

public class Main {
    public static void main(String[] args) {
        ArtistController controller = new ArtistController();
        controller.addArtist("John Doe", "Biography of John Doe");

        System.out.println(controller.getArtistName(0)); // Αναμένονται: John Doe

        // Δημιουργία και προσθήκη τραγουδιών
        Song song1 = new Song("Song A", "Rock", 240);
        Song song2 = new Song("Song B", "Rap", 300);

        // Δημιουργία playlist και προσθήκη τραγουδιών σε αυτή
        controller.addPlaylist("My Favorites");
        controller.addSongToPlaylist("My Favorites", song1);
        controller.addSongToPlaylist("My Favorites", song2);

        // Εμφάνιση playlist και συνολικής διάρκειας
        for (Playlist playlist : controller.getPlaylists()) {
            System.out.println("Playlist: " + playlist.getName());
            System.out.println("Total Duration: " + playlist.getTotalDuration() + " seconds");
            for (Song song : playlist.getSongs()) {
                System.out.println("- " + song.getTitle() + " (" + song.getGenre() + ")");
            }

            // Κλήση της νέας μεθόδου
            int totalDuration = calculateTotalDuration(playlist);
            System.out.println("Total Duration from method: " + totalDuration + " seconds");
        }
    }

    // Νέα μέθοδος που υπολογίζει τη συνολική διάρκεια όλων των τραγουδιών σε μια playlist
    public static int calculateTotalDuration(Playlist playlist) {
        int total = 0;
        for (Song song : playlist.getSongs()) {
            total += song.getDuration();
        }
        return total;
    }
}
