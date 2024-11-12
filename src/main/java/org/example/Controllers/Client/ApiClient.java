package org.example.Controllers.Client;

import org.example.Models.Album;
import org.example.Models.Artist;
import org.example.Models.Track;
import org.example.DataBase.DataBaseConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    private String makeApiRequest(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) throw new RuntimeException("HttpResponseCode: " + responseCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);
        reader.close();

        return response.toString();
    }

    public Artist getArtistInfo(String artistName) {
        try {
            String apiUrl = "https://www.theaudiodb.com/api/v1/json/2/search.php?s=" + artistName;
            String jsonResponse = makeApiRequest(apiUrl);

            JSONObject jsonObj = new JSONObject(jsonResponse);
            JSONArray artistsArray = jsonObj.getJSONArray("artists");

            if (artistsArray.length() > 0) {
                JSONObject artistObj = artistsArray.getJSONObject(0);
                String name = artistObj.getString("strArtist");
                String bio = artistObj.getString("strBiographyEN");

                return new Artist(name, bio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Album> getAlbumsByArtistId(String artistId) {
        List<Album> albums = new ArrayList<>();
        try {
            String apiUrl = "https://www.theaudiodb.com/api/v1/json/2/album.php?i=" + artistId;
            String jsonResponse = makeApiRequest(apiUrl);

            JSONObject jsonObj = new JSONObject(jsonResponse);
            JSONArray albumsArray = jsonObj.getJSONArray("album");

            for (int i = 0; i < albumsArray.length(); i++) {
                JSONObject albumObj = albumsArray.getJSONObject(i);
                String albumName = albumObj.getString("strAlbum");
                String year = albumObj.getString("intYearReleased");
                String coverUrl = albumObj.getString("strAlbumThumb");

                albums.add(new Album(albumName, year, coverUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return albums;
    }

    public List<Track> getTracksByAlbumId(String albumId) {
        List<Track> tracks = new ArrayList<>();
        try {
            String apiUrl = "https://www.theaudiodb.com/api/v1/json/2/track.php?m=" + albumId;
            String jsonResponse = makeApiRequest(apiUrl);

            JSONObject jsonObj = new JSONObject(jsonResponse);
            JSONArray tracksArray = jsonObj.getJSONArray("track");

            for (int i = 0; i < tracksArray.length(); i++) {
                JSONObject trackObj = tracksArray.getJSONObject(i);
                String trackName = trackObj.getString("strTrack");
                String artistName = trackObj.getString("strArtist");
                String albumName = trackObj.getString("strAlbum");
                Track track = new Track(trackName, artistName, albumName);
                tracks.add(track);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tracks;
    }

    public void fetchAndSaveTracksByAlbumId(String albumId) {
        DataBaseConnection dbConnection = new DataBaseConnection();
        List<Track> tracks = getTracksByAlbumId(albumId);
        for (Track track : tracks) {
            dbConnection.saveTrackToDatabase(track.getName(), track.getArtist(), track.getAlbum());
        }
    }
}


