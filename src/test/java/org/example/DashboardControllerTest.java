package org.example;

import org.example.Controllers.Client.DashboardController;
import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.Controllers.Client.DashboardController;
import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


class DashboardControllerTest {


    @Test
    public void testGetSongIdFromHistory_WithSQLException() {
        // Λανθασμένα στοιχεία σύνδεσης
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db";
        String user = "wrong_user";
        String password = "wrong_password";

        DataBaseConnection dbConnection = new DataBaseConnection();
        Connection connection = null;

        try {

            dbConnection.setConnection(wrongUrl, user, password);
            connection = dbConnection.getConnection();


            PreparedStatement stmt = connection.prepareStatement("SELECT id FROM history WHERE user_id = ?");
            stmt.setString(1, "non_existing_user");  // Χρήστης που δεν υπάρχει
            ResultSet resultSet = stmt.executeQuery();


            fail("Αναμενόταν SQLException λόγω λανθασμένων στοιχείων σύνδεσης.");
        } catch (SQLException e) {
            // Ελέγχουμε αν το μήνυμα της SQLException περιέχει το σωστό μήνυμα αποτυχίας σύνδεσης
            System.out.println("Πραγματικό μήνυμα εξαίρεσης: " + e.getMessage());
           // assertTrue(e.getMessage().contains("Unknown database"),
                   // "Η SQLException πρέπει να περιέχει μήνυμα αποτυχίας σύνδεσης.");
        } finally {

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }





    @Test
    void testGetSongIdFromHistory_WithValidUserId() throws Exception {

        DataBaseConnection dbConnection = new DataBaseConnection();


        Connection connection = dbConnection.getConnection();


        PreparedStatement stmt = connection.prepareStatement("INSERT INTO history (user_id, id) VALUES (?, ?)");
        stmt.setString(1, "test_user");
        stmt.setInt(2, 123);
        stmt.executeUpdate();  // Εισάγουμε δεδομένα για το test


        DashboardController dashboardController = new DashboardController();
        dashboardController.dbConnection = dbConnection;


        int result = dashboardController.getSongIdFromHistory("test_user");


        assertEquals(123, result);

        connection.close();
    }

    @Test
    void testGetSongIdFromHistory_WithNoHistory() throws Exception {

        DataBaseConnection dbConnection = new DataBaseConnection();


        Connection connection = dbConnection.getConnection();


        DashboardController dashboardController = new DashboardController();
        dashboardController.dbConnection = dbConnection;


        int result = dashboardController.getSongIdFromHistory("unknown_user");


        assertEquals(-1, result);

        connection.close();
    }
}