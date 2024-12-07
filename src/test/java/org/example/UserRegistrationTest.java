package org.example;

import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserRegistrationTest {

    private static DataBaseConnection db;
    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @InjectMocks
    private DataBaseConnection dbConnection;

    @BeforeEach
     void setUp() {
        db = new DataBaseConnection(); // Δημιουργία αντικειμένου DataBaseConnection
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Δοκιμή με νέα δεδομένα που δεν υπάρχουν στη βάση
        String username = "newUser";
        String email = "newuser@example.com";
        String password = "securePassword123";

        assertTrue(db.registerUser(username, email, password),
                "Η εγγραφή πρέπει να είναι επιτυχής για νέο χρήστη.");
    }

    @Test
    void testRegisterUser_Failure_DuplicateUsername() {
        // Δοκιμή με όνομα χρήστη που υπάρχει ήδη στη βάση
        String username = "existingUser";
        String email = "uniqueemail@example.com";
        String password = "securePassword123";

        assertFalse(db.registerUser(username, email, password),
                "Η εγγραφή πρέπει να αποτύχει λόγω υπάρχοντος ονόματος χρήστη.");
    }

    @Test
    void testRegisterUser_Failure_DuplicateEmail() {
        // Δοκιμή με email που υπάρχει ήδη στη βάση
        String username = "uniqueUsername";
        String email = "existingemail@example.com";
        String password = "securePassword123";

        assertFalse(db.registerUser(username, email, password),
                "Η εγγραφή πρέπει να αποτύχει λόγω υπάρχοντος email.");
    }

    @Test
    void testRegisterUser_Failure_InvalidInput() {
        // Δοκιμή με άκυρα δεδομένα (π.χ. κενά πεδία)
        String username = "";
        String email = "";
        String password = "";

        assertFalse(db.registerUser(username, email, password),
                "Η εγγραφή πρέπει να αποτύχει λόγω άκυρων δεδομένων.");
    }


}
