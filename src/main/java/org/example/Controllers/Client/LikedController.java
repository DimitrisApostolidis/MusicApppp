package org.example.Controllers.Client;

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



    private void loadLikedSongs() {
        String query = "SELECT name, created_at FROM favourite_songs WHERE user_id = ?";
        System.out.println("Loading liked songs for user ID: " + userId);

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId); // Χρησιμοποιούμε setString αντί για setInt
            ResultSet resultSet = preparedStatement.executeQuery();

            // Καθαρισμός του VBox πριν την προσθήκη νέων στοιχείων
            likedSongsContainer.getChildren().clear();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String createdAt = resultSet.getString("created_at");

                // Δημιουργία Label για κάθε τραγούδι
                Label songLabel = new Label(name + " - Added on: " + createdAt);
                songLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #facece; -fx-padding: 5px;");

                // Προσθήκη στο VBox
                likedSongsContainer.getChildren().add(songLabel);

                System.out.println("Added song: " + name + " - " + createdAt);
            }

        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την ανάκτηση των αγαπημένων τραγουδιών: " + e.getMessage());
        }
    }

}
