package org.example.hangman;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.MotionBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class GameController {
    private String word;
    private String visibleWord;
    Connection conn;
    List<Node> shapes;
    String incorrectLetters;
    private int incorrectCount;
    public EventHandler<KeyEvent> keyEventHandler;
    private MediaPlayer mediaPlayer;
    @FXML
    HBox visibleHbox;
    @FXML
    HBox incorrectHbox;
    @FXML
    Node shape1;
    @FXML
    Node shape2;
    @FXML
    Node shape3;
    @FXML
    Node shape4;
    @FXML
    Node shape5;
    @FXML
    Node shape6;

    private void updateVisible() {
        visibleHbox.getChildren().clear();
        for (int i = 0; i < visibleWord.length(); ++i) {
            char ch = visibleWord.charAt(i);
            Label label = new Label();
            label.setText(String.valueOf(ch));
            label.getStyleClass().add("label-visible");
            visibleHbox.getChildren().add(label);
        }
    }

    private void start() {
        Statement stmt = null;
        try {
            conn =
                    DriverManager.getConnection("jdbc:mysql://localhost/hangman?" +
                            "user=root&password=");
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("Select count(*) as num_words from words");
            rs.first();
            int num_words = rs.getInt("num_words");
            Random random = new Random();
            int id = random.nextInt(num_words - 1) + 1;
            rs = stmt.executeQuery("SELECT * from words where id = " + id);
            rs.first();
            word = rs.getString("word").toUpperCase();
            shapes = new ArrayList<>();
            shapes.add(0, shape1);
            shapes.add(1, shape2);
            shapes.add(2, shape4);
            shapes.add(3, shape6);
            shapes.add(4, shape3);
            shapes.add(5, shape5);
            incorrectHbox.getChildren().clear();
            visibleWord = rs.getString("visible").toUpperCase();
            updateVisible();
            for (Node node : shapes) {
                node.setVisible(false);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Add key listener
        keyEventHandler = (key) -> {
            KeyCode keyCode = key.getCode();
            if (keyCode.isLetterKey()) {
                checkLetter(keyCode.getChar());
            }
        };
        Hangman.primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
    }

    // synchronized to avoid possible sound interruptions
    public synchronized void checkLetter(String letter) {
        try {
            boolean findResult = false;
            for (int i = 0; i < word.length(); ++i)
                if (word.charAt(i) == letter.charAt(0) && visibleWord.charAt(i) == '_') {
                    findResult = true;
                    visibleWord = visibleWord.substring(0, i) + letter + visibleWord.substring(i + 1);
                }
            // Sound
            Media sound = null;
            if (findResult) {
                sound = new Media(new File("bell.mp3").toURI().toString());
            } else if (incorrectLetters == null || !incorrectLetters.contains(letter)) {
                ++incorrectCount;
                if (incorrectCount % 3 == 1)
                    sound = new Media(new File("boing.mp3").toURI().toString());
                else if (incorrectCount % 3 == 2)
                    sound = new Media(new File("boing-2.mp3").toURI().toString());
                else if (incorrectCount % 3 == 0) {
                    sound = new Media(new File("boo.mp3").toURI().toString());
                }
                incorrectLetters = incorrectLetters + " " + letter;
                shapes.get(incorrectCount - 1).setVisible(true);
                Label wrongLetterLabel = new Label();
                wrongLetterLabel.setText(letter);
                wrongLetterLabel.getStyleClass().add("label-incorrect");
                incorrectHbox.getChildren().add(wrongLetterLabel);
            }
            // check if nothing happens
            if (sound != null) {
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
                Platform.runLater(this::updateVisible);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (incorrectCount >= 6)
            lost();
        if (!visibleWord.contains("_"))
            win();
    }

    private void win() {
        MenuController.backgroundPlayer.stop();
        // Stop listening to letters
        Hangman.primaryStage.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
        Media sound = new Media(new File("yeah-boiii-i-i-i.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        for (Node node : visibleHbox.getChildren()) {
            ((Label) node).setStyle("-fx-text-fill: #35d7b9");
        }

    }

    private void lost() {
        MotionBlur lostBlur = new MotionBlur();
        lostBlur.setAngle(28);
        for (Node node : shapes)
            node.setEffect(lostBlur);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(lostBlur.radiusProperty(), 0)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(lostBlur.radiusProperty(), 63))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setCycleCount(1);
        timeline.play();
        MenuController.backgroundPlayer.stop();
        Media sound = new Media(new File("wtf.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        // Stop listening to letters
        Hangman.primaryStage.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
    }

    public void initialize() {
        start();
        Hangman.primaryStage.setResizable(false);

    }

    public void backMenu(ActionEvent event) {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Hangman.class.getResource("menu-view.fxml"));
            Scene menu = new Scene(fxmlLoader.load());
            Hangman.primaryStage.setScene(menu);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
