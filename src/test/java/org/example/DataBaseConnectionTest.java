package org.example;

import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataBaseConnectionTest {

    private static DataBaseConnection db;

    @BeforeAll
    static void setUp() {
        db = new DataBaseConnection();
    }

    @Test
    void testVerifyCredentials_Success() {
        // Δοκιμή με έγκυρα διαπιστευτήρια που υπάρχουν στη βάση δεδομένων
        String username = "validUser"; // Ελέγχει ότι ο χρήστης υπάρχει στη βάση
        String password = "validPassword"; // Αντίστοιχος κωδικός
        assertTrue(db.verifyCredentials(username, password), "Τα διαπιστευτήρια πρέπει να είναι έγκυρα.");
    }

    @Test
    void testVerifyCredentials_Failure() {
         //Δοκιμή με μη έγκυρα διαπιστευτήρια
        String username = "invalidUser";
        String password = "invalidPassword";
       assertFalse(db.verifyCredentials(username, password), "Τα διαπιστευτήρια δεν πρέπει να είναι έγκυρα.");
   }

    @Test
    public void testGetConnection_Success() {
        // Κλήση της μεθόδου getConnection για να πάρουμε μια σύνδεση
        Connection connection = db.getConnection();

        // Ελέγξτε αν η σύνδεση είναι έγκυρη
        assertNotNull(connection, "Η σύνδεση δεν είναι έγκυρη!");

        // Προσπαθούμε να εκτελέσουμε μία απλή ερώτηση για να ελέγξουμε την σύνδεση
        try (Statement stmt = connection.createStatement()) {
            assertNotNull(stmt, "Η δήλωση δεν δημιουργήθηκε σωστά!");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την εκτέλεση δήλωσης SQL");
        }
    }

    @Test
    public void testSetConnection_Success() {
        // Σωστά δεδομένα σύνδεσης (προσαρμόστε τα με την πραγματική σας βάση δεδομένων)
        String correctUrl = "jdbc:mysql://localhost:3306/rapsodiaplayer"; // Σωστό URL
        String user = "root"; // Σωστός χρήστης
        String password = "" ;// Σωστός κωδικός

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            // Προσπαθούμε να κάνουμε σύνδεση με τα σωστά δεδομένα
            dbConnection.setConnection(correctUrl, user, password);
            // Αν φτάσουμε εδώ, η σύνδεση ήταν επιτυχής
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Αναμενόταν επιτυχία σύνδεσης, αλλά προέκυψε σφάλμα: " + e.getMessage());
        }
    }

    @Test
    public void testSetConnection_Failure() {
        // Λανθασμένα δεδομένα σύνδεσης
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db"; // Λάθος URL
        String user = "root"; // Λάθος χρήστης
        String password = "wrongpassword"; // Λάθος κωδικός

        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            // Προσπαθούμε να κάνουμε σύνδεση με τα λανθασμένα δεδομένα
            dbConnection.setConnection(wrongUrl, user, password);
            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            // Αν προκύψει SQLException, το τεστ περνάει
            e.printStackTrace();
        }
    }

    @Test
    public void testAddPlaylist_Success() {
        // Κλήση της μεθόδου addPlaylist
        boolean result = db.addPlaylist("New Playlist", "23");

        // Ελέγξτε αν η μέθοδος επιστρέφει true (που σημαίνει ότι η playlist προστέθηκε επιτυχώς)
        assertTrue(result, "Η playlist δεν προστέθηκε σωστά στη βάση δεδομένων");

        // Ελέγξτε αν η playlist έχει προστεθεί στη βάση δεδομένων
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM playlist WHERE name = 'New Playlist'");

            // Αν υπάρχει εγγραφή, το αποτέλεσμα είναι true
            assertTrue(rs.next(), "Η playlist δεν βρέθηκε στη βάση δεδομένων");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Σφάλμα κατά την ανάκτηση της playlist από τη βάση δεδομένων");
        }
    }


    @Test
    public void testAddPlaylist_Failure() {
        // Δοκιμή για αποτυχία, όταν παραλείπεται το όνομα της playlist
        boolean result = db.addPlaylist("", "23");

        // Ελέγξτε αν η μέθοδος επιστρέφει false όταν υπάρχει σφάλμα
        assertFalse(result, "Η μέθοδος πρέπει να επιστρέψει false όταν η playlist δεν προστίθεται");

        // Δοκιμή με μη έγκυρο userId
        result = db.addPlaylist("New Playlist", "");
        assertFalse(result, "Η μέθοδος πρέπει να επιστρέψει false όταν το user_id είναι κενό");
    }

    @Test
    public void testAddPlaylist_ExceptionHandling() {
        // Δοκιμάζουμε λάθος δεδομένα σύνδεσης για να προκαλέσουμε SQLException
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db"; // Λάθος URL
        String user = "root"; // Λάθος χρήστης
        String password = "wrongpassword"; // Λάθος κωδικός

        // Δημιουργούμε το αντικείμενο της κλάσης DataBaseConnection
        DataBaseConnection dbConnection = new DataBaseConnection();

        try {
            // Ρυθμίζουμε τη σύνδεση με λάθος δεδομένα
            dbConnection.setConnection(wrongUrl, user, password);
            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            // Αν προκύψει SQLException, θα καταγραφεί και το τεστ θα περάσει
            e.printStackTrace();
            // Η σύνδεση απέτυχε, άρα το τεστ είναι επιτυχές
        }
    }









}
