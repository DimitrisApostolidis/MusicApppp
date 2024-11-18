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
import kong.unirest.JsonNode;
import javafx.application.Platform;
import java.util.HashMap;
import java.util.ArrayList;



public class DashboardController {

    @FXML
    private Text welcomeText; // σύνδεση με το Text στο FXML

    // Μέθοδος για ρύθμιση του ονόματος του χρήστη
    public void setUsername(String username) {
        welcomeText.setText("Welcome back, " + username + "!");
    }

    @FXML
    private TextField searchField;

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
    private ListView<String> resultsList;

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
    private Slider time;

    private LastFmApiClient apiClient = new LastFmApiClient();

    @FXML
    public void initialize() {
        searchField.setOnKeyReleased(event -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                resultsList.setVisible(true); // Εμφάνιση της λίστας
                searchArtists(query);
            } else {
                resultsList.setVisible(false); // Απόκρυψη της λίστας
            }
        });

        resultsList.setOnMouseClicked(event -> {
            String selectedArtist = resultsList.getSelectionModel().getSelectedItem();
            if (selectedArtist != null) {
                System.out.println("Selected artist: " + selectedArtist);
                // Μπορείς να καλέσεις τη μέθοδο για εμφάνιση πληροφοριών εδώ
            }
        });
    }


    private void searchArtists(String query) {
        new Thread(() -> {
            try {
                JsonNode response = apiClient.searchArtists(query);

                if (response != null && response.getObject().has("results")) {
                    var artistsArray = response.getObject()
                            .getJSONObject("results")
                            .getJSONObject("artistmatches")
                            .getJSONArray("artist");

                    List<String> artistNames = new ArrayList<>();
                    for (int i = 0; i < artistsArray.length(); i++) {
                        var artist = artistsArray.getJSONObject(i);
                        String name = artist.getString("name");
                        artistNames.add(name);
                    }

                    Platform.runLater(() -> {
                        if (!artistNames.isEmpty()) {
                            resultsList.setVisible(true); // Εμφάνιση λίστας αν υπάρχουν αποτελέσματα
                            updateResultsList(artistNames);
                        } else {
                            resultsList.setVisible(false); // Απόκρυψη αν δεν υπάρχουν αποτελέσματα
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        resultsList.setVisible(false); // Απόκρυψη σε περίπτωση σφάλματος
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    resultsList.setVisible(false); // Απόκρυψη σε περίπτωση σφάλματος
                });
            }
        }).start();
    }

    private void updateUIWithResults(String[] artistImages) {
        ImageView[] artistViews = {artist1, artist2, artist3, artist4, artist5};
        for (int i = 0; i < artistViews.length; i++) {
            if (i < artistImages.length) {
                artistViews[i].setImage(new Image(artistImages[i]));
            } else {
                artistViews[i].setImage(null); // Καθαρίζουμε αν δεν υπάρχουν αρκετά αποτελέσματα
            }
        }
    }

    private void updateResultsList(List<String> artistNames) {
        resultsList.getItems().clear();
        resultsList.getItems().addAll(artistNames);
    }




    public void displayArtistImages(String[] imageUrls) {
        ImageView[] imageViews = {artist1, artist2, artist3, artist4, artist5, artist6, artist7, artist8, artist9, artist10};
        String placeholderUrl = "https://via.placeholder.com/150"; // Placeholder εικόνα

        for (int i = 0; i < imageViews.length; i++) {
            if (i < imageUrls.length && !imageUrls[i].isEmpty()) {
                imageViews[i].setImage(new Image(imageUrls[i]));
            } else {
                imageViews[i].setImage(new Image(placeholderUrl));
            }
        }
    }}





