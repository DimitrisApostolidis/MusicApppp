package org.example.Controllers.Client;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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


            if (response.getStatus() == 200) {
                JsonNode body = response.getBody();
                if (body.getObject().has("artist")) {
                    JSONObject artist = body.getObject().getJSONObject("artist");
                    if (artist.has("bio")) {
                        JSONObject bio = artist.getJSONObject("bio");
                        if (bio.has("content")) {
                            String bioContent = bio.getString("content");
                            System.out.println("Artist Bio: " + bioContent);
                        } else {
                            System.out.println("No bio content found for " + artistName);
                        }
                    } else {
                        System.out.println("No bio found for " + artistName);
                    }

                    return body;
                } else {
                    System.out.println("Artist data not found for " + artistName);
                }
            } else {
                System.out.println("Failed to fetch artist info. HTTP Status: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred: Unable to fetch artist info.");
        }
        return null;
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

    public JsonNode searchAlbums(String albumName) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(BASE_URL)
                    .queryString("method", "album.search")
                    .queryString("album", albumName)
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
    public JsonNode getArtistImages(String artistName) {
        String apiUrl = "https://api.last.fm/artist/images?artist=" + URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        return Unirest.get(apiUrl)
                .header("Authorization", "Bearer 829ba83ee4aab86e1d130b2c514ef802")
                .asJson()
                .getBody();
    }


}