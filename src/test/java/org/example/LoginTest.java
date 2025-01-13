package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.MainApp;
import javafx.scene.Parent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxRobot;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends ApplicationTest {

    private TextField usernameField;
    private TextField passwordField;
    private Button loginButton;
    private Label errorLabel;
    private FxRobot robot;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Τίτλος της Εφαρμογής");

        // Δημιουργία σκηνής (scene) με layout
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Client/LoginClient.fxml"));
        Scene scene = new Scene(root, 600, 430);

        // Εφαρμογή του style πριν την εμφάνιση του stage
        scene.getStylesheets().add(getClass().getResource("/Styles/login.css").toExternalForm());

        // Ρύθμιση του σκηνικού στη σκηνή
        primaryStage.setScene(scene);

        // Εμφάνιση του stage
        primaryStage.show();
    }






    @BeforeEach
    void setUpFields() throws Exception {
        FxToolkit.setupStage(stage -> {
            stage.show();
        });

        robot = new FxRobot();
        usernameField = robot.lookup("#username_fld").queryAs(TextField.class);
        passwordField = robot.lookup("#password_fld").queryAs(TextField.class);
        loginButton = robot.lookup("#login_btn").queryAs(Button.class);
        errorLabel = robot.lookup("#error_lbl").queryAs(Label.class);

        assertNotNull(usernameField, "Username field not found!");
        assertNotNull(passwordField, "Password field not found!");
        assertNotNull(loginButton, "Login button not found!");
        assertNotNull(errorLabel, "Error label not found!");
    }


    @Test
    void testLoginSuccess() {
        // Εισαγωγή έγκυρων διαπιστευτηρίων
        robot.clickOn(usernameField).write("1");
        robot.clickOn(passwordField).write("1");

        // Κλικ στο κουμπί login
        robot.clickOn(loginButton);

        // Επαλήθευση ότι μεταβήκαμε στο client scene
        robot.lookup("#clientScene").query();
    }

    @Test
    void testLoginFailure() {
        // Εισαγωγή μη έγκυρων διαπιστευτηρίων
        robot.clickOn(usernameField).write("wrongUser");
        robot.clickOn(passwordField).write("wrongPass");

        // Κλικ στο κουμπί login
        robot.clickOn(loginButton);

        // Επαλήθευση ότι εμφανίζεται το error message
        assertTrue(errorLabel.isVisible(), "Error label should be visible for invalid login");
        assertEquals("Invalid username or password", errorLabel.getText(), "Error message mismatch");
    }
}

