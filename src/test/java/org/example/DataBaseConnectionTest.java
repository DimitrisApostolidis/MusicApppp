package org.example;

import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

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
        // Δοκιμή με μη έγκυρα διαπιστευτήρια
        String username = "invalidUser";
        String password = "invalidPassword";
        assertFalse(db.verifyCredentials(username, password), "Τα διαπιστευτήρια δεν πρέπει να είναι έγκυρα.");
    }
}
