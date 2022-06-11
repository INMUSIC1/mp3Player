package com.example.mp3player;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Controller implements Initializable{

    private ImageView iconPlay;
    private ImageView iconPause;
    private ImageView iconNext;
    private ImageView iconPrevious;

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
    private Label songName;

    @FXML
    private Label songAuthor;

    @FXML
    private Label playButton;

    @FXML
    private Label pauseButton;

    @FXML
    private AnchorPane bottomMenu;

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
    private Label previousButton;
    @FXML
    private Label nextButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider songSlider;
    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    private ListView<String> listViewSongs;

    ArrayList<String> downloadedSongs = new ArrayList<>(Arrays.asList("OG Buda & дора - Капли", "Лолита - На Титанике"));

    private Timer timer;
    private TimerTask task;
    private boolean running;
    private double totalTime;
    private double currentTime;
    private boolean active_track;
    private Playlist current_playlist = new Playlist();
    private ArrayList<Playlist> playlist = new ArrayList<>();
    private ArrayList<String> playlist_names = new ArrayList<>();
    private ArrayList<Song> current_songs = new ArrayList<>();
    private ArrayList<String> current_song_names = new ArrayList<>();
    private File main_directory = new File("src/main/java/com/example/mp3player/Playlist.java");

    @FXML
    private Label chosenSongFromListView;

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
    void toSearch(ActionEvent event) {
        toSearch.setOnAction(event3 -> System.out.println("Поиск..."));
        System.out.println("Поиск: " + searcher.getText());
        listViewSongs.getItems().clear();
        listViewSongs.getItems().addAll(searchList(searcher.getText(), downloadedSongs));
    }

    void readyToPlay(){
        setIcons();
    }

    @FXML
    void onSongsAsClick(ActionEvent event) throws Exception {
        songs.setOnAction(event4 -> System.out.println("Песни"));
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser
                .ExtensionFilter("Выбрать файл (.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(null);
        String filePath = file.toURI().toString();

        songLabel.setText(file.getName());
        if (filePath != null) {
            Media media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
            bottomMenu.setVisible(true);
            setIcons();
            playMedia();

            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            volumeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
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
                    labelTotalTime.setText(getTime(newDuration));

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
           playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
               @Override
               public void handle(MouseEvent mouseEvent) {
                   playMedia();
               }
           });
            pauseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    pauseMedia();
                }
            });


        }
    }
    public String getTime(Duration time) {

        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (seconds > 59) seconds = seconds % 60;
        if (minutes > 59) minutes = minutes % 60;
        if (hours > 59) hours = hours % 60;

        if (hours > 0) return String.format("%d:%02d:%02d",
                hours,
                minutes,
                seconds);
        else return String.format("%02d:%02d",
                minutes,
                seconds);
    }
    public void bindCurrentTimeLabel() {
        labelCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() {
                return getTime(mediaPlayer.getCurrentTime());
            }
        }, mediaPlayer.currentTimeProperty()));
    }

    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = mediaPlayer.getTotalDuration().toSeconds();
                songSlider.setValue(current / end);
                if (current / end == 1) {
                    cancelTimer();
                }
            }
        };
    }
    public void cancelTimer() {
        running = false;
        timer.cancel();
    }

    @FXML
    void onLastAddedAsClick(ActionEvent event) {
        lastAdded.setOnAction(event5 -> System.out.println("Последние добавленные"));
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
    private void playMedia() {
        mediaPlayer.play();

    }
    @FXML
    private void pauseMedia() {
        mediaPlayer.pause();
    }

    private void setIcons(){
        Image imagePlay = new Image (new File("src/main/resources/com/example/mp3player/asserts/play-button.png").toURI().toString());
        iconPlay = new ImageView(imagePlay);
        iconPlay.setFitWidth(40);
        iconPlay.setFitHeight(40);

        Image imagePause = new Image (new File("src/main/resources/com/example/mp3player/asserts/pause-button.png").toURI().toString());
        iconPause = new ImageView(imagePause);
        iconPause.setFitWidth(40);
        iconPause.setFitHeight(40);

        Image imageNext = new Image (new File("src/main/resources/com/example/mp3player/asserts/next-button.png").toURI().toString());
        iconNext = new ImageView(imageNext);
        iconNext.setFitWidth(40);
        iconNext.setFitHeight(40);

        Image imagePrevious = new Image (new File("src/main/resources/com/example/mp3player/asserts/previous-button.png").toURI().toString());
        iconPrevious = new ImageView(imagePrevious);
        iconPrevious.setFitWidth(40);
        iconPrevious.setFitHeight(40);


        playButton.setGraphic(iconPlay);
        pauseButton.setGraphic(iconPause);
        nextButton.setGraphic(iconNext);
        previousButton.setGraphic(iconPrevious);
    }
    @FXML
    public void initialize(URL arg0, ResourceBundle arg1) {
        listViewSongs.getItems().addAll(downloadedSongs);
    }

    private List<String> searchList(String searchSongs, List<String> listOfStrings) {
        List<String> searchSongsArray = Arrays.asList(searchSongs.trim().split(" "));
        return listOfStrings.stream().filter(input -> {
            return searchSongsArray.stream().allMatch(song ->
                    input.toLowerCase().contains(song.toLowerCase()));
        }).collect(Collectors.toList());
    }
}