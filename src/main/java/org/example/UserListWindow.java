package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.User.UserDatabase;
import org.example.User.UserRecord;

import java.util.Collection;

public class UserListWindow {

    public static void display() {
        UserDatabase db = UserDatabase.load();
        Collection<UserRecord> users = db.getAllUsers();

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); // blocks interaction with other windows
        window.setTitle("User List");
        window.setMinWidth(300);
        window.setMinHeight(400);

        ListView<String> userList = new ListView<>();
        for (UserRecord user : users) {
            String userInfo = "Username: " + user.getName() + " | Privilege: " + user.getUserType();
            userList.getItems().add(userInfo);
        }

        Label label = new Label("All Users and Their Privileges");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(_ -> window.close());

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10));
        layout.setVgap(10);
        layout.setHgap(10);
        layout.setAlignment(Pos.CENTER);

        layout.add(label, 0, 0);
        layout.add(userList, 0, 1);
        layout.add(closeButton, 0, 2);

        Scene scene = new Scene(layout);
        window.setScene(scene);

        window.showAndWait();
    }
}
