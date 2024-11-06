package org.example.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.Controllers.Client.ClientController;

public class ViewFactory {
    private AnchorPane dashboardView;
    private Stage loginStage;

    public ViewFactory(){}

    public AnchorPane getDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/Client/Dashboard.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dashboardView;
    }

    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/LoginClient.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginStage = new Stage();
        loginStage.setScene(scene);
        loginStage.initStyle(StageStyle.UNDECORATED);
        loginStage.show();
    }

    public void showClientWindow() {
        if (loginStage != null) {
            loginStage.close();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
        ClientController clientController = new ClientController();
        loader.setController(clientController);
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Rapsodia Player");
        stage.show();

    }


}