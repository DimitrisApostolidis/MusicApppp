package org.example.Controllers.Client;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class LastFmApiClient {
    private static final String API_KEY = "829ba83ee4aab86e1d130b2c514ef802";
    private static final String BASE_URL = "http://ws.audioscrobbler.com/2.0/";

    public JsonNode getArtistInfo(String artistName) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(BASE_URL)
                    .queryString("method", "artist.getinfo")
                    .queryString("artist", artistName)
                    .queryString("api_key", API_KEY)
                    .queryString("format", "json")
                    .asJson();

            // Έλεγχος για επιτυχία
            if (response.getStatus() == 200) {
                return response.getBody();
            } else {
                System.out.println("Error: " + response.getStatus());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonNode searchArtists(String artistName) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(BASE_URL)
                    .queryString("method", "artist.search")
                    .queryString("artist", artistName)
                    .queryString("api_key", API_KEY)
                    .queryString("format", "json")
                    .asJson();

            // Έλεγχος για επιτυχία
            if (response.getStatus() == 200) {
                return response.getBody();
            } else {
                System.out.println("Error: " + response.getStatus());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonNode searchTracks(String trackName) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(BASE_URL)
                    .queryString("method", "track.search")
                    .queryString("track", trackName)
                    .queryString("api_key", API_KEY)
                    .queryString("format", "json")
                    .asJson();

            if (response.getStatus() == 200) {
                return response.getBody();
            } else {
                System.out.println("Error: " + response.getStatus());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}