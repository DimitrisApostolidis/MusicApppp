package org.example.Controllers.Client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.util.Duration;
import kong.unirest.json.JSONArray;
import org.example.Controllers.LoginController;
import org.example.DataBase.DataBaseConnection;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kong.unirest.json.JSONObject;
import javafx.scene.layout.VBox;
import kong.unirest.JsonNode;
import org.example.Controllers.Client.DiscogsApiClient;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.example.Song;

import java.util.ArrayList;
import java.util.Objects;

import static org.example.DataBase.DataBaseConnection.getConnection;


public class DashboardController {

    public Label labelCurrentTime;
    public Label labelTotalDuration;
    public DataBaseConnection dbConnection;
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
    private AnchorPane imageContainer;

    @FXML
    private ListView<String> topTracksList;

    @FXML
    private Text artistBioText;
    private List<String> latestSearches = new ArrayList<>();
    HistoryController historyController = new HistoryController();
    private ObservableList<ImageView> artistImages = FXCollections.observableArrayList();
    private static final int MAX_IMAGES = 10;
    private MediaPlayer mediaPlayer;
    private DiscogsApiClient discogsApiClient = new DiscogsApiClient();
    private final LastFmApiClient apiClient = new LastFmApiClient();
    private LastFmApiClient lastFmApiClient = new LastFmApiClient();
    private final ObservableList<Image> imageList = FXCollections.observableArrayList();
    private List<String> playlist;  // Η λίστα με τα τραγούδια
    private int currentTrackIndex = 0;  // Ο δείκτης του τρέχοντος τραγουδιού
    private boolean isFavorite = false; // Μεταβλητή για να παρακολουθεί αν είναι στα αγαπημένα
    private JamendoApiClient jamendoApiClient = new JamendoApiClient();

    private Timeline sliderUpdater;
    public Text songTitleText;
    @FXML
    public ImageView songImageView;
    public Label labelNowPlaying;

    private int userId; // Ο χρήστης που είναι συνδεδεμένος
    // Αποθηκεύει το ID του τρέχοντος τραγουδιού
    private String lastImageUrl = null;

    private int selectedSongId = -1;
    // Μέθοδος για να ορίσουμε το userId κατά τη σύνδεση
    // Αντιστοίχιση του song_name
    private int clickCount = 0;
    @FXML
    private AnchorPane playlistPane;

    @FXML
    private ListView<String> playlistsList;
    @FXML
    private ScrollPane bioScrollPane;
    private String currentSongId;
    @FXML
    private TextArea artistBioTextArea;
    public void setUserId(int userId) {
        this.userId = userId;
    }


    public DashboardController() {

    }

    public DashboardController(DiscogsApiClient discogsApiClient, LastFmApiClient lastFmApiClient, ListView<String> resultsList) {
        this.resultsList = resultsList;
        this.discogsApiClient = discogsApiClient;
        this.lastFmApiClient = lastFmApiClient;
    }

