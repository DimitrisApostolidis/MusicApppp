package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import org.example.Models.Model;
import org.example.Views.ViewFactory;

public class MainApp extends Application{

    @Override
    public void start(Stage stage) {
        Model.getInstance().getViewFactory().showLoginWindow();
        //ss
    }
}
