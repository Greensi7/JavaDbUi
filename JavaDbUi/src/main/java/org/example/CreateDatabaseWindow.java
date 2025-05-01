package org.example;

import cz.fi.muni.pb162.sqlike.data.database.StandardDatabase;
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
import org.example.Data.DatabaseList;
import org.example.User.UserDatabase;


public class CreateDatabaseWindow {
    public static void display() {
        DatabaseList dab = DatabaseList.load();
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); // blocks interaction with other windows
        window.setTitle("Create Window");
        window.setMinWidth(300);
        window.setMinHeight(200);
        Label nameLabel = new Label("Database Name:");
        TextField nameInput = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String databaseName = nameInput.getText();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);

            if (!databaseName.isEmpty()) {
                if (dab.containsDatabase(databaseName)) {
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setContentText("Can not create database because it already exists.");
                } else {
                    dab.addDatabase(databaseName, new StandardDatabase(databaseName));
                    alert.setAlertType(AlertType.INFORMATION);
                    alert.setContentText("Database created: " + databaseName);
                    alert.showAndWait();
                    window.close();
                    return;
                }
            } else {
                alert.setAlertType(AlertType.INFORMATION);
                alert.setContentText("Can not create nameless database.");
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
