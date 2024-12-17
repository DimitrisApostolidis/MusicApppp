package org.example.Controllers.Client;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

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
    private Label suggestionLabel;

    // Initialize the ComboBoxes with options
    @FXML
    public void initialize() {
        moodComboBox.setItems(FXCollections.observableArrayList("Happy", "Sad", "Relaxed", "Energetic"));
        timeComboBox.setItems(FXCollections.observableArrayList("Morning", "Afternoon", "Evening", "Night"));
        seasonComboBox.setItems(FXCollections.observableArrayList("Spring", "Summer", "Autumn", "Winter"));
    }

    // Event handler for the Find Playlist button
    @FXML
    private void findPlaylist() {
        String mood = moodComboBox.getValue();
        String timeOfDay = timeComboBox.getValue();
        String season = seasonComboBox.getValue();
        String preferences = preferencesField.getText();

        if (mood == null || timeOfDay == null || season == null) {
            suggestionLabel.setText("Please fill in all required fields.");
            return;
        }

        // Call an API (e.g., ChatGPT API) to get playlist suggestions based on input
        String playlistSuggestion = fetchPlaylistSuggestion(mood, timeOfDay, season, preferences);

        // Display the suggestion
        suggestionLabel.setText(playlistSuggestion);
    }

    // Simulate fetching playlist suggestions using an API
    private String fetchPlaylistSuggestion(String mood, String timeOfDay, String season, String preferences) {
        // Placeholder logic for API call
        return String.format("Based on your mood (%s), time of day (%s), season (%s), and preferences (%s), we recommend: 'Your Personalized Playlist'!",
                mood, timeOfDay, season, preferences.isEmpty() ? "None" : preferences);
    }
}
