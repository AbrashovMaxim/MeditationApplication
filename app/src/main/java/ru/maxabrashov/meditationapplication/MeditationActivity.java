package ru.maxabrashov.meditationapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MeditationActivity extends AppCompatActivity implements Runnable {

    private int music = 0; // Поле для ID музыки в папке Resources
    private int time = 1; // Поле времени
    private int audioDuration = 1; // Поле длительности музыки

    private int slideBarMax = 1; // Поле максимального значения Ползунка
    private int slideBarMin = 1; // Поле минимального значения Ползунка
    private SeekBar seekBar = null; // Поле виджета Ползунка
    private MediaPlayer mediaPlayer = new MediaPlayer(); // Поле эелемента Проигрывателя
    private ImageButton playPause = null; // Поле кнопки Проигрывателя
    private boolean wasPlaying = false; // Поле Должна ли проигрываться музыка?
    private boolean wasStop = false; // Поле Остановлен ли проигрыватель?

    private TextView seekBarHint = null; // Поле текста Текущего проигрывающего времени
    private TextView seekBarLeft = null; // Поле текста Левая нижняя часть ползунка
    private TextView seekBarRight = null; // Поле текста Правая нижняя часть ползунка
    private Thread mainThread = null; // Поле основного потока для обновления Текущего проигрывающего времени.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_meditation);
        this.music = getIntent().getIntExtra("music", 0); // Получение ID музыки, при передачи из MainActivity
        this.time = getIntent().getIntExtra("time", 0) * 60000; // Получение длительности медитации и переводим в Миллисекунды, при передази из MainActivity

        this.playPause = findViewById(R.id.playPause); // Получаем кнопку Проигрывателя из интерфейса
        this.playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // При нажатие на кнопку, запускается проигрывание музыки
                playSong();
            }
        });

        this.seekBarHint = findViewById(R.id.textMain); // Получаем текст Текущего проигрывающего времени из интерфейса
        this.seekBarLeft = findViewById(R.id.textLeft); // Получаем текст в Левой нижней части ползунка из интерфейса
        this.seekBarRight = findViewById(R.id.textRight); // Получаем текст в Правой нижней части ползунка из интерфейса

        this.seekBar = findViewById(R.id.seekBar); // Получаем Ползунок из интерфейса
        this.seekBar.setMax(this.time); // Устанавливаем максимальное значение ползунка
        mediaPlayerComplete(); // Устанавливаем событие, которое запускает в момент остановки своей работы Проигрывателем

        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == time) { // Если прогресс Ползунка равняется длительности медитации
                    // Останавливаем работу проигрывателя и возвращаем все в исходное положение
                    wasPlaying = false;
                    slideBarMin = 1;
                    slideBarMax = audioDuration;
                    seekBar.setProgress(0);
                    wasStop = false;
                    clearMediaPlayer();
                    playPause.setImageDrawable(ContextCompat.getDrawable(MeditationActivity.this, R.drawable.play));
                    seekBarHint.setText("00:00");
                    seekBarLeft.setText("00:00");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    // В момент, когда пользователь закончил перемещение Ползунка
                    int max = audioDuration;
                    int min = 1;
                    // Мы делим Ползунок по секторам, которые равняются длине музыки
                    for (int j = 0; j < time/60000; j++) {
                        // Если у выбранного сектора начало меньше положения Ползунка и конец больше положения Ползунка
                        if (seekBar.getProgress() >= min && seekBar.getProgress() <= max) {
                            // То мы получаем положение Ползунка именно для этого сектора, чтобы проигрыватель музыка именно в этот момент
                            mediaPlayer.seekTo(seekBar.getProgress() - min);
                            slideBarMax = max;
                            slideBarMin = min;
                            wasPlaying = true;
                            break;
                        }
                        min = max;
                        max += audioDuration;
                    }

                }
                else {
                    // Если Проигрывателя не существует
                    wasPlaying = false;
                    wasStop = false;
                    playSong();
                }
            }
        });

        // Кнопка "Выйти", нажав на которую, пользователь заканчиваем медитацию и переносится в MainActivity
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            mainThread.interrupt();
            clearMediaPlayer();
            Intent intent = new Intent(MeditationActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    public void playSong() { // Проигрыванием музыки

        try {
            if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) { // Если в данный момент идет проигрывание музыки
                this.mediaPlayer.pause(); // Ставим музыку на паузу
                this.wasPlaying = false;
                this.wasStop = true;
                this.playPause.setImageDrawable(ContextCompat.getDrawable(MeditationActivity.this, R.drawable.play)); // Изменяем иконку
                return;
            }

            if (!this.wasPlaying) { // Если в данный момент нету проигрывания музыки

                if (this.wasStop) { // Если в данный момент остановлено проигрывание
                    this.mediaPlayer.start(); // Запускаем проигрывание
                    this.wasPlaying = true;
                    this.wasStop = false;
                    this.playPause.setImageDrawable(ContextCompat.getDrawable(MeditationActivity.this, R.drawable.pause));
                    return;
                }

                if (this.mediaPlayer == null) { // Если в данный момент Проигрывателя не существует
                    this.mediaPlayer = new MediaPlayer();
                    mediaPlayerComplete();
                }

                // Получаем ссылку на музыку из Resources и загружаем в проигрыватель
                this.playPause.setImageDrawable(ContextCompat.getDrawable(MeditationActivity.this, R.drawable.pause));
                Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/"+this.music);
                this.mediaPlayer.setDataSource(this, mediaPath);
                // Подгатавливаем проигрыватель к запуску
                this.mediaPlayer.prepare();
                this.mediaPlayer.setVolume(1f, 1f);
                this.mediaPlayer.setLooping(false);
                this.slideBarMax = this.mediaPlayer.getDuration();
                this.audioDuration = this.mediaPlayer.getDuration();
                this.seekBarRight.setText(getStringOnTime(this.time));
                this.mediaPlayer.start();
                wasPlaying = true;
                if (this.mainThread == null) {
                    this.mainThread = new Thread(this);
                    this.mainThread.start();
                }
            }

            this.wasPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public void run() { // Запуск потока, для обновления текста текущего времени медитации
        while (true) {
            try {
                Thread.sleep(1000); // Обновлене каждую секунду
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        int finalCurrentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(slideBarMin + finalCurrentPosition);
                        seekBarHint.setText(getStringOnTime(slideBarMin + finalCurrentPosition));
                        seekBarLeft.setText(getStringOnTime(slideBarMin + finalCurrentPosition));
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    // Метод очищения Проигрывателя и удаления Потока
    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }

    // Метод очищение Проигрывателя
    private void clearMediaPlayer() {
        try {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Метод устанавливает событие, при котором Проигрыватель заканчивает свою работу
    private void mediaPlayerComplete() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (seekBar.getProgress() != time) {
                        slideBarMin = slideBarMax;
                        slideBarMax += audioDuration;
                        wasPlaying = false;
                        wasStop = false;
                        mediaPlayer.reset();
                        playSong();
                    }
                }
            });
        }
    }

    // Метод преобразующий миллисекунды во время в формате "Minutes:Seconds"
    private String getStringOnTime(int time) {
        try {
            int minutes = (time / 1000) / 60;
            int seconds = (time / 1000) % 60;

            return  String.format("%02d:%02d", minutes, seconds);
        } catch (Exception e) { e.printStackTrace(); }
        return "";

    }
}