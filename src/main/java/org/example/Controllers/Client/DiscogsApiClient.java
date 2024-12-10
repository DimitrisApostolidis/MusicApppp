package org.example.Controllers.Client;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class DiscogsApiClient {
    private static final String API_KEY = "szIiVuKbWPUHTuUWNKVo";
    private static final String SECRET_KEY = "vrjzQYjcjwMlhXcxAXyvubcdvwvKYoPg";
    private static final String BASE_URL = "https://api.discogs.com/";

    public JsonNode searchArtists(String artistName) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "database/search")
                    .queryString("q", artistName)
                    .queryString("type", "artist")
                    .queryString("key", API_KEY)
                    .queryString("secret", SECRET_KEY)
                    .asJson();

            return response.getStatus() == 200 ? response.getBody() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonNode searchTracks(String trackName) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "database/search")
                    .queryString("q", trackName)
                    .queryString("type", "release")
                    .queryString("key", API_KEY)
                    .queryString("secret", SECRET_KEY)
                    .asJson();

            return response.getStatus() == 200 ? response.getBody() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTrackGenre(String trackName) {
        try {
            JsonNode response = searchTracks(trackName);
            if (response != null && response.getObject().has("results")) {
                JSONArray results = response.getObject().getJSONArray("results");
                if (!results.isEmpty()) {
                    JSONObject firstResult = results.getJSONObject(0);
                    JSONArray genres = firstResult.optJSONArray("genre");
                    return genres != null && !genres.isEmpty() ? genres.getString(0) : "Unknown";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

}
