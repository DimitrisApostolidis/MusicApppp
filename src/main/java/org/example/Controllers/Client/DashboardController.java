package org.example.Controllers.Client;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import org.example.DataBase.DataBaseConnection;
import org.example.Models.Album;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.example.DataBase.DataParser;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.text.Text;



public class DashboardController {

    @FXML
    private Text welcomeText;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView artist1;

    @FXML
    private ImageView artist2;

    @FXML
    private ImageView artist3;

    @FXML
    private ImageView artist4;

    @FXML
    private ImageView artist5;

    @FXML
    private ImageView artist6;

    @FXML
    private ImageView artist7;

    @FXML
    private ImageView artist8;

    @FXML
    private ImageView artist9;

    @FXML
    private ImageView artist10;

    @FXML
    private Button favoriteButton;

    @FXML
    private Button nextt;

    @FXML
    private Button pausee;

    @FXML
    private Button previouss;

    @FXML
    private Button playy;

    @FXML
    private Button searchButton;


    @FXML
    private Button addplaylist;

    @FXML
    private TextField searchField;

    @FXML
    private Slider time;

    private boolean isFavorite = false; // Μεταβλητή για να παρακολουθεί αν είναι στα αγαπημένα

    private int userId; // Ο χρήστης που είναι συνδεδεμένος
    private int selectedSongId; // Αποθηκεύει το ID του τρέχοντος τραγουδιού

    // Μέθοδος για να ορίσουμε το userId κατά τη σύνδεση

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setSelectedSongId(int songId) {
        this.selectedSongId = songId;
    }
    private int getSelectedSongId() {
        return selectedSongId;
    }

    @FXML
    public void initialize() {
        // Ρύθμιση εμφάνισης κουμπιού
        favoriteButton.setPrefSize(12, 12);
        favoriteButton.setMaxSize(12, 12);
        favoriteButton.setStyle("-fx-font-size: 10px; -fx-padding: 0; -fx-margin: 0;");

        // Ενέργεια κουμπιού
        favoriteButton.setOnAction(event -> {
            int songId = getSelectedSongId(); // Παίρνουμε το songId
            if (songId == -1) {
                System.err.println("Κανένα τραγούδι δεν είναι επιλεγμένο.");
                return;
            }

            if (!isFavorite) {
                System.out.println("Το τραγούδι προστέθηκε στα αγαπημένα!");
                favoriteButton.setStyle("-fx-background-color: #ff4081; -fx-border-color: #ff4081; -fx-text-fill: white;");
                isFavorite = true;
                addToFavorites(songId, userId);
            } else {
                System.out.println("Το τραγούδι αφαιρέθηκε από τα αγαπημένα!");
                favoriteButton.setStyle("-fx-background-color: transparent; -fx-border-color: #ffffff; -fx-text-fill: #ffffff;");
                isFavorite = false;
                removeFromFavorites(songId, userId);
            }
        });
    }


    private void addToFavorites(int songId, int userId) {
        String query = "INSERT INTO favourite_songs WHERE user_id = ? AND song_id = ?";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, songId);
            preparedStatement.executeUpdate();

            System.out.println("Το τραγούδι αποθηκεύτηκε στα αγαπημένα στη βάση δεδομένων.");
        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την προσθήκη του τραγουδιού στα αγαπημένα: " + e.getMessage());
        }
    }

    private void removeFromFavorites(int songId, int userId) {
        String query = "DELETE FROM favourite_songs WHERE user_id = ? AND song_id = ?";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, songId);
            preparedStatement.executeUpdate();

            System.out.println("Το τραγούδι αφαιρέθηκε από τα αγαπημένα στη βάση δεδομένων.");
        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την αφαίρεση του τραγουδιού από τα αγαπημένα: " + e.getMessage());
        }
    }

    public void displayArtistImages(String[] imageUrls) {
        ImageView[] imageViews = {artist1, artist2, artist3, artist4, artist5, artist6, artist7, artist8, artist9, artist10};
        for (int i = 0; i < imageUrls.length && i < imageViews.length; i++) {
            imageViews[i].setImage(new Image(imageUrls[i]));
        }
    }
}

