package org.example.Controllers.Client;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

public class BrowseController {

    @FXML
    private ComboBox<String> moodComboBox;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private ComboBox<String> seasonComboBox;

    @FXML
    private TextField preferencesField;

    @FXML
    private Button findPlaylistBtn;

    @FXML
    private TextArea suggestionLabel;

    @FXML
    private VBox browseContainer;

    @FXML
    private Button backButton;

    @FXML
    private Label errorMessage;

    @FXML
    public void initialize() {
        moodComboBox.setItems(FXCollections.observableArrayList("Happy", "Sad", "Relaxed", "Energetic"));
        timeComboBox.setItems(FXCollections.observableArrayList("Morning", "Afternoon", "Evening", "Night"));
        seasonComboBox.setItems(FXCollections.observableArrayList("Spring", "Summer", "Autumn", "Winter"));
        suggestionLabel.setVisible(false);
    }

    @FXML
    private void findPlaylist() {
        String mood = moodComboBox.getValue();
        String timeOfDay = timeComboBox.getValue();
        String season = seasonComboBox.getValue();

        if (mood == null || timeOfDay == null || season == null) {
            suggestionLabel.setText("Please fill in all required fields.");
            suggestionLabel.setVisible(true);
            return;
        }

        errorMessage.setVisible(false); // Απόκρυψη μηνύματος αν όλα είναι εντάξει

        String response = callGeminiAPI(mood, timeOfDay, season);

        if (response != null && !response.isEmpty()) {
            String formattedPlaylist = extractPlaylist(response);
            suggestionLabel.setText(formattedPlaylist);
        } else {
            suggestionLabel.setText("Sorry, we couldn't generate a playlist at the moment.");
        }

        // Hide the input options and show the result area
        browseContainer.setVisible(false);
        suggestionLabel.setVisible(true);
        suggestionLabel.setLayoutY(30); // Move it to the top
        suggestionLabel.setPrefHeight(600); // Adjust height

        // Show the back button
        backButton.setVisible(true);
    }


    @FXML
    private void goBackToOptions() {
        // Clear the previous playlist and reset fields
        suggestionLabel.setText("");
        suggestionLabel.setVisible(false);

        moodComboBox.setValue(null);
        timeComboBox.setValue(null);
        seasonComboBox.setValue(null);

        // Show the input options and hide the result area
        browseContainer.setVisible(true);

        // Hide the back button
        backButton.setVisible(false);
    }


    // Μέθοδος για να μορφοποιήσουμε το κείμενο σε playlist
    private String formatPlaylist(String response) {
        // Χωρίζουμε το κείμενο σε γραμμές
        String[] songs = response.split("\n");
        StringBuilder formattedPlaylist = new StringBuilder();

        for (String song : songs) {
            // Αφαιρούμε τα εισαγωγικά και διαμορφώνουμε τη γραμμή
            song = song.replaceAll("\"", "").trim(); // Αφαιρούμε τα εισαγωγικά
            if (!song.isEmpty()) {
                formattedPlaylist.append(song).append("\n");
            }
        }

        return formattedPlaylist.toString();
    }



    private String callGeminiAPI(String mood, String timeOfDay, String season) {
        try {
            // Δημιουργία prompt για το AI
            String prompt = "Create a playlist of 5 songs for the following conditions:\n" +
                    "Mood: " + mood + "\n" +
                    "Time of Day: " + timeOfDay + "\n" +
                    "Season: " + season + "\n" +
                    "Output the playlist as '1. Song Name by Artist Name'.";

            // Ρύθμιση του Gemini API
            String urlString = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAxeB4oCgZhQUFelETON-qwGknb8QQ0_-I";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Σώμα του αιτήματος
            String requestBody = "{\n" +
                    "  \"contents\": [{\n" +
                    "    \"parts\": [{ \"text\": \"" + prompt + "\" }]\n" +
                    "  }]\n" +
                    "}";

            // Αποστολή αιτήματος
            connection.getOutputStream().write(requestBody.getBytes());

            // Λήψη απόκρισης
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Επιστροφή της απάντησης
                return response.toString();
            } else {
                return "Error: Received response code " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating playlist.";
        }
    }

    private String extractPlaylist(String response) {
        try {
            // Μετατροπή της απόκρισης σε JSON
            JSONObject jsonResponse = new JSONObject(response);
            String playlist = jsonResponse
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            // Επιστροφή μόνο της λίστας των τραγουδιών
            return playlist.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing playlist data.";
        }
    }

}



