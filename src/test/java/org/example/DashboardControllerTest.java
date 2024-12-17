package org.example;

import org.example.Controllers.Client.DashboardController;
import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.Controllers.Client.DashboardController;
import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


class DashboardControllerTest {

    // Χρησιμοποιούμε μια in-memory βάση δεδομένων για το test (π.χ., H2)
    @Test
    void testGetSongIdFromHistory_WithSQLException() throws Exception {
        // Δημιουργία της βάσης δεδομένων και σύνδεση με in-memory DB (π.χ., H2)
        DataBaseConnection dbConnection = new DataBaseConnection();

        // Ρύθμιση της σύνδεσης στην in-memory βάση
        Connection connection = dbConnection.getConnection();

        // Προσομοίωση της αποτυχίας κατά την εκτέλεση του query
        // Αν υπάρχει πρόβλημα στην προετοιμασία του statement, αναμένουμε SQL Exception
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT id FROM history WHERE user_id = ?");
            stmt.setString(1, "non_existing_user"); // Χρησιμοποιούμε μη έγκυρο user_id
            ResultSet resultSet = stmt.executeQuery();
            // Αν φτάσουμε εδώ, κάτι πήγε λάθος γιατί αναμέναμε SQLException
            assertEquals(true, false); // Εδώ ο έλεγχος αποτυγχάνει σκόπιμα για να δείξουμε το σφάλμα.
        } catch (SQLException e) {
            // Ο έλεγχος είναι επιτυχής αν πετάξει SQLException
            assertEquals("Test SQL Exception", e.getMessage());
        } finally {
            connection.close();
        }
    }

    @Test
    void testGetSongIdFromHistory_WithValidUserId() throws Exception {
        // Δημιουργία της βάσης δεδομένων και σύνδεση με in-memory DB (π.χ., H2)
        DataBaseConnection dbConnection = new DataBaseConnection();

        // Ρύθμιση της σύνδεσης στην in-memory βάση
        Connection connection = dbConnection.getConnection();

        // Εκτέλεση ενός query για να δημιουργηθεί δεδομένα στο ιστορικό
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO history (user_id, id) VALUES (?, ?)");
        stmt.setString(1, "test_user");
        stmt.setInt(2, 123);
        stmt.executeUpdate();  // Εισάγουμε δεδομένα για το test

        // Τώρα, καλούμε την μέθοδο getSongIdFromHistory με έγκυρο user_id
        DashboardController dashboardController = new DashboardController();
        dashboardController.dbConnection = dbConnection;

        // Εκτέλεση της μεθόδου και επιβεβαίωση του αποτελέσματος
        int result = dashboardController.getSongIdFromHistory("test_user");

        // Αναμενόμενο αποτέλεσμα είναι το songId από την βάση
        assertEquals(123, result);

        connection.close();
    }

    @Test
    void testGetSongIdFromHistory_WithNoHistory() throws Exception {
        // Δημιουργία της βάσης δεδομένων και σύνδεση με in-memory DB (π.χ., H2)
        DataBaseConnection dbConnection = new DataBaseConnection();

        // Ρύθμιση της σύνδεσης στην in-memory βάση
        Connection connection = dbConnection.getConnection();

        // Εκτέλεση της μεθόδου getSongIdFromHistory με χρήστη που δεν υπάρχει στην ιστορία
        DashboardController dashboardController = new DashboardController();
        dashboardController.dbConnection = dbConnection;

        // Εκτέλεση της μεθόδου και επιβεβαίωση ότι το αποτέλεσμα είναι -1 (δηλαδή δεν υπάρχει ιστορικό)
        int result = dashboardController.getSongIdFromHistory("unknown_user");

        // Αναμενόμενο αποτέλεσμα είναι -1 όταν δεν υπάρχει ιστορικό
        assertEquals(-1, result);

        connection.close();
    }
}


