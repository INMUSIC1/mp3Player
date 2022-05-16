package com.example.mp3player;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Controller {

    @FXML
    private Button albums;

    @FXML
    private Button artists;

    @FXML
    private Button lastAdded;

    @FXML
    private Button mediaLibrary;

    @FXML
    private Button playlists;

    @FXML
    private TextField searcher;

    @FXML
    private Button songs;

    @FXML
    private Button toSearch;

    @FXML
    void onMediaLibraryAsClick(ActionEvent event) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        mediaLibrary.setOnAction(event1 -> mediaLibrary.setEffect(shadow));
        mediaLibrary.setOnAction(event1 -> System.out.println("Медиатека"));
    }

    @FXML
    void onPlaylistsAsClick(ActionEvent event) {
        playlists.setOnAction(event2 -> System.out.println("Плейлист"));
    }

    @FXML
    void onSearchAsClick(ActionEvent event) {
        toSearch.setOnAction(event3 -> System.out.println("Поиск..."));
        System.out.println("Поиск: " + searcher.getText());
    }

    @FXML
    void onSongsAsClick(ActionEvent event) throws Exception {
        songs.setOnAction(event4 -> System.out.println("Песни"));
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser
                .ExtensionFilter("Выбрать файл (.mp3)", ".mp3");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(null);
        String filePath = file.toURI().toString();
        songLabel.setText(file.getName());
        if (filePath != null) {
            Media media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();

            volumeSlider.setValue(mediaPlayer.getVolume()*100);
            volumeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    mediaPlayer.setVolume(volumeSlider.getValue()/100);
                }
            });
            songSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mediaPlayer.seek(Duration.seconds(songSlider.getValue()));
                }
            });
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                    songSlider.setValue(newValue.toSeconds());
                }
            });
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
                    bindCurrentTimeLabel();
                    if (!songSlider.isValueChanging()) {
                        songSlider.setValue(newTime.toSeconds());
                    }
                    labelCurrentTime.getText();
                    labelTotalTime.getText();
                }
            });
            mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                    songSlider.setMax(newDuration.toSeconds());

                }
            });
            songSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mediaPlayer.seek(Duration.seconds(songSlider.getValue()));

                }
            });
            songSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    mediaPlayer.seek(Duration.seconds(songSlider.getValue()));
                }
            });
            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    Duration total = media.getDuration();
                    songSlider.setMax(total.toSeconds());
                }
            });
        }
    }

    private void bindCurrentTimeLabel() {
        labelCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return null;
            }
        }));


    }

    @FXML
    void onLastAddedAsClick(ActionEvent event) {
        lastAdded.setOnAction(event5 -> System.out.println("Последние добавленные"));
    }

    @FXML
    void onAlbumsAsClick(ActionEvent event) {
        albums.setOnAction(event6 -> System.out.println("Альбомы"));
    }

    @FXML
    void onArtistsAsClick(ActionEvent event) {
        artists.setOnAction(event7 -> System.out.println("Артисты"));
    }

    private boolean searchFor() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Поиск...");
        File file = chooser.showSaveDialog(null);
        file.createNewFile();

        System.out.println("Поиск: " + file.getAbsolutePath());
        System.out.println("Поиск: " + searcher.getText());
        return true;
    }

    @FXML
    private Pane pane;
    @FXML
    private Label songLabel;
    @FXML
    private Label labelCurrentTime;
    @FXML
    private Label labelTotalTime;
    @FXML
    private HBox mainButtons;
    @FXML
    private Button playButton, pauseButton, previousButton, nextButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider songSlider;
    private Media media;
    private MediaPlayer mediaPlayer;
    @FXML
    private void playMedia(ActionEvent event) {
        mediaPlayer.play();
    }
    @FXML
    private void pauseMedia(ActionEvent event) {
        mediaPlayer.pause();
    }
}