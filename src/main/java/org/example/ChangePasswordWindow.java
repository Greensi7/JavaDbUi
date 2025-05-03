package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.User.UserDatabase;
import org.example.User.UserRecord;

public class ChangePasswordWindow {
    public static void display(UserRecord currentUser) {
        UserDatabase db = UserDatabase.load();
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Change Your Password");
        window.setMinWidth(300);
        window.setMinHeight(200);

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10));
        layout.setVgap(10);
        layout.setHgap(10);
        layout.setAlignment(Pos.CENTER);

        Label currentPassLabel = new Label("Current Password:");
        PasswordField currentPassInput = new PasswordField();

        Label newPassLabel = new Label("New Password:");
        PasswordField newPassInput = new PasswordField();

        Label confirmPassLabel = new Label("Confirm Password:");
        PasswordField confirmPassInput = new PasswordField();

        Button changeButton = new Button("Change Password");
        changeButton.setOnAction(e -> {
            String currentPass = currentPassInput.getText();
            String newPass = newPassInput.getText();
            String confirmPass = confirmPassInput.getText();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);

            if (!currentUser.getPassword().equals(currentPass)) {
                alert.setContentText("Current password is incorrect.");
            } else if (!newPass.equals(confirmPass)) {
                alert.setContentText("New passwords do not match.");
            } else {
                UserRecord user = db.getUserData(currentUser.getName());
                user.setPassword(newPass);
                db.save();

                alert.setContentText("Password successfully changed.");
                window.close();
            }
            alert.showAndWait();
        });

        layout.add(currentPassLabel, 0, 0);
        layout.add(currentPassInput, 1, 0);
        layout.add(newPassLabel, 0, 1);
        layout.add(newPassInput, 1, 1);
        layout.add(confirmPassLabel, 0, 2);
        layout.add(confirmPassInput, 1, 2);
        layout.add(changeButton, 1, 3);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}