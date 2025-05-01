package org.example;

import cz.fi.muni.pb162.sqlike.data.database.Database;
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
public class MainAppWindowAdmin {

    private Stage stage;
    private DatabaseList database;

    public MainAppWindowAdmin() {
        stage = new Stage();
        stage.setTitle("Choose Database");

        database = DatabaseList.load();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);

        Text label = new Text("Select a Database:");
        ComboBox<String> databaseComboBox = new ComboBox<>();
        databaseComboBox.getItems().addAll(database.getDatabases().keySet());
        databaseComboBox.setValue("NO DATABASE SELECTED");

        Button openButton = new Button("Open Database");
        Button createDatabase = new Button("Create Database");
        Button deleteDatabase = new Button("Delete Database");
        deleteDatabase.setOnAction(e -> {
            DeleteDatabaseWindow.display();
            database = DatabaseList.load();
            databaseComboBox.getItems().clear();
            databaseComboBox.getItems().addAll(database.getDatabases().keySet());
        });
        createDatabase.setOnAction(e -> {
            CreateDatabaseWindow.display();
            database = DatabaseList.load();
            databaseComboBox.getItems().clear();
            databaseComboBox.getItems().addAll(database.getDatabases().keySet());
        });
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

        centerBox.getChildren().addAll(label, databaseComboBox, openButton, createDatabase, deleteDatabase);
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

        Button createUserButton = new Button("Create User");
        Button deleteUser = new Button("Delete User");
        Button allUsers = new Button("Display All Users");
        allUsers.setOnAction(e -> UserListWindow.display());
        createUserButton.setOnAction(e -> CreateUserWindow.display());
        deleteUser.setOnAction(e -> DeleteUserWindow.display());

        HBox bottomRight = new HBox(10, createUserButton, deleteUser, allUsers);
        bottomRight.setAlignment(Pos.BOTTOM_RIGHT);
        bottomRight.setPadding(new Insets(10));
        root.setBottom(bottomRight);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
    }
}
