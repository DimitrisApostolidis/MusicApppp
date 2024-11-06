package org.example.Controllers.Client;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import org.example.Models.Album;
import java.util.List;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.example.DataBase.DataParser;

public class DashboardController {



    @FXML
    private ImageView artist1;
    @FXML
    private ImageView artist2;
    @FXML
    private ImageView artist3;
    @FXML
    private ImageView artist4;
    @FXML
    private ImageView artist5;
    @FXML
    private ImageView artist6;
    @FXML
    private ImageView artist7;
    @FXML
    private ImageView artist8;
    @FXML
    private ImageView artist9;
    @FXML
    private ImageView artist10;







    @FXML
    private ListView<String> albumListView;
    @FXML
    public void initialize() {
        ApiClient apiClient = new ApiClient();
        String response = apiClient.getArtistData("artistName");
        List<Album> albums = apiClient.getAlbumsByArtistId("112024"); // ID καλλιτέχνη

        if (response != null) {
            DataParser dataParser = new DataParser();
            dataParser.parseArtistData(response);
            for (Album album : albums) {
                // albumListView.getItems().add(album.getName() + " (" + album.getYear() + ")");
            }// Κλήση της μεθόδου ανάλυσης JSON
            // Εδώ μπορείς να προσθέσεις λογική για να εμφανίσεις τα δεδομένα στο UI
        }
    }

    public void displayArtistImages(String[] imageUrls) {
        ImageView[] imageViews = {artist1, artist2, artist3, artist4, artist5, artist6, artist7, artist8, artist9, artist10};
        for (int i = 0; i < imageUrls.length && i < imageViews.length; i++) {
            imageViews[i].setImage(new Image(imageUrls[i]));
        }
    }
}




