package com.example.mp3player;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;


public class Controller implements Initializable {
    private ImageView iconPlay;
    private ImageView iconPause;
    private ImageView iconNext;
    private ImageView iconPrevious;
    /** кнопка "Артисты" */
    @FXML
    private Button artists;
    /** кнопка "Последние добавленные" */
    @FXML
    private Button lastAdded;
    /** кнопка "Медиатека"*/
    @FXML
    private Button mediaLibrary;
    /** кнопка "Плейлисты" */
    @FXML
    private Button playlists;
    /** текстовое поле для поиска */
    @FXML
    private TextField searcher;
    /** кнопка "Добавить песню" */
    @FXML
    private Button songs;
    /** кнопка, запускающая процесс поиска */
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

    private ListView<String> listViewPlaylists;
    //private  List<String> downloadedSongs;
                //= new ArrayList<>(Arrays.asList("hhhhh"));

    private Timer timer;
    private TimerTask task;
    private boolean running;
    private double totalTime;
    private double currentTime;
    private boolean active_track;
    private int flag1 = 0;
    private int songNumber;
    //private Playlist current_playlist = new Playlist();
    private ArrayList<String> playlist = new ArrayList<>();
    private ArrayList<String> playlist_names = new ArrayList<>();
    private static String current_playlist = "allSongs";
    //private ArrayList<Song> current_songs = new ArrayList<>();
    private ArrayList<String> current_song_names = new ArrayList<>();
    private File main_directory = new File("C:\\Playlists");

    @FXML
    private Label chosenSongFromListView;

