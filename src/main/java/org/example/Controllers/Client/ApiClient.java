package org.example.Controllers.Client;
import java.io.BufferedReader;
import org.example.Models.Album;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {
    public String getArtistData(String artistName) {
        String apiUrl = "https://www.theaudiodb.com/api/v1/json/2/album.php?i=112024" + artistName;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Album> getAlbumsByArtistId(String artistId) {
        List<Album> albums = new ArrayList<>();
        try {
            String apiUrl = "https://www.theaudiodb.com/api/v1/json/2/album.php?i=" + artistId;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();

                JSONObject jsonObj = new JSONObject(jsonResponse.toString());
                JSONArray albumsArray = jsonObj.getJSONArray("album");

                for (int i = 0; i < albumsArray.length(); i++) {
                    JSONObject albumObj = albumsArray.getJSONObject(i);
                    String albumName = albumObj.getString("strAlbum");
                    String year = albumObj.getString("intYearReleased");
                    albums.add(new Album(albumName, year));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return albums;
    }
}
