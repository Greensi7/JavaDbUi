package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.User.UserDatabase;
import org.example.User.UserRecord;
import org.example.User.UserType;

import java.util.Collection;

public class UserListWindow {

    public static void display() {
        // Load the user database
        UserDatabase db = UserDatabase.load();
        Collection<UserRecord> users = db.getAllUsers();

        // Create a new window (Stage)
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); // blocks interaction with other windows
        window.setTitle("User List");
        window.setMinWidth(300);
        window.setMinHeight(400);

        // Set up the ListView to display user names and privileges
        ListView<String> userList = new ListView<>();
        for (UserRecord user : users) {
            String userInfo = "Username: " + user.getName() + " | Privilege: " + user.getUserType();
            userList.getItems().add(userInfo);
        }

        // Label at the top
        Label label = new Label("All Users and Their Privileges");

        // Create the Close Button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());

        // Layout setup with GridPane
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10));
        layout.setVgap(10);
        layout.setHgap(10);
        layout.setAlignment(Pos.CENTER);

        layout.add(label, 0, 0);
        layout.add(userList, 0, 1);
        layout.add(closeButton, 0, 2);

        // Scene setup
        Scene scene = new Scene(layout);
        window.setScene(scene);

        // Show the window
        window.showAndWait();
    }
}
