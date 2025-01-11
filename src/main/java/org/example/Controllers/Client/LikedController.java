package org.example.Controllers.Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.Controllers.LoginController;
import org.example.DataBase.DataBaseConnection;
import org.example.Controllers.LoginController;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LikedController implements Initializable {

    @FXML
    private VBox likedSongsContainer;

    private String userId;

    public LikedController() {
        this.likedSongsContainer = new VBox();
    }

    public VBox getLikedSongsContainer() {
        return likedSongsContainer;
    }


    public void setUserId(String userId) {
        this.userId = userId;
        System.out.println("User ID set to: " + userId);
        loadLikedSongs();

    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Παράδειγμα χρήστη που είναι ήδη συνδεδεμένος
        String loggedInUserId = LoginController.userId; // Η τιμή αυτή είναι ήδη String
        setUserId(loggedInUserId);
    }


    public void loadLikedSongs() {
        // Αρχικοποίηση του likedSongsContainer αν είναι null
        if (likedSongsContainer == null) {
            likedSongsContainer = new VBox(); // Διασφαλίζουμε ότι είναι έτοιμο το container
        }

        String query = "SELECT DISTINCT name, created_at FROM favourite_songs WHERE user_id = ?";
        System.out.println("Loading liked songs for user ID: " + userId);
        System.out.println("Executing query: " + query);

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            // Ελέγχουμε αν η σύνδεση είναι null ή κλειστή
            if (connection == null || connection.isClosed()) {
                System.err.println("Η σύνδεση με τη βάση δεδομένων αποτύχει ή είναι κλειστή.");
                return;  // Αν η σύνδεση απέτυχε ή είναι κλειστή, διακόπτουμε τη διαδικασία
            }

            // Ορισμός του userId για το query
            preparedStatement.setString(1, userId);

            // Εκτέλεση του query και ανάκτηση αποτελεσμάτων
            ResultSet resultSet = preparedStatement.executeQuery();

            // Ελέγχουμε αν το ResultSet έχει αποτελέσματα
            if (!resultSet.next()) {
                System.out.println("No liked songs found for user ID: " + userId);
            } else {
                // Επιστρέφουμε στον πρώτο δείκτη του ResultSet
                resultSet.beforeFirst(); // Σημαντικό για την εκτέλεση της επεξεργασίας

                // Καθαρισμός του likedSongsContainer πριν την προσθήκη νέων δεδομένων
                likedSongsContainer.getChildren().clear();
                System.out.println("Cleared existing songs from the container.");

                // Επεξεργασία των αποτελεσμάτων από τη βάση
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String createdAt = resultSet.getString("created_at");
                    System.out.println("Retrieved song: " + name + " - " + createdAt); // Debugging

                    // Δημιουργία νέου Label για το τραγούδι και την ημερομηνία προσθήκης
                    Label songLabel = new Label(name + " - Added on: " + createdAt);
                    songLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #facece; -fx-padding: 5px;");

                    // Προσθήκη του songLabel στο VBox στο σωστό UI thread
                    Platform.runLater(() -> likedSongsContainer.getChildren().add(songLabel));

                    System.out.println("Added song: " + name + " - " + createdAt);  // Εκτύπωση για debugging
                }
            }

        } catch (SQLException e) {
            // Εμφάνιση πιο κατανοητού μηνύματος και της πλήρους εξαίρεσης
            System.err.println("Σφάλμα κατά την ανάκτηση των αγαπημένων τραγουδιών: " + e.getMessage());
            e.printStackTrace();  // Εμφάνιση πλήρους στοίβας εξαίρεσης για debugging
        }
    }




}
