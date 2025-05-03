package org.example;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.User.UserDatabase;
import org.example.User.UserRecord;

import java.util.stream.Collectors;

public class ChangeUserPasswordWindow {
    public static void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Change User Password");
        window.setMinWidth(300);
        window.setMinHeight(200);

        UserDatabase db = UserDatabase.load();

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10));
        layout.setVgap(10);
        layout.setHgap(10);
        layout.setAlignment(Pos.CENTER);

        Label userLabel = new Label("Select User:");
        ComboBox<String> userComboBox = new ComboBox<>();
        userComboBox.setItems(FXCollections.observableArrayList(
                db.getAllUsers().stream()
                        .map(UserRecord::getName)
                        .collect(Collectors.toList())
        ));
        if (!userComboBox.getItems().isEmpty()) {
            userComboBox.setValue(userComboBox.getItems().get(0));
        }

        Label newPassLabel = new Label("New Password:");
        PasswordField newPassInput = new PasswordField();

        Button changeButton = new Button("Change Password");
        changeButton.setOnAction(e -> {
            String selectedUser = userComboBox.getValue();
            String newPassword = newPassInput.getText();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);

            if (selectedUser == null || selectedUser.isEmpty()) {
                alert.setContentText("Please select a user.");
            } else if (newPassword == null || newPassword.isEmpty()) {
                alert.setContentText("New password cannot be empty.");
            } else {
                UserRecord user = db.getUserData(selectedUser);
                if (user != null) {
                    user.setPassword(newPassword);
                    db.save();
                    alert.setContentText("Password for user '" + selectedUser + "' changed successfully.");
                    window.close();
                } else {
                    alert.setContentText("User not found.");
                }
            }
            alert.showAndWait();
        });

        layout.add(userLabel, 0, 0);
        layout.add(userComboBox, 1, 0);
        layout.add(newPassLabel, 0, 1);
        layout.add(newPassInput, 1, 1);
        layout.add(changeButton, 1, 2);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}