    /** активация кнопки и наложение эффекта */
    @FXML
    void onMediaLibraryAsClick(ActionEvent event) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        mediaLibrary.setOnAction(event1 -> mediaLibrary.setEffect(shadow));
        mediaLibrary.setOnAction(event1 -> System.out.println("Медиатека"));
    }
    /** активация кнопки "Плейлисты" */
    @FXML
    void onPlaylistsAsClick(ActionEvent event) throws IOException {
        playlists.setOnAction(event2 -> System.out.println("Плейлист"));
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File playlist_import_dir = directoryChooser.showDialog(null);
            while (Objects.requireNonNull(playlist_import_dir.listFiles()).length == 0) {
                playlist_import_dir = directoryChooser.showDialog(null);
            }
            File dir = new File(playlist_import_dir.toURI());
            List<File> lst = new ArrayList<>();
            flag1 = 0;
            String import_dir = null;
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if ((file.isFile()) & (file.toString().endsWith("mp3"))) {
                    lst.add(file);
                    String trackfromplaylist = file.toString();
                    trackfromplaylist = trackfromplaylist.replaceAll("%20", " ");
                    trackfromplaylist = trackfromplaylist.replace("\\", "/");
                    List<String> lines = FileUtils.readLines(new File("C:\\Playlists\\allSongs.txt"), "utf-8");
                    if (lines.size() != 0) {
                        for (int i = 0; i < lines.size(); i++) {
                            if (lines.get(i).equals(trackfromplaylist)) {
                                flag1++;
                            }
                        }
                        if (flag1 == 0) {
                            FileWriter writer = new FileWriter("C:\\Playlists\\allSongs.txt", true);
                            BufferedWriter bufferWriter = new BufferedWriter(writer);
                            bufferWriter.write(trackfromplaylist + "\n");
                            bufferWriter.close();
                        }
                    } else {
                        FileWriter writer = new FileWriter("C:\\Playlists\\allSongs.txt", true);
                        BufferedWriter bufferWriter = new BufferedWriter(writer);
                        bufferWriter.write(trackfromplaylist + "\n");
                        bufferWriter.close();
                    }
                    String directory = playlist_import_dir.toString().replace("\\", "/");
                    ArrayList<String> array = new ArrayList<String>(List.of(directory.split("/")));
                    import_dir = (String) array.get(array.size() - 1);
                    FileWriter writer2 = new FileWriter("C:\\Playlists\\" + import_dir + ".txt", true);
                    BufferedWriter bufferWriter1 = new BufferedWriter(writer2);
                    bufferWriter1.write(trackfromplaylist + "\n");
                    bufferWriter1.close();
                    File createFile = new File("C:\\Playlists\\" + import_dir + ".txt");
//                    if (!createFile.exists())
//                        try {
//                            createFile.createNewFile();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
                }
            }
            current_playlist = import_dir;
            System.out.println(current_playlist);
            updateSongs();
        } catch (RuntimeException | IOException e) {
            System.out.println("incorrect input");
        }
       if (listViewSongs != null) {
           bottomMenu.setVisible(true);
           setIcons();
           playPlaylist();
           //listViewSongs.setVisible(true);
        }
    }

    /** активация кнопки, запускающей поиск */
    @FXML
    void toSearch(ActionEvent event) {
        toSearch.setOnAction(event3 -> System.out.println("Поиск..."));
        System.out.println("Поиск: " + searcher.getText());
        listViewSongs.getItems().clear();
        listViewSongs.getItems().addAll(searchList(searcher.getText(), current_song_names));
    }

    void readyToPlay(){
        setIcons();
    }

    /** При нажатии кнопки "Добавить песню" появляется диалоговое окно, в котором предлагается выбрать песню для проигрывания.
     * Далее трек проигрывается
     * @throws Exception при возникновении ошибки
     * @see FileChooser.ExtensionFilter при выборе песни в диалоговом окне есть фильтр на выбор файла в формате mp3
     *
     */
    @FXML
    void onSongsAsClick(ActionEvent event) throws Exception {
        songs.setOnAction(event4 -> System.out.println("Песни"));
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser
                .ExtensionFilter("Выбрать файл (.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(null);
        String filePath = file.toURI().toString();

        current_playlist = "allSongs";
            String song = filePath.replaceAll("file:/", "");
            song = song.replaceAll("%20", " ");
            flag1 = 0;
            List<String> lines = FileUtils.readLines(new File("C:\\Playlists\\allSongs.txt"), "utf-8");
            if (lines.size() != 0) {
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).equals(song)) {
                        flag1++;
                    }
                }
                if (flag1 == 0) {
                    FileWriter writer1 = new FileWriter("C:\\Playlists\\allSongs.txt", true);
                    BufferedWriter bufferWriter = new BufferedWriter(writer1);
                    bufferWriter.write(song + "\n");
                    bufferWriter.close();
                }
            } else {
                FileWriter writer1 = new FileWriter("C:\\Playlists\\allSongs.txt", true);
                BufferedWriter bufferWriter = new BufferedWriter(writer1);
                bufferWriter.write(song + "\n");
                bufferWriter.close();
            }
        songLabel.setText(file.getName());
        if (filePath != null) {
            Media media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
            bottomMenu.setVisible(true);
            setIcons();
            playMedia();
            listViewSongs.setVisible(true);
            updateSongs();

        }
    }

    /**
     * @param time установка продолжительности трека
     * @return возращается формат продолжительности трека --:--:-- или --:--
     */
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

    /** начало работы таймлайна. если песня проигрывается, то видно сколько времени с начала песни прошли и сколько всего длится трек
     */
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

    /** окончание работы таймлайна. если песня не проигрывается, то таймлайн перестает работать */
    public void cancelTimer() {
        running = false;
        timer.cancel();
    }

    /** активация кнопки "Последние добавленные" */
    @FXML
    void onLastAddedAsClick(ActionEvent event) {
        lastAdded.setOnAction(event5 -> System.out.println("Последние добавленные"));
    }

    /** активация кнопки "Артисты" */
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

    /** восроизвести песню */
    @FXML
    private void playMedia() {
        mediaPlayer.play();
        running=true;
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

        listViewSongs.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    if (running) {
                        mediaPlayer.stop();
                    }
                    songNumber = listViewSongs.getSelectionModel().getSelectedIndex();
                    try {
                        playPlaylist();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    /** поставить на паузу */
    @FXML
    private void pauseMedia() {
        mediaPlayer.pause();
    }
//    private void changeCurrentPlaylist(String new_name) {
//        current_playlist = new_name;
//    }

    /** проигрывание плейлиста
     * @throws IOException при возникновении ошибки */
    private void playPlaylist() throws IOException {
        List<String> lines = FileUtils.readLines(new File("C:\\Playlists\\" + current_playlist + ".txt"), "utf-8");
        String filePath = lines.get(songNumber);
        File f = new File(filePath);
        String name = f.getName();
        URI filepath = URI.create(("file:/" + filePath).replaceAll(" ", "%20"));
        media = new Media(filepath.toString());
        mediaPlayer = new MediaPlayer(media);
        bottomMenu.setVisible(true);
        songLabel.setText(name);
        playMedia();
    }

    /** обновление загруженных песен
     * @throws IOException при возникновении ошибки
     */
    private void updateSongs() throws IOException {
        Scanner s = new Scanner(new File("C:\\Playlists\\" + current_playlist + ".txt"));
        ArrayList<String> downloadedSongs = new ArrayList<String>();
        int length = 0;
        while (s.hasNextLine()) {
            String G[] = s.nextLine().split("/");
            int glength = G.length - 1;
            downloadedSongs.add(G[glength].replaceAll(".mp3", ""));
            length++;
        }
        s.close();
        //System.out.println("Текущий плейлист: " + current_playlist);
        listViewSongs.getItems().clear();
        listViewSongs.getItems().addAll(downloadedSongs);
    }
//    private void updatePlaylist() throws IOException {
//        int playlist_amount = Objects.requireNonNull(main_directory.listFiles()).length;
//        if (playlist_amount > 0) {
//           playlist.clear();
//           playlist_names.clear();
//           listViewPlaylists.getItems().clear();
//            for (File f : Objects.requireNonNull(main_directory.listFiles())) {
//                playlist.add(f.getName());
//                playlist_names.add(f.getName().replaceAll(".txt", ""));
//            }
//            //listViewPlaylists.getItems().clear();
//            listViewPlaylists.getItems().addAll(playlist_names);
//        }
//    }
    /** установка иконок для кнопок "play", "pause", "next", "previous" */
    private void setIcons(){
        Image imagePlay = new Image(new File("src/main/resources/com/example/mp3player/asserts/play-button.png").toURI().toString());
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
        //listViewSongs.getItems().addAll(lines);
    }

    private List<String> searchList(String searchSongs, List<String> listOfStrings) {
        List<String> current_song_names = Arrays.asList(searchSongs.trim().split(" "));
        return listOfStrings.stream().filter(input -> {
            return current_song_names.stream().allMatch(song ->
                    input.toLowerCase().contains(song.toLowerCase()));
        }).collect(Collectors.toList());
    }
}
