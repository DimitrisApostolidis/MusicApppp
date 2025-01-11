package org.example;

import kong.unirest.JsonNode;
import org.example.Controllers.Client.JamendoApiClient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JamendoApiClientTest {

    @Test
    public void testSearchTracks() {
        JamendoApiClient apiClient = new JamendoApiClient();
        JsonNode result = apiClient.searchTracks("beatles");

        if (result != null) {
            System.out.println("Απόκριση: " + result.toString());  // Εκτυπώστε την απόκριση για να δείτε τη δομή
        }
        assertNotNull(result, "Η απάντηση δεν πρέπει να είναι null");

        assertTrue(result.getObject().has("results"), "Η απάντηση πρέπει να περιέχει πεδίο 'tracks'");
        assertTrue(result.getObject().getJSONArray("results").length() > 0, "Πρέπει να υπάρχουν τραγούδια στη λίστα");
    }

}