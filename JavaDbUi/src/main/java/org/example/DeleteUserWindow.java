package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.User.UserDatabase;


public class DeleteUserWindow {
    public static void display() {
        UserDatabase db = UserDatabase.load();
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); // blocks interaction with other windows
        window.setTitle("Delete User");
        window.setMinWidth(300);
        window.setMinHeight(200);
        Label nameLabel = new Label("Username:");
        TextField nameInput = new TextField();

        Button createButton = new Button("Delete");
        createButton.setOnAction(e -> {
            String username = nameInput.getText();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);

            if (!username.isEmpty()) {
                if (!db.exists(username)) {
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setContentText("Username does not exist.");
                } else {
                    db.removeUser(username);
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setContentText("User deleted: " + username);
                    alert.showAndWait();
                    window.close();
                    return;
                }
            } else {
                alert.setAlertType(AlertType.INFORMATION);
                alert.setContentText("Username does not exist.");
            }

            alert.showAndWait();
        });

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10));
        layout.setVgap(10);
        layout.setHgap(10);
        layout.setAlignment(Pos.CENTER);

        layout.add(nameLabel, 0, 0);
        layout.add(nameInput, 1, 0);
        layout.add(createButton, 1, 2);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
