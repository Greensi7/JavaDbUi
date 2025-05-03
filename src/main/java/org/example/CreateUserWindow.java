package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.User.UserDatabase;
import org.example.User.UserType;


public class CreateUserWindow {
    public static void display() {
        UserDatabase db = UserDatabase.load();
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Create New User");
        window.setMinWidth(300);
        window.setMinHeight(200);
        Label nameLabel = new Label("Username:");
        TextField nameInput = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passInput = new PasswordField();

        ComboBox<UserType> userT = new ComboBox<>();
        userT.getItems().addAll(UserType.values());
        userT.setValue(UserType.VIEWER);


        Button createButton = new Button("Create");
        createButton.setOnAction(_ -> {
            String username = nameInput.getText();
            String password = passInput.getText();
            UserType type = userT.getValue();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);

            if (!username.isEmpty() && !password.isEmpty()) {
                if (db.exists(username)) {
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setContentText("User already exists!");
                } else {
                    db.addUser(username, password, type);
                    alert.setContentText("User created: " + username);
                    alert.showAndWait();
                    window.close();
                    return;
                }
            } else {
                alert.setAlertType(AlertType.INFORMATION);
                alert.setContentText("Username and password cannot be empty.");
            }

            alert.showAndWait();
        });

        GridPane layout = new GridPane();
        layout.add(userT, 0, 2);
        layout.setPadding(new Insets(10));
        layout.setVgap(10);
        layout.setHgap(10);
        layout.setAlignment(Pos.CENTER);

        layout.add(nameLabel, 0, 0);
        layout.add(nameInput, 1, 0);
        layout.add(passLabel, 0, 1);
        layout.add(passInput, 1, 1);
        layout.add(createButton, 1, 2);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
