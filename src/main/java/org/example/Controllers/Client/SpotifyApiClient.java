package org.example.Controllers.Client;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

public class SpotifyApiClient {

    private final String clientId;
    private final String clientSecret;
    private String accessToken;

    public SpotifyApiClient(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        authenticate();
    }

    private void authenticate() {
        HttpResponse<JsonNode> response = Unirest.post("https://accounts.spotify.com/api/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .basicAuth(clientId, clientSecret)
                .field("grant_type", "client_credentials")
                .asJson();

        if (response.getStatus() == 200) {
            accessToken = response.getBody().getObject().getString("access_token");
        } else {
            throw new RuntimeException("Failed to authenticate with Spotify API");
        }
    }

    public JSONArray searchTracks(String query) {
        HttpResponse<JsonNode> response = Unirest.get("https://api.spotify.com/v1/search")
                .header("Authorization", "Bearer " + accessToken)
                .queryString("q", query)
                .queryString("type", "track")
                .queryString("limit", 10) // Περιορισμός αποτελεσμάτων για απλοποίηση
                .asJson();

        if (response.getStatus() == 200) {
            return response.getBody().getObject()
                    .getJSONObject("tracks")
                    .getJSONArray("items");
        } else {
            throw new RuntimeException("Failed to search tracks: " + response.getStatusText());
        }
    }

}
