package org.example.hangman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Hangman extends Application {
    public static Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Hangman.class.getResource("menu-view.fxml"));
        Scene menu = new Scene(fxmlLoader.load());
        stage.setTitle("Hangman");
        try {
            stage.getIcons().add(new Image("file:hangman.jpg"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        stage.setScene(menu);
        stage.setMinWidth(528);
        stage.setMinHeight(581);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}