package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.Data.DatabaseList;

/**
 * Main Application Window after login.
 * Allows selection of a database and user management.
 */
public class MainAppWindowViewer {

    private Stage stage;

    public MainAppWindowViewer() {
        stage = new Stage();
        stage.setTitle("Choose Database");

        DatabaseList database = DatabaseList.load();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        Text label = new Text("Select a Database:");
        ComboBox<String> databaseComboBox = new ComboBox<>();
        databaseComboBox.getItems().addAll(database.getDatabases().keySet());
        databaseComboBox.setValue("NO DATABASE SELECTED");

        Button openButton = new Button("Open Database");
        openButton.setOnAction(e -> {
            if(!databaseComboBox.getValue().equals("NO DATABASE SELECTED")) {
                new DatabaseWindow(database.getDatabase(databaseComboBox.getValue())).show();
                stage.close();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText("You have not selected any database.");
                alert.showAndWait();
            }
        });

        centerBox.getChildren().addAll(label, databaseComboBox, openButton);
        root.setCenter(centerBox);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            new LoginPage().start(new Stage());
            stage.close();
        });
        HBox topRight = new HBox(logoutButton);
        topRight.setAlignment(Pos.TOP_RIGHT);
        topRight.setPadding(new Insets(0, 10, 0, 0));
        root.setTop(topRight);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
    }
}
