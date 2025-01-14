package org.example.Controllers.Admin;

public class AdminController {
}





















































//public void initialize(URL url, ResourceBundle resourceBundle) {
//    preloadClientScene();
//    login_btn.setOnAction(actionEvent -> handleLogin());
//    createAccount_btn.setOnAction(actionEvent -> handleCreateAccount());
//
//    // Έλεγχος αν υπάρχουν αποθηκευμένα στοιχεία και αυτόματη συμπλήρωση
//    String savedUsername = prefs.get("username", "");
//    String savedPassword = prefs.get("password", "");
//
//    if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
//        username_fld.setText(savedUsername);
//        password_fld.setText(savedPassword);
//        remember_me_chk.setSelected(true);  // Αν είναι αποθηκευμένο, επιλέγεται το checkbox
//    }
//}
//
//private void preloadClientScene() {
//    try {
//        if (clientScene == null) {
//            // Load the scene only once
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
//            Parent clientRoot = loader.load();
//
//            // Cache the scene
//            clientScene = new Scene(clientRoot);
//            clientScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
//        }
//    } catch (IOException e) {
//        logger.log(Level.SEVERE, "Failed to preload Client scene", e);
//    }
//}
//
//
//
//
//public void handleLogin() {
//    String username = username_fld.getText();
//    String password = password_fld.getText();
//    DataBaseConnection db = new DataBaseConnection();
//
//    // playlistController.clearLoggedInUserId();
//    // Remember me επιλογή
//    if (remember_me_chk.isSelected()) {
//        prefs.put("username", username);  // Αποθήκευση του username
//        prefs.put("password", password);  // Αποθήκευση του password
//    } else {
//        prefs.remove("username");
//        prefs.remove("password");
//        username_fld.clear();
//        password_fld.clear();
//    }
//
//    // Αν ο χρήστης είναι admin
//    if ("admin".equals(username) && "admin".equals(password)) {
//        // Καθαρισμός του προηγούμενου userId
//        String userId = "admin";
//        playlistController.saveLoggedInUserId(userId);
//        openClientScene(username);
//        error_lbl.setVisible(false);
//
//    } else {
//        // Έλεγχος διαπιστευτηρίων χρήστη
//
//        if (db.verifyCredentials(username, password)) {
//            userId = db.getUserId(username); // Λήψη του νέου userId
//            playlistController.saveLoggedInUserId(userId);
//            openClientScene(username);
//            error_lbl.setVisible(false);
//        } else {
//            error_lbl.setText("Invalid username or password");
//            error_lbl.setVisible(true);
//        }
//    }
//}
//
//
//
//private void openClientScene(String username) {
//    try {
//        // Φόρτωση του Client.fxml
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
//        Parent clientRoot = loader.load();
//
//        // Λήψη του controller και ρύθμιση των δεδομένων
//        ClientController clientController = loader.getController();
//        if (clientController != null) {
//            clientController.setUsername(username);
//            clientController.setWelcomeText(username);
//
//        }
//
//        // Δημιουργία σκηνής και εφαρμογή στυλ
//        Scene clientScene = new Scene(clientRoot);
//        clientScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
//
//        // Κλείσιμο του τρέχοντος παραθύρου
//        Stage currentStage = (Stage) username_fld.getScene().getWindow();
//        currentStage.close();
//
//        // Δημιουργία νέου παραθύρου και εφαρμογή εφέ
//        Stage newStage = new Stage();
//        newStage.initStyle(StageStyle.UNDECORATED); // Αφαίρεση default title bar
//        newStage.setScene(clientScene);
//
//        // Προσθήκη εφέ fade-in
//        newStage.setOpacity(0);
//        newStage.show();
//
//        Timeline timeline = new Timeline(
//                new KeyFrame(Duration.ZERO, new KeyValue(newStage.opacityProperty(), 0)),
//                new KeyFrame(Duration.seconds(0.3), new KeyValue(newStage.opacityProperty(), 1))
//        );
//        timeline.play(); // Έναρξη του animation
//
//
//
//
//    } catch (IOException e) {
//        logger.log(Level.SEVERE, "Failed to load Client scene", e);
//    }
//}
//
//// Handle creating a new account
//private void handleCreateAccount() {
//    try {
//        // Load the sign-up scene
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/SignUpClient.fxml"));
//        Parent clientRoot = loader.load();
//        SignUpController signUpController = loader.getController();
//
//        // Close the current window and open the sign-up window
//        Stage currentStage = (Stage) login_btn.getScene().getWindow();
//        Scene newScene = new Scene(clientRoot);
//        newScene.getStylesheets().add(getClass().getResource("/Styles/Background.css").toExternalForm());
//        currentStage.setScene(newScene);
//        currentStage.show();
//
//    } catch (IOException e) {
//        logger.log(Level.SEVERE, "Failed to load SignUp scene", e);
//    }
//}
//
//
//public String getUsernameFromDatabase(String userId) {
//    DataBaseConnection db = new DataBaseConnection();
//    String query = "SELECT username FROM user WHERE user_id = ?";
//
//    try {
//        return db.executeQuery(query, userId);
//    } catch (Exception e) {
//        e.printStackTrace();
//        return "Guest";
//    }
//
//}
//public void testableHandleCreateAccount() {
//    handleCreateAccount();
//}
//
//public void testableOpenClientScene(String username) {
//    openClientScene(username);
//}