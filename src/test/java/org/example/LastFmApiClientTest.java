package org.example;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

import kong.unirest.ObjectMapper;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.example.Controllers.Client.LastFmApiClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LastFmApiClientTest {
    private final LastFmApiClient lastFmApiClient = new LastFmApiClient();

    @Test
    public void testGetArtistInfo_Success() {
        String artistName = "Coldplay";
        JsonNode response = lastFmApiClient.getArtistInfo(artistName);

        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null");

        JSONObject body = response.getObject();
        assertTrue(body.has("artist"), "Η απάντηση πρέπει να περιέχει το πεδίο 'artist'");

        JSONObject artist = body.getJSONObject("artist");
        assertTrue(artist.has("bio"), "Το πεδίο 'artist' πρέπει να περιέχει 'bio'");

        JSONObject bio = artist.getJSONObject("bio");
        assertTrue(bio.has("content"), "Το πεδίο 'bio' πρέπει να περιέχει 'content'");

    }
    @Test
    public void testGetArtistInfo_Failure() {
        String artistName = "NonExistentArtist123456";
        JsonNode response = lastFmApiClient.getArtistInfo(artistName);

        assertNull(response, "Η απάντηση πρέπει να είναι null για ανύπαρκτο καλλιτέχνη");
    }


        @Test
    public void testSearchArtists_Success() {
        String artistName = "Coldplay";
        JsonNode response = lastFmApiClient.searchArtists(artistName);

        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null");

        JSONObject body = response.getObject();
        assertNotNull(body, "Το σώμα της απάντησης δεν πρέπει να είναι null");
        assertTrue(body.has("results"), "Η απάντηση πρέπει να περιέχει το πεδίο 'results'");

        JSONObject results = body.getJSONObject("results");
        assertTrue(results.has("artistmatches"), "Η απάντηση πρέπει να περιέχει το πεδίο 'artistmatches'");

        JSONObject artistMatches = results.getJSONObject("artistmatches");
        assertTrue(artistMatches.has("artist"), "Η απάντηση πρέπει να περιέχει το πεδίο 'artist'");

        assertFalse(artistMatches.getJSONArray("artist").isEmpty(), "Η λίστα των αποτελεσμάτων δεν πρέπει να είναι κενή");
    }

    @Test
    public void testSearchArtists_Failure() {
        String artistName = "NonExistentArtist123456";
        JsonNode response = lastFmApiClient.searchArtists(artistName);
        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null ακόμα και σε αποτυχία");

        JSONObject body = response.getObject();
        assertNotNull(body, "Το σώμα της απάντησης δεν πρέπει να είναι null");

        assertTrue(body.has("results"), "Η απάντηση πρέπει να περιέχει το πεδίο 'results'");

        JSONObject results = body.getJSONObject("results");
        assertTrue(results.has("artistmatches"), "Η απάντηση πρέπει να περιέχει το πεδίο 'artistmatches'");

        JSONObject artistMatches = results.getJSONObject("artistmatches");

        assertTrue(
                artistMatches.isNull("artist") || artistMatches.getJSONArray("artist").isEmpty(),
                "Η λίστα των αποτελεσμάτων πρέπει να είναι κενή για μη υπαρκτό καλλιτέχνη"
        );
    }

    @Test
    public void testSearchTracks_Success() {
        String trackName = "Fix You";  // Ένα όνομα τραγουδιού που πιθανόν να υπάρχει
        JsonNode response = lastFmApiClient.searchTracks(trackName);
        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null");

        JSONObject body = response.getObject();
        assertNotNull(body, "Το σώμα της απάντησης δεν πρέπει να είναι null");
        assertTrue(body.has("results"), "Η απάντηση πρέπει να περιέχει το πεδίο 'results'");

        JSONObject results = body.getJSONObject("results");
        assertTrue(results.has("trackmatches"), "Η απάντηση πρέπει να περιέχει το πεδίο 'trackmatches'");

        JSONObject trackMatches = results.getJSONObject("trackmatches");
        assertFalse(trackMatches.isNull("track") || trackMatches.getJSONArray("track").isEmpty(),
                "Η λίστα των αποτελεσμάτων δεν πρέπει να είναι κενή");
    }
    @Test
    public void testSearchTracks_Failure() {
        String trackName = "NonExistentTrack123456";
        JsonNode response = lastFmApiClient.searchTracks(trackName);
        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null ακόμα και σε αποτυχία");

        JSONObject body = response.getObject();
        assertNotNull(body, "Το σώμα της απάντησης δεν πρέπει να είναι null");

        assertTrue(body.has("results"), "Η απάντηση πρέπει να περιέχει το πεδίο 'results'");

        JSONObject results = body.getJSONObject("results");
        assertTrue(results.has("trackmatches"), "Η απάντηση πρέπει να περιέχει το πεδίο 'trackmatches'");

        JSONObject trackMatches = results.getJSONObject("trackmatches");

        assertTrue(trackMatches.isNull("track") || trackMatches.getJSONArray("track").isEmpty(),
                "Η λίστα των αποτελεσμάτων πρέπει να είναι κενή για μη υπαρκτό τραγούδι");
    }
    @Test
    public void testSearchAlbums_Success() {
        String albumName = "A Rush of Blood to the Head";  // Ένα όνομα άλμπουμ που πιθανόν να υπάρχει
        JsonNode response = lastFmApiClient.searchAlbums(albumName);
        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null");

        JSONObject body = response.getObject();
        assertNotNull(body, "Το σώμα της απάντησης δεν πρέπει να είναι null");
                assertTrue(body.has("results"), "Η απάντηση πρέπει να περιέχει το πεδίο 'results'");

        JSONObject results = body.getJSONObject("results");
        assertTrue(results.has("albummatches"), "Η απάντηση πρέπει να περιέχει το πεδίο 'albummatches'");
        JSONObject albumMatches = results.getJSONObject("albummatches");
        assertFalse(albumMatches.isNull("album") || albumMatches.getJSONArray("album").isEmpty(),"Η λίστα των αποτελεσμάτων δεν πρέπει να είναι κενή");
    }

    @Test
    public void testSearchAlbums_Failure() {
        String albumName = "NonExistentAlbum123456";
        JsonNode response = lastFmApiClient.searchAlbums(albumName);

        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null ακόμα και σε αποτυχία");

        JSONObject body = response.getObject();
        assertNotNull(body, "Το σώμα της απάντησης δεν πρέπει να είναι null");

        assertTrue(body.has("results"), "Η απάντηση πρέπει να περιέχει το πεδίο 'results'");

        JSONObject results = body.getJSONObject("results");
        assertTrue(results.has("albummatches"), "Η απάντηση πρέπει να περιέχει το πεδίο 'albummatches'");

        JSONObject albumMatches = results.getJSONObject("albummatches");

        assertTrue(albumMatches.isNull("album") || albumMatches.getJSONArray("album").isEmpty(),
                "Η λίστα των αποτελεσμάτων πρέπει να είναι κενή για μη υπαρκτό άλμπουμ");
    }

    @Test
    public void testGetTopTracks_Success() {
        JsonNode response = lastFmApiClient.getTopTracks();
        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null");
        JSONObject body = response.getObject();
        assertNotNull(body, "Το σώμα της απάντησης δεν πρέπει να είναι null");
        assertTrue(body.has("tracks"), "Η απάντηση πρέπει να περιέχει το πεδίο 'tracks'");
        JSONObject tracks = body.getJSONObject("tracks");
        assertTrue(tracks.has("track"), "Η απάντηση πρέπει να περιέχει το πεδίο 'track'");
        JSONArray trackMatches = tracks.getJSONArray("track");
        assertFalse(trackMatches.isEmpty(), "Η λίστα των αποτελεσμάτων δεν πρέπει να είναι κενή");
    }

    @Test
    public void testGetTopTracks_Failure() {
        JsonNode response = lastFmApiClient.getTopTracks();
        assertNotNull(response, "Η απάντηση δεν πρέπει να είναι null");
        JSONObject body = response.getObject();
        assertNotNull(body, "Το σώμα της απάντησης δεν πρέπει να είναι null");
        assertTrue(body.has("tracks"), "Η απάντηση πρέπει να περιέχει το πεδίο 'tracks'");
        JSONObject tracks = body.getJSONObject("tracks");
        assertTrue(tracks.has("track"), "Η απάντηση πρέπει να περιέχει το πεδίο 'track'");
        JSONArray trackMatches = tracks.getJSONArray("track");
        assertEquals(50, trackMatches.length(), "Η λίστα των αποτελεσμάτων πρέπει να είναι κενή");
    }
}