    @FXML
    public void initialize() {
        //bioScrollPane.setVisible(false);
        loadTopTracks();
        //imageContainer.setStyle("-fx-padding: 20 0 0 0;");
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

        playlist = new ArrayList<>();
        playlist.add(getClass().getResource("/music/song1.mp3").toString());
        playlist.add(getClass().getResource("/music/song2.mp3").toString());
        playlist.add(getClass().getResource("/music/song3.mp3").toString());
        labelCurrentTime.setVisible(false);
        labelTotalDuration.setVisible(false);

        // Αρχικοποίηση του πρώτου τραγουδιού
        loadTrack(currentTrackIndex);

        playy.setOnMouseClicked(mouseEvent -> {
            playMusic();
        });

        pausee.setOnMouseClicked(mouseEvent -> {
            mediaPlayer.pause();
        });

        nextt.setOnMouseClicked(mouseEvent -> {
            playNext();
        });

        previouss.setOnMouseClicked(mouseEvent -> {
            playPrevious();
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
                String genre = "Unknown"; // Προεπιλεγμένη τιμή
                String trackName = null;

                try {
                    if (selectedItem.startsWith("Track: ")) {
                        trackName = selectedItem.substring(7); // Αφαίρεση του prefix "Track: "
                        resetFavoriteButton();
                        setNowPlayingImage(trackName);

                        if (isTrackFavorite(trackName)) {
                            favoriteButton.setStyle("-fx-background-color: #ff4081; -fx-border-color: #ff4081; -fx-text-fill: white;");
                            isFavorite = true; // Το τραγούδι είναι ήδη στα αγαπημένα
                        } else {
                            favoriteButton.setStyle("-fx-background-color: transparent; -fx-border-color: #ffffff; -fx-text-fill: #ffffff;");
                            isFavorite = false; // Το τραγούδι δεν είναι στα αγαπημένα
                        }
                        JSONObject response = apiClient.searchTracks(trackName).getObject();
                        setNowPlayingSongName(trackName);

                        if (response.has("track")) {
                            imageUrl = response.getJSONObject("track").getJSONArray("image").getJSONObject(3).getString("#text");
                            artistName = response.getJSONObject("track").getString("artist");

                            // Ανάκτηση του είδους
                            JSONArray genres = response.getJSONObject("track").optJSONArray("genres");
                            if (genres != null && genres.length() > 0) {
                                genre = genres.getString(0); // Παίρνει το πρώτο είδος
                            }
                        }

                        // Αποθήκευση στο ιστορικό
                        saveToHistory(trackName, artistName, genre, imageUrl);
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
            // Παίρνουμε το songId


            if (!isFavorite) {
                System.out.println("Το τραγούδι προστέθηκε στα αγαπημένα!");
                favoriteButton.setStyle("-fx-background-color: #ff4081; -fx-border-color: #ff4081; -fx-text-fill: white;");
                isFavorite = true;
                addToFavorites();
            } else {
                System.out.println("Το τραγούδι αφαιρέθηκε από τα αγαπημένα!");
                favoriteButton.setStyle("-fx-background-color: transparent; -fx-border-color: #ffffff; -fx-text-fill: #ffffff;");
                isFavorite = false;
                removeFromFavorites();
            }
        });
    }


    private boolean isTrackFavorite(String trackName) {
        String userId = LoginController.userId; // Πάρε το userId από το LoginController
        if (userId == null || userId.isEmpty()) {
            System.err.println("Ο χρήστης δεν είναι συνδεδεμένος.");
            return false;
        }

        // Ερώτημα SQL για να ελέγξετε αν το τραγούδι υπάρχει ήδη στα αγαπημένα του χρήστη
        String query = "SELECT COUNT(*) FROM favourite_songs WHERE user_id = ? AND name = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Αντικαταστήστε το 'track_name' με το πραγματικό όνομα στήλης που αποθηκεύει το όνομα του τραγουδιού
            statement.setString(1, userId);
            statement.setString(2, trackName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // Επιστρέφει true αν το τραγούδι υπάρχει
                }
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την επικοινωνία με τη βάση δεδομένων.");
            e.printStackTrace();
        }
        return false; // Επιστρέφει false αν δεν βρέθηκε το τραγούδι
    }

    public int getSongIdFromHistory(String userId) {
        int songId = -1;
        String query = "SELECT id FROM history WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";  // Επιλογή του πιο πρόσφατου τραγουδιού

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId); // Ορίζουμε το userId

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    songId = resultSet.getInt("id");  // Παίρνουμε το songId του πιο πρόσφατου τραγουδιού
                } else {
                    System.err.println("Δεν βρέθηκε τραγούδι στο ιστορικό για τον χρήστη.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την ανάκτηση του song_id από το ιστορικό: " + e.getMessage());
        }

