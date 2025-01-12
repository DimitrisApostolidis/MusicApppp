package org.example;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import org.example.Controllers.Client.HistoryController;
import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;


public class HistoryControllerTest {

    private final String URL = "jdbc:mysql://localhost:3306/rapsodiaplayer";
    private final String USER = "root";
    private final String PASSWORD = "";

    @Test
    public void testLoadHistory_NoHistory() {
        try {

            DataBaseConnection dbConnection = new DataBaseConnection();
            dbConnection.setConnection(URL, USER, PASSWORD); // Σύνδεση με τη βάση

            String userId = "456"; // Χρήστης χωρίς ιστορικό


            String deleteQuery = "DELETE FROM history WHERE user_id = ?";
            try (Connection connection = dbConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, userId);
                int rowsDeleted = preparedStatement.executeUpdate();
                System.out.println("Διαγράφηκαν " + rowsDeleted + " εγγραφές ιστορικού για τον χρήστη " + userId);
            }


            HistoryController historyController = new HistoryController();
            historyController.historyContainer = new VBox();
            historyController.loadHistory();


            assertEquals(0, historyController.historyContainer.getChildren().size(),
                    "Ο container πρέπει να είναι κενός όταν δεν υπάρχουν δεδομένα ιστορικού.");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την εκτέλεση της μεθόδου: " + e.getMessage());
        }
    }



    @Test
    public void testLoadHistory_SQLException() {
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db";
        String user = "wrong_user";
        String password = "wrong_password";

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            dbConnection.setConnection(wrongUrl, user, password);

            HistoryController historyController = new HistoryController();
            historyController.historyContainer = new VBox(); // Αρχικοποίηση του container
            historyController.loadHistory();

            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            System.out.println("Πραγματικό μήνυμα εξαίρεσης: " + e.getMessage());
            // Ελέγχουμε αν το μήνυμα της εξαίρεσης περιέχει το σωστό μήνυμα
            assertTrue(e.getMessage().contains("Δεν μπορεί να γίνει σύνδεση με τη βάση δεδομένων"),
                    "Η SQLException πρέπει να περιέχει μήνυμα αποτυχίας σύνδεσης.");
        }
    }

}
