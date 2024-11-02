package org.example.Controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.Models.Model;
import org.example.DataBaseConnection; // Βεβαιωθείτε ότι αυτή η κλάση υπάρχει

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Label username_lbl;
    public TextField username_fld;
    public TextField password_fld;
    public Button login_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(actionEvent -> handleLogin());
    }

    private void handleLogin() {
        String username = username_fld.getText();
        String password = password_fld.getText();

        DataBaseConnection db = new DataBaseConnection();
        if (db.verifyCredentials(username, password)) {
            Model.getInstance().getViewFactory().showClientWindow();
        } else {
            error_lbl.setText("Invalid username or password");
        }
    }
}