        return songId;
    }

    private void resetFavoriteButton() {
        // Επαναφέρουμε την κατάσταση του κουμπιού favouriteButton
        favoriteButton.setStyle("-fx-background-color: transparent; -fx-border-color: #ffffff; -fx-text-fill: #ffffff;");
        isFavorite = false; // Επαναφέρουμε την κατάσταση του τραγουδιού από αγαπημένο σε μη αγαπημένο
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

        }).start();
    }

    private void addToFavorites() {
        // Ανάκτηση του userId από το LoginController
        String userId = LoginController.userId;
        System.out.println("User ID: " + userId);

        if (userId == null || userId.isEmpty()) {
            System.err.println("Το userId δεν είναι έγκυρο. Ο χρήστης δεν έχει συνδεθεί.");
            return;
        }

        // Ανάκτηση του τελευταίου songId και songName από το ιστορικό
        String[] songData = getLastSongFromHistory(userId);

        if (songData == null) {
            System.err.println("Δεν βρέθηκε τραγούδι στο ιστορικό.");
            return;
        }

        String songId = songData[0];    // songId ως String
        String songName = songData[1];  // songName ως String

        // Ερώτημα για την εισαγωγή στα favourite_songs
        String queryInsertFavorite = "INSERT INTO favourite_songs (user_id, song_id, name) VALUES (?, ?, ?)";

        try (Connection connection = getConnection()) {
            // Εισαγωγή του song_id, user_id και song_name στα αγαπημένα
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryInsertFavorite)) {
                preparedStatement.setString(1, userId);   // Εισάγουμε το user_id
                preparedStatement.setString(2, songId);   // Εισάγουμε το song_id ως String
                preparedStatement.setString(3, songName); // Εισάγουμε το song_name

                preparedStatement.executeUpdate();

                System.out.println("Το τραγούδι αποθηκεύτηκε στα αγαπημένα.");
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την προσθήκη του τραγουδιού στα αγαπημένα: " + e.getMessage());
        }
    }



    private String[] getLastSongFromHistory(String userId) {
        int songId = -1;
        String songName = null;

        // Ερώτημα για να πάρεις το τελευταίο songId και songName από το ιστορικό
        String query = "SELECT id, title FROM history WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId); // Εισάγουμε το user_id
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                songId = resultSet.getInt("id");        // Παίρνουμε το τελευταίο song_id
                songName = resultSet.getString("title"); // Παίρνουμε το όνομα του τραγουδιού
            }

        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την ανάκτηση του τελευταίου τραγουδιού από το ιστορικό: " + e.getMessage());
        }

        // Επιστρέφουμε το songId και songName σε ένα πίνακα
        if (songId != -1 && songName != null) {
            return new String[]{String.valueOf(songId), songName};
        } else {
            return null;  // Αν δεν βρέθηκαν δεδομένα
        }
    }


    private void removeFromFavorites() {
        String userId = LoginController.userId; // Παίρνουμε το userId από το LoginController
        if (userId == null || userId.isEmpty()) {
            System.err.println("Το userId δεν είναι έγκυρο. Ο χρήστης δεν έχει συνδεθεί.");
            return;
        }

        // Βήμα 1: Ανάκτηση του τελευταίου song_id από τα αγαπημένα
        int songId = getLastSongIdFromFavorites(userId); // Χρησιμοποιούμε την ίδια μέθοδο για να πάρουμε το τελευταίο song_id

        if (songId == -1) {
            System.err.println("Δεν βρέθηκε τραγούδι στα αγαπημένα για τον χρήστη.");
            return;
        }

        // Βήμα 2: Διαγραφή του τραγουδιού από τα αγαπημένα
        String queryDelete = "DELETE FROM favourite_songs WHERE user_id = ? AND song_id = ? "; // Διαγραφή ΜΟΝΟ του πρώτου τραγουδιού που βρέθηκε

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryDelete)) {

            preparedStatement.setString(1, userId); // userId από το LoginController
            preparedStatement.setInt(2, songId); // songId που βρήκαμε από τα αγαπημένα

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Το τραγούδι αφαιρέθηκε από τα αγαπημένα στη βάση δεδομένων.");
            } else {
                System.out.println("Δεν βρέθηκε το τραγούδι στα αγαπημένα για τον χρήστη.");
            }
        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την αφαίρεση του τραγουδιού από τα αγαπημένα: " + e.getMessage());
        }
    }

    // Μέθοδος για να ανακτήσεις το τελευταίο songId από τα αγαπημένα
    private int getLastSongIdFromFavorites(String userId) {
        int songId = -1;

        // Ερώτημα για να πάρεις το τελευταίο songId από τα αγαπημένα
        String query = "SELECT song_id FROM favourite_songs WHERE user_id = ? ";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId); // Εισάγουμε το user_id
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                songId = resultSet.getInt("song_id"); // Παίρνουμε το τελευταίο song_id
            }

        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την ανάκτηση του τελευταίου τραγουδιού από τα αγαπημένα: " + e.getMessage());
        }

        return songId;
    }
    public void searchAll(String query) {
        new Thread(() -> {
            try {
                // Αναζήτηση σε όλες τις υπηρεσίες
                JsonNode trackResponse = jamendoApiClient.searchTracks(query);
                JsonNode artistResponse = discogsApiClient.searchArtists(query);
                JsonNode albumResponse = lastFmApiClient.searchAlbums(query);

                // Λίστες για αποτελέσματα και URLs
                List<String> results = new ArrayList<>();
                List<String> streamingUrls = new ArrayList<>();
                List<String> imageUrls = new ArrayList<>();
                List<String> itemTypes = new ArrayList<>();
                List<String> bios = new ArrayList<>();  // Δημιουργία της λίστας bios
                List<String> artistNames = new ArrayList<>(); // Δημιουργία της λίστας για τα ονόματα των καλλιτεχνών

                // Επεξεργασία tracks από Jamendo
                if (trackResponse != null) {
                    var tracks = trackResponse.getObject().getJSONArray("results");
                    for (int i = 0; i < tracks.length(); i++) {
                        var track = tracks.getJSONObject(i);
                        String title = track.getString("name");
                        String streamUrl = track.getString("audio");

                        results.add("Track: " + title);
                        streamingUrls.add(streamUrl);
                        imageUrls.add(track.optString("image", "")); // Προσθήκη εικόνας αν υπάρχει
                        itemTypes.add("Track");
                        bios.add(""); // Δεν υπάρχει bio για τα tracks
                        artistNames.add(""); // Δεν υπάρχει όνομα καλλιτέχνη για τα tracks
                    }
                }

                // Επεξεργασία artists από Discogs
                if (artistResponse != null) {
                    var artists = artistResponse.getObject().getJSONArray("results");
                    for (int i = 0; i < artists.length(); i++) {
                        var artist = artists.getJSONObject(i);
                        String name = artist.getString("title");

                        results.add("Artist: " + name);
                        streamingUrls.add(null); // Δεν έχει URL αναπαραγωγής
                        imageUrls.add(artist.optString("cover_image", "")); // Εικόνα του artist
                        itemTypes.add("Artist");
                       // bios.add(artist.optString("bio", "No bio available")); // Προσθήκη bio αν υπάρχει
                        artistNames.add(name); // Προσθήκη του ονόματος του καλλιτέχνη
                    }
                }

                // Επεξεργασία albums από LastFm
                if (albumResponse != null) {
                    var albums = albumResponse.getObject().getJSONObject("results").getJSONObject("albummatches").getJSONArray("album");
                    for (int i = 0; i < albums.length(); i++) {
                        var album = albums.getJSONObject(i);
                        String name = album.getString("name");

                        results.add("Album: " + name);
                        streamingUrls.add(null); // Δεν έχει URL αναπαραγωγής
                        var images = album.getJSONArray("image");
                        imageUrls.add(images.length() > 0 ? images.getJSONObject(images.length() - 1).getString("#text") : "");
                        itemTypes.add("Album");
                        bios.add(""); // Δεν υπάρχει bio για τα albums
                        artistNames.add(""); // Δεν υπάρχει όνομα καλλιτέχνη για τα albums
                    }
                }

                // Ενημέρωση UI
                Platform.runLater(() -> {
                    if (!results.isEmpty()) {
                        resultsList.setVisible(true);
                        updateResultsList(results);
                        setupListSelectionListener(results, streamingUrls, imageUrls, itemTypes, bios, artistNames);
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


    private void setupStreamingUrls(List<String> urls) {
        resultsList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.intValue() >= 0) {
                String selectedUrl = urls.get(newValue.intValue());
                playStream(selectedUrl);
            }
        });
    }

    private void playStream(String url) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        Media media = new Media(url);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> mediaPlayer.play());
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
            artistBioTextArea.setVisible(false);

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

                Text imageTitle = new Text(itemType + ": " + title);
                imageTitle.setStyle("-fx-fill: white; -fx-font-size: 16px;");

                VBox imageBox = new VBox(imageView, imageTitle);
                imageBox.setSpacing(10);
                imageBox.setStyle("-fx-alignment: center;");
                imageContainer.getChildren().add(imageBox);
            }
        });
    }


    private void setupListSelectionListener(List<String> results, List<String> streamingUrls, List<String> imageUrls, List<String> itemTypes, List<String> bios, List<String> artistNames) {
        resultsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                int index = resultsList.getSelectionModel().getSelectedIndex();
                if (index >= 0) {
                    String selectedType = itemTypes.get(index);
                    String selectedUrl = streamingUrls.get(index);
                    String selectedImage = imageUrls.get(index);
                    String bio = bios.get(index); // Πάρε το bio από την λίστα
                    String title = results.get(index).substring(selectedType.length() + 2);
                    String artistName = artistNames.get(index); // Πάρε το όνομα του καλλιτέχνη

                    updateImage(selectedImage, title, selectedType);

                    if ("Artist".equals(selectedType)) {

                        Platform.runLater(() -> {
                            updateArtistImages(artistName);
                            artistBioTextArea.setText("");

                            artistBioTextArea.setVisible(true);

                        });


                    } else {
                        artistBioTextArea.setVisible(false);  // Απόκρυψη του TextArea αν δεν είναι καλλιτέχνης
                    }

                    if ("Track".equals(selectedType) && selectedUrl != null) {
                        playStream(selectedUrl);
                        Platform.runLater(() -> {
                            artistBioTextArea.setText("");
                            artistBioTextArea.setVisible(false);
                        });
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
                           // Platform.runLater(() -> {
                                //artistBioTextArea.setText("");
                               // artistBioTextArea.setVisible(true);
                                artistBioTextArea.setText(bioContent);

                          //  });
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
        // Αναζήτηση για το τραγούδι με βάση το user_id και το title
        String checkQuery = "SELECT id FROM history WHERE user_id = ? AND title = ?";

        try (Connection connection = getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {

            checkStatement.setString(1, LoginController.userId);
            checkStatement.setString(2, trackName);

            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // Εάν το τραγούδι υπάρχει ήδη στο ιστορικό
                int id = resultSet.getInt("id");

                // Ενημέρωση όλων των τραγουδιών για να θέσουμε το is_playing = 0 για όλα τα τραγούδια του χρήστη
                String resetQuery = "UPDATE history SET is_playing = 0 WHERE user_id = ?";
                try (PreparedStatement resetStatement = connection.prepareStatement(resetQuery)) {
                    resetStatement.setString(1, LoginController.userId);
                    resetStatement.executeUpdate();
                }

                // Ενημέρωση του τραγουδιού που ήδη υπάρχει με is_playing = 1
                String updateQuery = "UPDATE history SET artist = ?, genre = ?, image_url = ?, is_playing = 1 WHERE id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setString(1, artistName != null ? artistName : "Unknown");
                    updateStatement.setString(2, genre); // Ενημέρωση του είδους
                    updateStatement.setString(3, imageUrl != null ? imageUrl : "");
                    updateStatement.setInt(4, id);
                    updateStatement.executeUpdate();
                }

            } else {
                // Εάν το τραγούδι δεν υπάρχει, πρώτα ενημερώνουμε όλα τα τραγούδια με is_playing = 0
                String resetQuery = "UPDATE history SET is_playing = 0 WHERE user_id = ?";
                try (PreparedStatement resetStatement = connection.prepareStatement(resetQuery)) {
                    resetStatement.setString(1, LoginController.userId);
                    resetStatement.executeUpdate();
                }

                // Εισάγουμε το νέο τραγούδι στο ιστορικό με is_playing = 1
                String insertQuery = "INSERT INTO history (user_id, title, artist, genre, image_url, created_at, is_playing) VALUES (?, ?, ?, ?, ?, NOW(), 1)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setString(1, LoginController.userId);
                    insertStatement.setString(2, trackName);
                    insertStatement.setString(3, artistName != null ? artistName : "Unknown");
                    insertStatement.setString(4, genre); // Καταχώρηση του είδους
                    insertStatement.setString(5, imageUrl != null ? imageUrl : "");
                    insertStatement.executeUpdate();
                }
            }

            // Ανανεώνουμε το ιστορικό μετά την αποθήκευση
            historyController.loadHistory();

        } catch (SQLException e) {
            System.err.println("Σφάλμα κατά την αποθήκευση του ιστορικού: " + e.getMessage());
        }
    }


    private void setLabelTotalDuration() {
        int totalTime = (int) mediaPlayer.getMedia().getDuration().toSeconds();
        int totalMinutes = (int) totalTime / 60;
        int totalSeconds = (int) totalTime % 60;
        labelTotalDuration.setText(String.format("%d:%02d", totalMinutes, totalSeconds));
    }


    private void setLabelCurrentTime() {
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            // Λήψη του τρέχοντος χρόνου σε δευτερόλεπτα
            double currentTime = newValue.toSeconds();

            int currentMinutes = (int) currentTime / 60;
            int currentSeconds = (int) currentTime % 60;

            // Ενημέρωση του label με τον τρέχοντα χρόνο σε μορφή mm:ss
            labelCurrentTime.setText(String.format("%d:%02d", currentMinutes, currentSeconds));
        });
    }

    private void loadTrack(int index) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();  // Σταμάτημα του τρέχοντος τραγουδιού
            mediaPlayer.dispose(); // Απελευθέρωση πόρων του προηγούμενου MediaPlayer
        }

        // Φόρτωση του νέου τραγουδιού
        Media media = new Media(playlist.get(index));
        mediaPlayer = new MediaPlayer(media);

        bindSlider();

        // Προαιρετικά, μπορείς να κάνεις το τραγούδι να παίζει αυτόματα όταν φορτώνεται
        mediaPlayer.setOnEndOfMedia(this::playNext); // Αυτόματη μετάβαση στο επόμενο τραγούδι όταν τελειώνει
    }

    private void bindSlider() {
        // Δημιουργούμε το sliderUpdater μόνο μία φορά
        if (sliderUpdater != null) {
            sliderUpdater.stop();
        }

        // Ρύθμιση της διάρκειας του slider όταν το MediaPlayer είναι έτοιμο
        mediaPlayer.setOnReady(() -> {
            time.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
            setLabelTotalDuration();
            setLabelCurrentTime();
        });

        mediaPlayer.setOnError(() -> {
            System.out.println("Error occurred in MediaPlayer: " + mediaPlayer.getError().getMessage());
        });

        // Δημιουργία Timeline για την ενημέρωση του Slider
        sliderUpdater = new Timeline(new KeyFrame(Duration.seconds(0.2), event -> {
            // Ενημερώνουμε το Slider μόνο αν ο χρήστης δεν το μετακινεί και ο ήχος παίζει
            if (!time.isValueChanging() && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                time.setValue(mediaPlayer.getCurrentTime().toSeconds());
            }
        }));
        sliderUpdater.setCycleCount(Timeline.INDEFINITE);
        sliderUpdater.play();

        // Όταν ο χρήστης αρχίζει και σταματά να μετακινεί τον Slider
        time.valueChangingProperty().addListener((observable, wasChanging, isChanging) -> {
            if (!isChanging) {
                // Όταν ο χρήστης αφήνει τον Slider, αλλάζουμε τη θέση του MediaPlayer
                if (mediaPlayer.getStatus() == MediaPlayer.Status.READY || mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.seek(Duration.seconds(time.getValue()));
                }
            }
        });

        // Εναλλακτική ενημέρωση του MediaPlayer αν ο χρήστης αφήσει τον Slider χωρίς αλλαγές
//        time.valueProperty().addListener((observable, oldValue, newValue) -> {
//            // Εκτελούμε το seek μόνο αν ο χρήστης δεν αλλάζει την τιμή μέσω dragging
//            if (!time.isValueChanging()) {
//                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
//            }
//        });

        // Κλείσιμο του slider αν το τραγούδι ολοκληρωθεί
        mediaPlayer.setOnEndOfMedia(() -> {
            time.setValue(time.getMax()); // Ενημερώνουμε το slider όταν το τραγούδι τελειώνει
        });
    }


    public void playMusic() {
        if (mediaPlayer != null) {
            labelTotalDuration.setVisible(true);
            labelCurrentTime.setVisible(true);
            mediaPlayer.setRate(1.0); // Ρυθμίζουμε την ταχύτητα στην κανονική
            mediaPlayer.play();
            if (!mediaPlayer.isMute()) {
                String mediaSource = mediaPlayer.getMedia().getSource();
                String songName = new File(mediaSource).getName(); // Παίρνουμε το όνομα του αρχείου
                setNowPlayingSongName(songName);
                Image image = new Image(getClass().getResource("/Fxml/Client/icons/play.png").toExternalForm());
                songImageView.setImage(image);
            }
        }
    }

    public void playNext() {
        time.setValue(time.getMin());
        currentTrackIndex = (currentTrackIndex + 1) % playlist.size(); // Κυκλική εναλλαγή
        loadTrack(currentTrackIndex);
        playMusic();
    }

    public void playPrevious() {
        time.setValue(time.getMin());
        currentTrackIndex = (currentTrackIndex - 1 + playlist.size()) % playlist.size(); // Κυκλική εναλλαγή προς τα πίσω
        loadTrack(currentTrackIndex);
        playMusic();
    }

    private void loadTopTracks() {
        JsonNode response = apiClient.getTopTracks();
        if (response != null) {
            List<String> tracks = parseTopTracks(response);
            topTracksList.getItems().addAll(tracks);
        } else {
            showError("Δεν ήταν δυνατή η φόρτωση των Top Tracks από το Last.fm.");
        }
    }

    private List<String> parseTopTracks(JsonNode response) {
        List<String> tracks = new ArrayList<>();
        try {
            JSONObject jsonObject = response.getObject();
            JSONObject tracksObject = jsonObject.getJSONObject("tracks");
            JSONArray trackArray = tracksObject.getJSONArray("track");

            for (int i = 0; i < trackArray.length(); i++) {
                JSONObject track = trackArray.getJSONObject(i);
                String name = track.getString("name");
                String artist = track.getJSONObject("artist").getString("name");
                tracks.add(name + " - " + artist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Πρόβλημα κατά την επεξεργασία των δεδομένων.");
        }
        return tracks;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Σφάλμα");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void showPlaylistsPane() {
        clickCount++;
        // Δημιουργία σύνδεσης με τη βάση δεδομένων μέσω ενός αντικειμένου της κλάσης DataBaseConnection
        DataBaseConnection db = new DataBaseConnection();

        // Φόρτωσε τις playlists του χρήστη από τη βάση δεδομένων
        List<String> playlists = db.getPlaylistNames(LoginController.userId);  // Κλήση μέσω του αντικειμένου db

        if (playlists == null || playlists.isEmpty()) {
            System.out.println("Δεν βρέθηκαν playlists για τον χρήστη με ID: " + userId);
            // Αν δεν υπάρχουν playlists, μπορούμε να ενημερώσουμε τον χρήστη ή να κάνουμε κάτι άλλο
            return;
        }

        // Προσθήκη των playlists στη λίστα
        playlistsList.getItems().clear();  // Καθαρισμός της τρέχουσας λίστας
        playlistsList.getItems().addAll(playlists);  // Προσθήκη των playlists από τη βάση δεδομένων


        // Εμφάνιση του Pane
        playlistPane.setVisible(true);
        // Υπολογίζουμε το κέντρο του rootPane
        double centerX = (rootPane.getWidth() - playlistPane.getPrefWidth()) / 2;
        double centerY = (rootPane.getHeight() - playlistPane.getPrefHeight()) / 2;

        // Τοποθετούμε το playlistPane στο κέντρο του rootPane
        playlistPane.setLayoutX(centerX);
        playlistPane.setLayoutY(centerY);


       // if (clickCount == 2) {
           // playlistPane.setVisible(false);  // Κλείνουμε το παράθυρο
           // System.out.println("Το παράθυρο έκλεισε μετά από δύο πατήσεις.");
           // clickCount = 0;  // Επαναφορά του clickCount για μελλοντικές πατήσεις
       // }
    }




@FXML
private void addSongToSelectedPlaylist() {
    // Παίρνουμε το επιλεγμένο όνομα της playlist
    String selectedPlaylist = playlistsList.getSelectionModel().getSelectedItem();

    if (selectedPlaylist != null) {
        DataBaseConnection db = new DataBaseConnection();
        int playlistId = db.getPlaylistIdByName(selectedPlaylist);

        if (playlistId == -1) {
            System.out.println("Δεν βρέθηκε το ID για την playlist με το όνομα: " + selectedPlaylist);
            return;
        }

        // Ανάκτηση του τρέχοντος τραγουδιού από το ιστορικό του χρήστη
        String[] currentSong = getCurrentSongFromHistory(LoginController.userId);

        if (currentSong != null) {
            int songId = Integer.parseInt(currentSong[0]);  // songId είναι στην πρώτη θέση του πίνακα
            String songTitle = currentSong[1];  // songTitle είναι στη δεύτερη θέση του πίνακα

            // Έλεγχος αν το τραγούδι υπάρχει ήδη στην playlist
            if (db.isSongInPlaylist(playlistId, songId)) {
                System.out.println("Το τραγούδι υπάρχει ήδη στην playlist.");
                playlistPane.setVisible(false);
                return;  // Αν υπάρχει, δεν το προσθέτουμε ξανά
            }

            // Προσθήκη του τραγουδιού στην playlist
            boolean successAdd = db.addSongToPlaylist(playlistId, songId, songTitle);
            if (successAdd) {
                System.out.println("Το τραγούδι προστέθηκε επιτυχώς στην playlist: " + selectedPlaylist);
                playlistPane.setVisible(false);
            } else {
                System.out.println("Η προσθήκη του τραγουδιού απέτυχε.");
            }
        } else {
            System.out.println("Δεν βρέθηκε το τραγούδι στο ιστορικό.");
        }
    } else {
        System.out.println("Δεν επιλέξατε καμία playlist.");
    }
}

public String[] getCurrentSongFromHistory(String userId) {
    // Επιστρέφει το songId και τον τίτλο του τραγουδιού που βρίσκεται στο ιστορικό και είναι το τρέχον (is_playing = 1)
    String query = "SELECT id, title FROM history WHERE user_id = ? AND is_playing = 1";
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setString(1, userId);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                String songId = String.valueOf(resultSet.getInt("id"));
                String title = resultSet.getString("title");
                return new String[]{songId, title};  // Επιστρέφουμε το songId και τον τίτλο
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;  // Αν δεν υπάρχει τραγούδι που παίζει
}









    public void updateArtistBio(String bio) {
        artistBioTextArea.setText(bio);
        artistBioTextArea.setVisible(true);
    }

}