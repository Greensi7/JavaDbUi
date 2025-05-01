package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.User.UserDatabase;
import org.example.User.UserRecord;
import org.example.User.UserType;

public class LoginPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        UserDatabase db = UserDatabase.load();
        db.removeUser("admin");
        db.addUser("admin", "admin", UserType.ADMIN);


        primaryStage.setTitle("Login");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 600, 400);
        primaryStage.setScene(scene);
        Text sceneTitle = new Text("Welcome");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);
        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
        Button signButton = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(signButton);
        grid.add(hbBtn, 1, 4);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);
        signButton.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            if (db.isValidUser(username, password)) {
                UserRecord user = db.getUserData(username);

                if(user.getUserType() == UserType.ADMIN) {
                    new MainAppWindowAdmin().show();
                }
                else{
                    new MainAppWindowViewer().show();
                }
                primaryStage.close();
            } else {
                actionTarget.setText("Invalid username or password");
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
