package org.example.hangman;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

import static javafx.beans.binding.Bindings.createObjectBinding;

public class MenuController {
    Stage primaryStage;
    public static MediaPlayer backgroundPlayer;
    @FXML
    public Button play;
    @FXML
    public VBox menuVBox;
    @FXML
    public Label titleLabel;

    public void initialize() {
        //Bindings
        primaryStage = Hangman.primaryStage;
        menuVBox.maxWidthProperty().bind(primaryStage.widthProperty().divide(2));
        menuVBox.maxHeightProperty().bind(primaryStage.heightProperty().divide(2));
        play.prefWidthProperty().bind(primaryStage.widthProperty().divide(3));
        titleLabel.fontProperty().bind(createObjectBinding(() -> Font.font(menuVBox.getWidth() / 6), menuVBox.widthProperty()));
        //Sound
        Media sound = new Media(new File("troll.mp3").toURI().toString());
        backgroundPlayer = new MediaPlayer(sound);
        //Keep repeating sound
        backgroundPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                backgroundPlayer.seek(Duration.ZERO);
            }
        });
        backgroundPlayer.play();
    }

    public void playGame(ActionEvent event) throws IOException {
        //Sound
        Media sound = new Media(new File("click.mp3").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        FXMLLoader fxmlLoader = new FXMLLoader(Hangman.class.getResource("in-game-view.fxml"));
        Scene inGame = new Scene(fxmlLoader.load());
        primaryStage.setScene(inGame);
    }
}