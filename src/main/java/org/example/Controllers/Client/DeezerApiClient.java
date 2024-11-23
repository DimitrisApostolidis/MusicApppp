package org.example.Controllers.Client;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class DeezerApiClient {

    private static final String DEEZER_API_URL = "https://api.deezer.com/artist/";

    public static JsonNode searchArtist(String artistName) {
        HttpResponse<JsonNode> response = Unirest.get(DEEZER_API_URL + "search")
                .queryString("q", artistName)
                .queryString("limit", "1")
                .asJson();

        return response.getBody();
    }

    public static JsonNode searchAlbum(String albumName) {
        HttpResponse<JsonNode> response = Unirest.get(DEEZER_API_URL + "search")
                .queryString("q", albumName)
                .queryString("limit", "1")
                .asJson();

        return response.getBody();
    }
}
