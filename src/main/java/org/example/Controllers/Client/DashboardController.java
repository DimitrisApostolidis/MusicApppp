package org.example.Controllers.Client;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import org.example.Models.Album;
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
    private Text welcomeText; // σύνδεση με το Text στο FXML

    // Μέθοδος για ρύθμιση του ονόματος του χρήστη
    public void setUsername(String username) {
        welcomeText.setText("Welcome back, " + username + "!");
    }

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
    private Button nextt;

    @FXML
    private Button favoriteButton;

    @FXML
    private Button pausee;

    @FXML
    private Button previouss;


    @FXML
    private Button playy;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Slider time;


    @FXML
    private boolean isFavorite = false; // Μια μεταβλητή για να παρακολουθεί αν είναι στα αγαπημένα

    @FXML
    public void initialize() {

        // Ορίζουμε το μέγεθος του κουμπιού για να είναι μικρό
        favoriteButton.setPrefWidth(12);  // Πολύ μικρό πλάτος
        favoriteButton.setPrefHeight(12);  // Πολύ μικρό ύψος
        favoriteButton.setMaxWidth(12);    // Περιορίζουμε το μέγιστο πλάτος
        favoriteButton.setMaxHeight(12);   // Περιορίζουμε το μέγιστο ύψος
        favoriteButton.setStyle("-fx-font-size: 10px; -fx-padding: 0; -fx-margin: 0;");

        // Ορισμός της ενέργειας που συμβαίνει όταν πατηθεί το κουμπί
        favoriteButton.setOnAction(event -> {
            if (!isFavorite) {
                // Αν το τραγούδι δεν είναι στα αγαπημένα, το προσθέτουμε
                System.out.println("Το τραγούδι προστέθηκε στα αγαπημένα!");

                // Αλλαγή χρώματος του κουμπιού σε ροζ
                favoriteButton.setStyle("-fx-background-color: #ff4081; -fx-border-color: #ff4081; -fx-text-fill: white;");
                isFavorite = true; // Ενημερώνουμε ότι το τραγούδι είναι στα αγαπημένα
            } else {
                // Αν το τραγούδι είναι ήδη στα αγαπημένα, το αφαιρούμε και επιστρέφουμε το κουμπί στην αρχική του κατάσταση
                System.out.println("Το τραγούδι αφαιρέθηκε από τα αγαπημένα!");

                // Επιστροφή του κουμπιού στο κανονικό του χρώμα
                favoriteButton.setStyle("-fx-background-color: transparent; -fx-border-color: #ffffff; -fx-text-fill: #ffffff;");
                isFavorite = false; // Ενημερώνουμε ότι το τραγούδι δεν είναι πια στα αγαπημένα
            }
        });
    }


    public void displayArtistImages(String[] imageUrls) {
        ImageView[] imageViews = {artist1, artist2, artist3, artist4, artist5, artist6, artist7, artist8, artist9, artist10};
        for (int i = 0; i < imageUrls.length && i < imageViews.length; i++) {
            imageViews[i].setImage(new Image(imageUrls[i]));
        }
    }


}