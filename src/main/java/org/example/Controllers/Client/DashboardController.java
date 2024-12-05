package org.example.Controllers.Client;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import org.example.DataBase.DataBaseConnection;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kong.unirest.json.JSONObject;
import javafx.scene.layout.VBox;
import kong.unirest.JsonNode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import kong.unirest.JsonNode;
import javafx.application.Platform;
import org.example.Controllers.LoginController;
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

    private List<String> latestSearches = new ArrayList<>();

    private ObservableList<ImageView> artistImages = FXCollections.observableArrayList();
    private static final int MAX_IMAGES = 10;

    private    DiscogsApiClient discogsApiClient = new DiscogsApiClient();
    private LastFmApiClient apiClient = new LastFmApiClient();
    private  LastFmApiClient lastFmApiClient = new LastFmApiClient();
    private final ObservableList<Image> imageList = FXCollections.observableArrayList();

    private boolean isFavorite = false; // Μεταβλητή για να παρακολουθεί αν είναι στα αγαπημένα

    public Text songTitleText;
    public ImageView songImageView;
    public Label labelNowPlaying;

    private int userId; // Ο χρήστης που είναι συνδεδεμένος
    private int selectedSongId; // Αποθηκεύει το ID του τρέχοντος τραγουδιού
    private String lastImageUrl=null;
    // Μέθοδος για να ορίσουμε το userId κατά τη σύνδεση
    private String username;
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setSelectedSongId(int songId) {
        this.selectedSongId = songId;
    }
    private int getSelectedSongId() {
        return selectedSongId;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public DashboardController() {

    }

    public DashboardController(DiscogsApiClient discogsApiClient, LastFmApiClient lastFmApiClient, ListView<String> resultsList) {
        this.resultsList = resultsList;
        this.discogsApiClient = discogsApiClient;
        this.lastFmApiClient = lastFmApiClient;
    }
    public DashboardController(String username) {
        this.username = username;
    }
    @FXML
    public void initialize() {
        // Παίρνεις το username από τον LoginController


        imageContainer.setStyle("-fx-padding: 20 0 0 0;");
        displayLatestSearches();

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
                resultsList.setVisible(true);
                searchAll(query);
            } else {
                resultsList.setVisible(false);
            }
        });

        resultsList.setOnMouseClicked(event -> {
            String selectedItem = resultsList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                String imageUrl = null;
                String artistName = null;
                String genre = "Unknown";
                String duration = "Unknown";
                String trackName = null;
                try {
                    if (selectedItem.startsWith("Track: ")) {
                        trackName = selectedItem.substring(7); // Αφαίρεση του prefix "Track: "
                        setNowPlayingImage(trackName);
                        JSONObject response = apiClient.searchTracks(trackName).getObject();
                        setNowPlayingSongName(trackName);
                        if (response.has("track")) {
                            imageUrl = response.getJSONObject("track").getJSONArray("image").getJSONObject(3).getString("#text");
                            artistName = response.getJSONObject("track").getString("artist");
                            genre = response.getJSONObject("track").optString("genre", "Unknown");
                            duration = response.getJSONObject("track").optString("duration", "Unknown");
                        }
                        // Αποθήκευση στο ιστορικό χρησιμοποιώντας το userId που πήρες
                        saveToHistory(trackName, artistName, genre, imageUrl); // Χρησιμοποιείς το userId εδώ
                    }
                    resultsList.setVisible(false); // Απόκρυψη λίστας μετά την επιλογή
                } catch (Exception e) {
                    e.printStackTrace();
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

    private void setNowPlayingSongName(String trackName) {
        songTitleText.setText(trackName);
        songTitleText.setVisible(true);
        labelNowPlaying.setVisible(true);
    }


    private void setNowPlayingImage(String trackName) {
        new Thread(() -> {
            JsonNode trackResponse = discogsApiClient.searchTracks(trackName);
            List<String> results = new ArrayList<>();
            List<String> imageUrls = new ArrayList<>();
            List<String> itemTypes = new ArrayList<>();
            List<String> bios = new ArrayList<>();
            if (trackResponse != null) extractDiscogsTracks(trackResponse, results, imageUrls, itemTypes, bios);
            if (!imageUrls.isEmpty()) {
                songImageView.setImage(new Image(imageUrls.get(0)));
            }else {
                System.out.println("imageUrls are empty");
            }
        }).start();
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
    public void searchAll(String query) {
        new Thread(() -> {
            try {
                JsonNode artistResponse = discogsApiClient.searchArtists(query);
                JsonNode trackResponse = discogsApiClient.searchTracks(query);
                JsonNode albumResponse = lastFmApiClient.searchAlbums(query);
                List<String> results = new ArrayList<>();
                List<String> imageUrls = new ArrayList<>();
                List<String> itemTypes = new ArrayList<>();
                List<String> bios = new ArrayList<>();  // Δημιουργία της λίστας bios

                if (artistResponse != null) extractDiscogsArtists(artistResponse, results, imageUrls, itemTypes, bios);
                if (trackResponse != null) extractDiscogsTracks(trackResponse, results, imageUrls, itemTypes, bios);
                if (albumResponse != null) extractLastFmAlbums(albumResponse, results, imageUrls, itemTypes, bios);

                // Προσθήκη των αποτελεσμάτων στη λίστα latestSearches
                latestSearches.addAll(results);

                Platform.runLater(() -> {
                    if (!results.isEmpty()) {
                        resultsList.setVisible(true);
                        updateResultsList(results);
                        setupListSelectionListener(results, imageUrls, itemTypes);  // Προσθήκη bios εδώ
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



    public void displayLatestSearches() {
        if (!latestSearches.isEmpty()) {
            updateResultsList(latestSearches); // Ενημέρωση της λίστας με τα τελευταία αποτελέσματα
            resultsList.setVisible(true); // Εμφάνιση της λίστας
        }
    }



    public void extractDiscogsArtists(JsonNode response, List<String> results, List<String> imageUrls, List<String> itemTypes, List<String> bios) {
        var resultsArray = response.getObject().getJSONArray("results");
        for (int i = 0; i < resultsArray.length(); i++) {
            var artist = resultsArray.getJSONObject(i);
            String artistName = artist.getString("title");

            results.add("Artist: " + artistName);
            itemTypes.add("artist");
            imageUrls.add(artist.optString("cover_image", ""));

            // Αν υπάρχει το bio του καλλιτέχνη
            String bio = artist.optString("bio", "No bio available"); // Χρησιμοποιούμε "No bio available" αν δεν υπάρχει bio
            bios.add(bio); // Προσθήκη του bio στη λίστα

        }
    }
    public void extractDiscogsTracks(JsonNode response, List<String> results, List<String> imageUrls, List<String> itemTypes, List<String> bios) {
        var resultsArray = response.getObject().getJSONArray("results");
        for (int i = 0; i < resultsArray.length(); i++) {
            var track = resultsArray.getJSONObject(i);
            String trackTitle = track.getString("title");

            results.add("Track: " + trackTitle);
            itemTypes.add("Track");
            imageUrls.add(track.optString("cover_image", ""));

            // Δεν υπάρχει bio για τα tracks, οπότε προσθέτουμε κενό
            bios.add("");

        }
    }

    public void extractLastFmAlbums(JsonNode response, List<String> results, List<String> imageUrls, List<String> itemTypes, List<String> bios) {
        var albums = response.getObject().getJSONObject("results").getJSONObject("albummatches").getJSONArray("album");
        for (int i = 0; i < albums.length(); i++) {
            var album = albums.getJSONObject(i);
            String albumName = album.getString("name");

            results.add("Album: " + albumName);
            itemTypes.add("Album");
            var images = album.getJSONArray("image");
            imageUrls.add(images.length() > 0 ? images.getJSONObject(images.length() - 1).getString("#text") : "");

            // Αν υπάρχει το bio του άλμπουμ, το προσθέτουμε
            String bio = album.optString("bio", "No bio available");
            bios.add(bio);

        }
    }










    private void updateImage(String imageUrl, String title, String itemType) {
        Platform.runLater(() -> {
            imageContainer.getChildren().clear();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                ImageView imageView = new ImageView(new Image(imageUrl));
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);

                // Δημιουργία τίτλου με τον τύπο και τον τίτλο του αντικειμένου
                Text imageTitle = new Text(itemType + ": " + title); // Χρησιμοποίησε μόνο μία φορά το itemType
                imageTitle.setStyle("-fx-fill: white; -fx-font-size: 16px;");

                VBox imageBox = new VBox(imageView, imageTitle);
                imageBox.setSpacing(10);
                imageBox.setStyle("-fx-alignment: center;");
                imageContainer.getChildren().add(imageBox);
            }
        });
    }


    private void setupListSelectionListener(List<String> results, List<String> imageUrls, List<String> itemTypes) {
        resultsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                int index = resultsList.getSelectionModel().getSelectedIndex();
                if (index >= 0 && index < imageUrls.size()) {
                    String imageUrl = imageUrls.get(index);
                    String itemType = itemTypes.get(index); // Πάρε τον τύπο
                    String title = results.get(index).substring(itemType.length() + 2); // Αφαίρεσε το "Track: ", "Album: ", κλπ.
                    updateImage(imageUrl, title, itemType); // Ενημέρωση εικόνας

                    if ("artist".equals(itemType)) {
                        updateArtistImages(title); // Καλέστε τη μέθοδο με το όνομα του καλλιτέχνη
                    }
                }
            }
        });
    }








    private void displaySingleImage(String imageUrl, String title) {
        Platform.runLater(() -> {
            imageContainer.getChildren().clear(); // Καθαρίζουμε οποιαδήποτε προηγούμενη εικόνα

            // Δημιουργία της εικόνας ImageView
            ImageView imageView = new ImageView(new Image(imageUrl));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);

            // Δημιουργία τίτλου
            Text imageTitle = new Text(title); // Χρησιμοποιούμε μόνο τον τίτλο χωρίς επανάληψη του τύπου
            imageTitle.setStyle("-fx-fill: white; -fx-font-size: 16px; -fx-alignment: center;");

            // Προσθήκη εικόνας και τίτλου στο container
            VBox imageBox = new VBox(imageView, imageTitle);
            imageBox.setSpacing(10);
            imageBox.setStyle("-fx-alignment: center;");
            imageContainer.getChildren().add(imageBox); // Προσθήκη στο container
        });
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

                    if (artist.has("bio")) {
                        var bio = artist.getJSONObject("bio");
                        if (bio.has("content")) {
                            String bioContent = bio.getString("content");
                            System.out.println("Bio of " + artistName + ": " + bioContent); // Εμφανίζουμε το bio στο terminal
                        } else {
                            System.out.println("No bio content found for " + artistName);
                        }
                    } else {
                        System.out.println("No bio found for " + artistName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }






    public void addArtistImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return; // Αν το URL είναι άκυρο, επιστρέφει

        ImageView imageView = new ImageView(new Image(imageUrl)); // Δημιουργία ImageView
        imageView.setFitWidth(150); // Θέσε μέγεθος εικόνας
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Αν ξεπεράσουμε τις 10 εικόνες, αφαιρούμε την παλιότερη
        if (artistImages.size() >= MAX_IMAGES) {
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

    private void saveToHistory(String trackName, String artistName, String genre, String imageUrl) {
        String query = "INSERT INTO history (user_id, title, artist, genre, image_url) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, LoginController.userId); // Χρήση του userId
            preparedStatement.setString(2, trackName);
            preparedStatement.setString(3, artistName != null ? artistName : "Unknown");
            preparedStatement.setString(4, genre != null ? genre : "Unknown");
            preparedStatement.setString(5, imageUrl != null ? imageUrl : "");

            preparedStatement.executeUpdate();
            System.out.println("Το ιστορικό αποθηκεύτηκε στη βάση δεδομένων.");
        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την αποθήκευση του ιστορικού: " + e.getMessage());
        }
    }

}


