public class Timer {
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

        }
