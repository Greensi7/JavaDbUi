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
import org.example.User.UserRecord;

public class MainAppWindowViewer {

    private final Stage stage;

    public MainAppWindowViewer(UserRecord user) {
        stage = new Stage();
        stage.setTitle("Choose Database");

        DatabaseList database = DatabaseList.load();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        Text label = new Text("Select a Database:");
        ComboBox<String> databaseComboBox = new ComboBox<>();
        assert database != null;
        databaseComboBox.getItems().addAll(database.getDatabases().keySet());
        databaseComboBox.setValue("NO DATABASE SELECTED");

        Button openButton = new Button("Open Database");
        openButton.setOnAction(_ -> {
            if(!databaseComboBox.getValue().equals("NO DATABASE SELECTED")) {
                new DatabaseWindow(database.getDatabase(databaseComboBox.getValue()), database, user).show();
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
        logoutButton.setOnAction(_ -> {
            new LoginPage().start(new Stage());
            stage.close();
        });
        HBox topRight = new HBox(logoutButton);
        topRight.setAlignment(Pos.TOP_RIGHT);
        topRight.setPadding(new Insets(0, 10, 0, 0));
        root.setTop(topRight);

        Button userPasswordChange = new Button("Change Password");
        userPasswordChange.setOnAction(_ -> ChangePasswordWindow.display(user));


        VBox bottomRight = new VBox(10, userPasswordChange);
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
