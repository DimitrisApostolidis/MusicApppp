package org.example;


import javafx.scene.layout.VBox;
import org.example.Controllers.Client.LikedController;
import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;

public class LikedControllerTest {



    private final String URL = "jdbc:mysql://localhost:3306/rapsodiaplayer"; // Βάση δεδομένων για δοκιμή
    private final String USER = "root"; // Χρήστης βάσης δεδομένων
    private final String PASSWORD = ""; // Κωδικός βάσης δεδομένων





    @Test
    public void testLoadLikedSongs_NoLikedSongs() {
        try {
            // Δημιουργία σύνδεσης με τη βάση
            DataBaseConnection dbConnection = new DataBaseConnection();
            dbConnection.setConnection(URL, USER, PASSWORD); // Σύνδεση με τη βάση

            String userId = "456"; // Χρήστης χωρίς αγαπημένα τραγούδια

            // Διασφαλίζουμε ότι δεν υπάρχουν αγαπημένα τραγούδια για αυτόν τον χρήστη
            String deleteQuery = "DELETE FROM favourite_songs WHERE user_id = ?";
            try (Connection connection = dbConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, userId);
                int rowsDeleted = preparedStatement.executeUpdate();
                System.out.println("Διαγράφηκαν " + rowsDeleted + " εγγραφές για τον χρήστη " + userId);
            }

            // Εκτέλεση της μεθόδου που ελέγχεται
            LikedController likedController = new LikedController();
            likedController.setUserId(userId); // Ορισμός του userId πριν τη φόρτωση
            likedController.loadLikedSongs(); // Φόρτωση αγαπημένων τραγουδιών

            // Έλεγχος ότι ο container είναι κενός
            assertEquals(0, likedController.getLikedSongsContainer().getChildren().size(),
                    "Ο container πρέπει να είναι κενός όταν δεν υπάρχουν αγαπημένα τραγούδια.");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την εκτέλεση της μεθόδου: " + e.getMessage());
        }
    }










    @Test
    public void testLoadLikedSongs_SQLException() {
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db";
        String user = "wrong_user";
        String password = "wrong_password";

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            dbConnection.setConnection(wrongUrl, user, password);

            LikedController likedController = new LikedController();
            likedController.setUserId("123");
            likedController.loadLikedSongs();

            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            System.out.println("Πραγματικό μήνυμα εξαίρεσης: " + e.getMessage());
            // Ελέγχουμε αν το μήνυμα της εξαίρεσης περιέχει το σωστό μήνυμα
            assertTrue(e.getMessage().contains("Δεν μπορεί να γίνει σύνδεση με τη βάση δεδομένων"),
                    "Η SQLException πρέπει να περιέχει μήνυμα αποτυχίας σύνδεσης.");
        }
    }
}