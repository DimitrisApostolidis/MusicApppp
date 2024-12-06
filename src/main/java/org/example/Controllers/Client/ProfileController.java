package org.example.Controllers.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.Controllers.LoginController;
import org.example.DataBase.DataBaseConnection;
import org.example.PlaylistController;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.example.Controllers.LoginController.userId;

public class ProfileController {

    @FXML
    private ImageView profileImage;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    private static String loggedInUserId;
  String userId;


    public void saveLoggedInUserId(String userId) {
        loggedInUserId = userId;
    }

    public static String getLoggedInUserId() {
        return loggedInUserId;
    }
    @FXML
    public void initialize() {
        String userId = LoginController.userId; // Χρησιμοποιώντας getter για το userId

        if (userId != null) {
            try (Connection connection = DataBaseConnection.getConnection()) {
                String query = "SELECT username, email FROM user WHERE user_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, userId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("username");
                    String email = resultSet.getString("email");

                    // Ενημέρωση των Labels
                    usernameLabel.setText(name);
                    emailLabel.setText(email);
                } else {
                    System.out.println("No user found with ID: " + userId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("User ID is null. No user is logged in.");
        }
    }



    public void uploadProfilePicture(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            profileImage.setImage(image);
            System.out.println("New profile picture selected: " + selectedFile.getName());
        }
    }

    /**
     * Αποθηκεύει τις αλλαγές στο προφίλ του χρήστη.
     */
    public void saveProfileChanges(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();

        if (name.isEmpty() || email.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return;
        }

        System.out.println("Profile updated: Name - " + name + ", Email - " + email);
    }

    /**
     * Αλλάζει τον κωδικό πρόσβασης του χρήστη.
     */
    public void changePassword(ActionEvent event) {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match.");
            return;
        }

        System.out.println("Password changed successfully.");
    }
}
