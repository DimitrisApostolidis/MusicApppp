package org.example.Controllers.Client;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class JamendoApiClient {
    private static final String BASE_URL = "https://api.jamendo.com/v3.0";
    private static final String CLIENT_ID = "1de92382"; // Αντικαταστήστε με το API Key σας.

    public JsonNode searchTracks(String query) {
        HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "/tracks")
                .queryString("client_id", CLIENT_ID)
                .queryString("format", "json")
                .queryString("limit", "10")
                .queryString("search", query)
                .asJson();

        if (response.getStatus() == 200) {
            return response.getBody();
        } else {
            System.err.println("Error: " + response.getStatusText());
            return null;
        }
    }
}
