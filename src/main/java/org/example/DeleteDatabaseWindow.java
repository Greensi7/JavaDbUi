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
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.Data.DatabaseList;

public class DeleteDatabaseWindow {
    public static void display() {
        DatabaseList dab = DatabaseList.load();

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Delete Database");
        window.setMinWidth(300);
        window.setMinHeight(200);

        Label nameLabel = new Label("Select Database:");
        ComboBox<String> databaseChoiceBox = new ComboBox<>();
        databaseChoiceBox.setItems(FXCollections.observableArrayList(dab.getDatabases().keySet()));
        databaseChoiceBox.setPromptText("Select Database");

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(_ -> {
            String databaseName = databaseChoiceBox.getValue();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);

            if (databaseName != null && !databaseName.isEmpty()) {
                if (!dab.containsDatabase(databaseName)) {
                    alert.setContentText("Given database does not exist.");
                } else {
                    dab.removeDatabase(databaseName);
                    alert.setContentText("Database deleted: " + databaseName);
                    alert.showAndWait();
                    window.close();
                    return;
                }
            } else {
                alert.setContentText("Please select a database to delete.");
            }
            alert.showAndWait();
        });

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10));
        layout.setVgap(10);
        layout.setHgap(10);
        layout.setAlignment(Pos.CENTER);

        layout.add(nameLabel, 0, 0);
        layout.add(databaseChoiceBox, 1, 0);
        layout.add(deleteButton, 1, 2);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}