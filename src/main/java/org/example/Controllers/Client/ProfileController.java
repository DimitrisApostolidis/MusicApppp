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

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        String userId = LoginController.userId;

        if (userId != null) {
            try (Connection connection = DataBaseConnection.getConnection()) {
                String query = "SELECT username, email, profile_picture FROM user WHERE user_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, userId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("username");
                    String email = resultSet.getString("email");


                    usernameLabel.setText(name);
                    emailLabel.setText(email);


                    byte[] imageBytes = resultSet.getBytes("profile_picture");

                    if (imageBytes != null) {
                        InputStream is = new ByteArrayInputStream(imageBytes);
                        Image image = new Image(is);
                        profileImage.setImage(image);
                    }
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
            try (Connection connection = DataBaseConnection.getConnection();
                 FileInputStream fis = new FileInputStream(selectedFile)) {

                // Ενημέρωση της βάσης δεδομένων
                String userId = LoginController.userId; // Λήψη του ID του συνδεδεμένου χρήστη
                if (userId == null) {
                    System.out.println("No user is logged in.");
                    return;
                }

                String updateQuery = "UPDATE user SET profile_picture = ? WHERE user_id = ?";
                PreparedStatement statement = connection.prepareStatement(updateQuery);
                statement.setBinaryStream(1, fis, (int) selectedFile.length());
                statement.setString(2, userId);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Profile picture updated successfully.");
                    // Εμφάνιση της εικόνας στην εφαρμογή
                    Image image = new Image(selectedFile.toURI().toString());
                    profileImage.setImage(image);
                } else {
                    System.out.println("Failed to update profile picture.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





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

        String userId = LoginController.userId;
        if (userId == null) {
            System.out.println("No user is logged in.");
            return;
        }

        try (Connection connection = DataBaseConnection.getConnection()) {

            String verifyPasswordQuery = "SELECT password FROM user WHERE user_id = ?";
            PreparedStatement verifyStmt = connection.prepareStatement(verifyPasswordQuery);
            verifyStmt.setString(1, userId);
            ResultSet resultSet = verifyStmt.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                if (!storedPassword.equals(currentPassword)) {
                    System.out.println("Current password is incorrect.");
                    return;
                }
            } else {
                System.out.println("User not found.");
                return;
            }

            String updatePasswordQuery = "UPDATE user SET password = ? WHERE user_id = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updatePasswordQuery);
            updateStmt.setString(1, newPassword);
            updateStmt.setString(2, userId);

            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Password changed successfully.");
            } else {
                System.out.println("Failed to update password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
