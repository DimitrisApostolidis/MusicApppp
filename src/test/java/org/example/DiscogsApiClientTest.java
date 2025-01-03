package org.example;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.example.Controllers.Client.DiscogsApiClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class DiscogsApiClientTest {

    private DiscogsApiClient discogsApiClient = new DiscogsApiClient();

    @Test
    public void testSearchArtists_Success() {
        JsonNode response = discogsApiClient.searchArtists("V. Garlic");
        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null");
        assertTrue(response.getObject().has("results"), "Η απάντηση πρέπει να περιέχει 'results'");
        assertFalse(response.getObject().getJSONArray("results").isEmpty(), "Η λίστα των αποτελεσμάτων δεν πρέπει να είναι κενή");
        String artistTitle = response.getObject().getJSONArray("results").getJSONObject(0).getString("title");
        assertEquals("V. Garlic", artistTitle, "Ο τίτλος του πρώτου καλλιτέχνη πρέπει να είναι 'V. Garlic'");
    }
    @Test
    public void testSearchArtists_Failure() {
        JsonNode response = discogsApiClient.searchArtists("nonexistent artist");
        assertNull(response, "Η απάντηση πρέπει να είναι null για έναν μη υπάρχοντα καλλιτέχνη");
    }
    @Test
    public void testSearchTracks_Success() {
        String trackName = "Imagine";
        JsonNode response = discogsApiClient.searchTracks(trackName);
        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null για ένα υπάρχον τραγούδι");
        assertTrue(response.getObject().getJSONArray("results").length() > 0, "Η λίστα των αποτελεσμάτων πρέπει να έχει τουλάχιστον 1 στοιχείο");
    }
    @Test
    public void testSearchTracks_Failure() {
        String trackName = "Nonexistent Track";
        JsonNode response = discogsApiClient.searchTracks(trackName);
        assertNull(response, "Η απάντηση πρέπει να είναι null για ένα μη υπάρχον τραγούδι");
    }
    @Test
    public void testGetTrackGenre_Success() {
        String trackName = "Imagine";

        JsonNode response = discogsApiClient.searchTracks(trackName);
        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null");
        assertTrue(response.getObject().has("results"), "Η απάντηση πρέπει να περιέχει αποτελέσματα");

        JSONArray results = response.getObject().getJSONArray("results");
        assertTrue(results.length() > 0, "Η λίστα των αποτελεσμάτων πρέπει να έχει τουλάχιστον 1 στοιχείο");

        JSONObject firstResult = results.getJSONObject(0);
        JSONArray genres = firstResult.optJSONArray("genre");
        assertNotNull(genres, "Το πεδίο 'genre' δεν πρέπει να είναι null");
        assertTrue(genres.length() > 0, "Το πεδίο 'genre' πρέπει να έχει τουλάχιστον 1 στοιχείο");

        String genre = genres.getString(0);
        assertEquals("Rock", genre, "Το genre πρέπει να είναι 'Rock'");  // Προσαρμόστε το genre αν χρειάζεται
    }
    @Test
    public void testGetTrackGenre_NoGenre() {
        String trackName = "Imagine";
        JSONObject result = new JSONObject();
        result.put("genre", new JSONArray());  // Κενό array για το genre
        JSONArray results = new JSONArray().put(result);
        JSONObject responseBody = new JSONObject().put("results", results);
        JsonNode mockResponse = new JsonNode(responseBody.toString());
        JsonNode response = discogsApiClient.searchTracks(trackName);
        String genre = discogsApiClient.getTrackGenre(trackName);
        assertNotNull(genre, "Το genre δεν πρέπει να είναι null");
    }






}