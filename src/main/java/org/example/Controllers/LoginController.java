package org.example.Controllers;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.Models.Model;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    public Label username_lbl;
    public TextField username_fld;
    public TextField password_fld;
    public Button login_btn;
    public Label error_lbl;
    public Button signup_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(actionEvent -> Model.getInstance().getViewFactory().showClientWindow());
        signup_btn.setOnAction(actionEvent -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("src/main/resources/SignUp.fxml")); // Σιγουρέψου για τη σωστή διαδρομή
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) signup_btn.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: Could not load SignUp.fxml");
            }
        });
    }
}