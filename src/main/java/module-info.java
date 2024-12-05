module org.example {
    requires javafx.controls;  // Required for JavaFX controls
    requires javafx.fxml;     // Required for JavaFX FXML (if you're using it)
    requires de.jensd.fx.glyphs.fontawesome;
    requires de.jensd.fx.glyphs.commons;
    requires java.desktop;
    requires java.sql;
    requires org.json;
    requires unirest.java;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.prefs;
    opens org.example.DataBase to org.mockito;

    opens org.example.Controllers to javafx.fxml;

    opens org.example.Controllers.Client to javafx.fxml;
    opens org.example to javafx.fxml; // Open your package to FXMLLoader (if using FXML)
    exports org.example; // Exports your package
    exports org.example.Controllers;
    exports org.example.Controllers.Admin;
    exports org.example.Controllers.Client;
    exports org.example.Models;
    exports org.example.Views;
    opens org.example.Models to javafx.fxml;
}