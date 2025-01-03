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

            if (response.getStatus() == 200 && response.getBody() != null) {
                JsonNode responseBody = response.getBody();
                JSONArray results = responseBody.getObject().optJSONArray("results");

                // Έλεγχος αν το πεδίο "results" είναι κενό
                if (results == null || results.length() == 0) {
                    return null;  // Δεν υπάρχουν αποτελέσματα
                }

                // Αν τα αποτελέσματα δεν σχετίζονται με τον καλλιτέχνη που ψάχνετε, επιστρέφετε null
                for (int i = 0; i < results.length(); i++) {
                    JSONObject artist = results.getJSONObject(i);
                    String title = artist.optString("title", "").toLowerCase();
                    if (title.contains(artistName.toLowerCase())) {
                        return responseBody;  // Αν βρεθεί ο καλλιτέχνης, επιστρέφουμε τα αποτελέσματα
                    }
                }

                // Αν δεν βρεθεί ο καλλιτέχνης, επιστρέφουμε null
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonNode searchTracks(String trackName) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "database/search")
                    .queryString("q", trackName)
                    .queryString("type", "release")
                    .queryString("key", API_KEY)
                    .queryString("secret", SECRET_KEY)
                    .asJson();

            if (response.getStatus() == 200 && response.getBody() != null) {
                JsonNode responseBody = response.getBody();
                JSONArray results = responseBody.getObject().optJSONArray("results");

                // Έλεγχος αν το πεδίο "results" είναι κενό
                if (results == null || results.length() == 0) {
                    return null;  // Δεν υπάρχουν αποτελέσματα
                }

                // Αν τα αποτελέσματα δεν σχετίζονται με το τραγούδι που ψάχνετε, επιστρέφετε null
                for (int i = 0; i < results.length(); i++) {
                    JSONObject track = results.getJSONObject(i);
                    String title = track.optString("title", "").toLowerCase();
                    if (title.contains(trackName.toLowerCase())) {
                        return responseBody;  // Αν βρεθεί το τραγούδι, επιστρέφουμε τα αποτελέσματα
                    }
                }

                // Αν δεν βρεθεί το τραγούδι, επιστρέφουμε null
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTrackGenre(String trackName) {
        try {
            JsonNode response = searchTracks(trackName);  // Κλήση της μεθόδου για αναζήτηση τραγουδιού

            // Ελέγχουμε αν η απάντηση είναι έγκυρη και αν περιέχει το πεδίο "results"
            if (response != null && response.getObject().has("results")) {
                JSONArray results = response.getObject().getJSONArray("results");

                // Ελέγχουμε αν η λίστα των αποτελεσμάτων δεν είναι κενή
                if (results.length() > 0) {
                    // Εξετάζουμε τα αποτελέσματα και ψάχνουμε για το genre
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject track = results.getJSONObject(i);
                        String title = track.optString("title", "").toLowerCase();

                        // Αν το τραγούδι περιέχει το όνομα που ψάχνουμε
                        if (title.contains(trackName.toLowerCase())) {
                            JSONArray genres = track.optJSONArray("genre");

                            // Ελέγχουμε αν το genre είναι διαθέσιμο και επιστρέφουμε το πρώτο genre
                            if (genres != null && genres.length() > 0) {
                                return genres.getString(0);  // Επιστρέφουμε το πρώτο genre
                            }
                            break;  // Αν το genre δεν υπάρχει, διακόπτουμε την αναζήτηση
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  // Εκτυπώνουμε το σφάλμα για debugging
        }

        // Επιστρέφουμε "Unknown" αν δεν βρεθεί genre ή σε περίπτωση σφάλματος
        return "Unknown";
    }




}