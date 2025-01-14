package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardTest extends ApplicationTest {

    private TextField usernameField;
    private TextField passwordField;
    private Button loginButton;
    private Label errorLabel;
    private FxRobot robot;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Φόρτωση της σκηνής Login
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Client/LoginClient.fxml"));
        Scene scene = new Scene(root, 600, 430);
        scene.getStylesheets().add(getClass().getResource("/Styles/login.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @BeforeEach
    void setUpFields() {
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
    void testLoginAndPlaySong() {
        // Βήμα 1: Εισαγωγή διαπιστευτηρίων και σύνδεση
        robot.clickOn(usernameField).write("1");
        robot.clickOn(passwordField).write("1");
        robot.clickOn(loginButton);

        // Βήμα 2: Μετάβαση στο Client.fxml
        TextField searchField = robot.lookup("#searchField").queryAs(TextField.class);
        ListView<String> resultsList = robot.lookup("#resultsList").queryAs(ListView.class);

        assertNotNull(searchField, "Search field not found!");
        assertNotNull(resultsList, "Results list not found!");

        // Βήμα 3: Αναζήτηση για το τραγούδι "Like I Do"
        robot.clickOn(searchField).write("like I do it with my enemies");
        robot.type(KeyCode.ENTER);

        // Βήμα 4: Αναμονή για να εμφανιστούν τα αποτελέσματα
        robot.sleep(2000); // Αναμονή 2 δευτερολέπτων για φόρτωση αποτελεσμάτων

        // Βήμα 5: Επιλογή του πρώτου στοιχείου από τα αποτελέσματα
        assertFalse(resultsList.getItems().isEmpty(), "Η λίστα αποτελεσμάτων είναι άδεια!");
        robot.interact(() -> resultsList.getSelectionModel().select(0)); // Επιλέγει το πρώτο στοιχείο
        robot.clickOn(resultsList); // Επιβεβαίωση της επιλογής μέσω κλικ

        // Βήμα 6: Αναμονή για ενημέρωση του UI
        robot.sleep(2000);

        // Βήμα 7: Επιβεβαίωση ότι το τραγούδι παίζει
        Label nowPlayingLabel = robot.lookup("#labelNowPlaying").queryAs(Label.class);
        assertNotNull(nowPlayingLabel, "Now playing label not found!");
    }



    private void waitForResultsToLoad(ListView<String> resultsList) {
        int timeoutInSeconds = 10; // Μέγιστος χρόνος αναμονής
        int intervalInMilliseconds = 500; // Διάστημα μεταξύ ελέγχων

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutInSeconds * 1000) {
            if (!resultsList.getItems().isEmpty()) {
                return; // Τα αποτελέσματα έχουν φορτωθεί
            }
            robot.sleep(intervalInMilliseconds);
        }
        fail("Τα αποτελέσματα δεν φορτώθηκαν στη λίστα εντός χρόνου.");
    }



}
