package org.example.Controllers.Client;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import org.example.DataBase.DataBaseConnection;
import org.example.Models.Album;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.json.JSONObject;
import org.json.JSONArray;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import kong.unirest.JsonNode;
import kong.unirest.HttpResponse;
import javafx.application.Platform;
import java.util.HashMap;
import java.util.ArrayList;




public class DashboardController {

    @FXML
    private Text welcomeText;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView artist1, artist2, artist3, artist4, artist5, artist6, artist7, artist8, artist9, artist10;

    @FXML
    private Button nextt, favoriteButton, pausee, previouss, playy, searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Slider time;

    @FXML
    private ListView<String> resultsList;

    @FXML
    private HBox imageContainer;


    private ObservableList<ImageView> artistImages = FXCollections.observableArrayList();
    private static final int MAX_ARTISTS = 10;


    private LastFmApiClient apiClient = new LastFmApiClient();
    private final ObservableList<Image> imageList = FXCollections.observableArrayList();

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

        // Προσθήκη listener για κλικ έξω από τη λίστα
        rootPane.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
            if (resultsList.isVisible()) {
                // Υπολογισμός των συντεταγμένων του `resultsList` στη σκηνή
                double listX = resultsList.localToScene(resultsList.getBoundsInLocal()).getMinX();
                double listY = resultsList.localToScene(resultsList.getBoundsInLocal()).getMinY();
                double listWidth = resultsList.getBoundsInParent().getWidth();
                double listHeight = resultsList.getBoundsInParent().getHeight();

                // Έλεγχος αν το κλικ έγινε εκτός του `resultsList`
                if (!(event.getSceneX() >= listX && event.getSceneX() <= listX + listWidth &&
                        event.getSceneY() >= listY && event.getSceneY() <= listY + listHeight)) {
                    resultsList.setVisible(false); // Απόκρυψη λίστας
                }
            }
        });

        searchField.setOnKeyReleased(event -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                resultsList.setVisible(true); // Εμφάνιση της λίστας
                searchAll(query);
            } else {
                resultsList.setVisible(false); // Απόκρυψη της λίστας
            }
        });


        resultsList.setOnMouseClicked(event -> {
            String selectedItem = resultsList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                String imageUrl = null;

                if (selectedItem.startsWith("Artist: ")) {
                    String artistName = selectedItem.substring(8);
                    imageUrl = apiClient.getArtistInfo(artistName).getObject()
                            .getJSONObject("artist")
                            .getJSONArray("image")
                            .getJSONObject(3)
                            .getString("#text");
                } else if (selectedItem.startsWith("Track: ")) {
                    String trackName = selectedItem.substring(7);
                    // Λογική για την εικόνα του τραγουδιού (αν υπάρχει)
                } else if (selectedItem.startsWith("Album: ")) {
                    String albumName = selectedItem.substring(7);
                    // Λογική για την εικόνα του άλμπουμ (αν υπάρχει)
                }

                if (imageUrl != null) {
                    addArtistImage(imageUrl);
                }
            }
        });


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

    private void searchAll(String query) {
        new Thread(() -> {
            try {
                JsonNode artistResponse = apiClient.searchArtists(query);
                JsonNode trackResponse = apiClient.searchTracks(query);
                JsonNode albumResponse = apiClient.searchAlbums(query);

                List<String> results = new ArrayList<>();
                List<String> imageUrls = new ArrayList<>();

                // Καλλιτέχνες
                if (artistResponse != null && artistResponse.getObject().has("results")) {
                    kong.unirest.json.JSONObject resultsJson = artistResponse.getObject().getJSONObject("results");
                    kong.unirest.json.JSONArray artistArray = resultsJson.getJSONObject("artistmatches").getJSONArray("artist");

                    for (int i = 0; i < artistArray.length(); i++) {
                        kong.unirest.json.JSONObject artist = artistArray.getJSONObject(i);
                        results.add("Artist: " + artist.getString("name"));

                        kong.unirest.json.JSONArray imageArray = artist.getJSONArray("image");
                        if (imageArray.length() > 3) {
                            String imageUrl = imageArray.getJSONObject(3).getString("#text");
                            imageUrls.add(imageUrl);
                        } else {
                            imageUrls.add(""); // Αν δεν υπάρχει εικόνα, προσθέτουμε κενή συμβολοσειρά
                        }
                    }
                }

                // Τραγούδια
                if (trackResponse != null && trackResponse.getObject().has("results")) {
                    kong.unirest.json.JSONObject resultsJson = trackResponse.getObject().getJSONObject("results");
                    kong.unirest.json.JSONArray trackArray = resultsJson.getJSONObject("trackmatches").getJSONArray("track");

                    for (int i = 0; i < trackArray.length(); i++) {
                        kong.unirest.json.JSONObject track = trackArray.getJSONObject(i);
                        results.add("Track: " + track.getString("name"));

                        kong.unirest.json.JSONArray imageArray = track.getJSONArray("image");
                        if (imageArray.length() > 3) {
                            String imageUrl = imageArray.getJSONObject(3).getString("#text");
                            imageUrls.add(imageUrl);
                        } else {
                            imageUrls.add("");
                        }
                    }
                }

                // Άλμπουμ
                if (albumResponse != null && albumResponse.getObject().has("results")) {
                    kong.unirest.json.JSONObject resultsJson = albumResponse.getObject().getJSONObject("results");
                    kong.unirest.json.JSONArray albumArray = resultsJson.getJSONObject("albummatches").getJSONArray("album");

                    for (int i = 0; i < albumArray.length(); i++) {
                        kong.unirest.json.JSONObject album = albumArray.getJSONObject(i);
                        results.add("Album: " + album.getString("name"));

                        kong.unirest.json.JSONArray imageArray = album.getJSONArray("image");
                        if (imageArray.length() > 3) {
                            String imageUrl = imageArray.getJSONObject(3).getString("#text");
                            imageUrls.add(imageUrl);
                        } else {
                            imageUrls.add("");
                        }
                    }
                }

                // Ενημέρωση UI
                Platform.runLater(() -> {
                    if (!results.isEmpty()) {
                        resultsList.setVisible(true);
                        updateResultsList(results);
                        setupListSelectionListener(results, imageUrls);
                    } else {
                        resultsList.setVisible(false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> resultsList.setVisible(false));
            }
        }).start();
    }

    private void setupListSelectionListener(List<String> results, List<String> imageUrls) {
        resultsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                int index = resultsList.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < imageUrls.size()) {
                    String imageUrl = imageUrls.get(index);
                    updateImage(imageUrl);
                }
            }
        });
    }

    private void updateImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Ενημερώστε το UI με τη νέα εικόνα
            // π.χ., ImageView.setImage(new Image(imageUrl));
            System.out.println("Ενημερώθηκε η εικόνα με URL: " + imageUrl);
        } else {
            System.out.println("Δεν υπάρχει διαθέσιμη εικόνα για το επιλεγμένο στοιχείο.");
        }
    }


    private void updateResultsList(List<String> results) {
        resultsList.getItems().clear();
        resultsList.getItems().addAll(results);
    }

    private void updateArtistImages(String artistName) {
        new Thread(() -> {
            try {
                JsonNode artistInfo = apiClient.getArtistInfo(artistName); // Κάλεσε το API για πληροφορίες καλλιτέχνη
                if (artistInfo != null && artistInfo.getObject().has("artist")) {
                    var artist = artistInfo.getObject().getJSONObject("artist");
                    if (artist.has("image")) {
                        var images = artist.getJSONArray("image");
                        String largeImageUrl = null;

                        // Αναζητούμε την εικόνα με το μέγεθος "large"
                        for (int i = 0; i < images.length(); i++) {
                            var image = images.getJSONObject(i);
                            if ("large".equals(image.getString("size")) && image.has("#text") && !image.getString("#text").isEmpty()) {
                                largeImageUrl = image.getString("#text");
                            }
                        }

                        // Ενημέρωση της UI με την εικόνα
                        String finalImageUrl = largeImageUrl; // Για να μπορεί να χρησιμοποιηθεί μέσα στο Platform.runLater
                        Platform.runLater(() -> {
                            if (finalImageUrl != null && !finalImageUrl.isEmpty()) {
                                addArtistImage(finalImageUrl); // Προσθέτουμε τη νέα εικόνα
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateUIWithArtistImages() {
        imageContainer.getChildren().clear(); // Καθαρισμός του GridPane
        for (int i = 0; i < artistImages.size(); i++) {
            ImageView imageView = artistImages.get(i);
            GridPane.setRowIndex(imageView, i / 5); // Ρύθμιση γραμμής
            GridPane.setColumnIndex(imageView, i % 5); // Ρύθμιση στήλης
            imageContainer.getChildren().add(imageView);
        }
    }


    public void addArtistImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return; // Αν το URL είναι άκυρο, επιστρέφει

        ImageView imageView = new ImageView(new Image(imageUrl)); // Δημιουργία ImageView
        imageView.setFitWidth(150); // Θέσε μέγεθος εικόνας
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Αν ξεπεράσουμε τις 10 εικόνες, αφαιρούμε την παλιότερη
        if (artistImages.size() >= MAX_ARTISTS) {
            artistImages.remove(0);
        }

        // Προσθήκη νέας εικόνας και ανανέωση UI
        artistImages.add(imageView);
        updateUI();
    }




    private void updateUI() {
        imageContainer.getChildren().clear(); // imageContainer είναι το VBox/HBox
        imageContainer.getChildren().addAll(artistImages);
    }


    public void displayArtistImages(String[] imageUrls) {
        ImageView[] imageViews = {artist1, artist2, artist3, artist4, artist5, artist6, artist7, artist8, artist9, artist10};
        for (int i = 0; i < imageUrls.length && i < imageViews.length; i++) {
            imageViews[i].setImage(new Image(imageUrls[i]));
        }
    }
}
