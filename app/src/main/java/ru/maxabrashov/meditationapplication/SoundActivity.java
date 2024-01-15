package ru.maxabrashov.meditationapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.HashMap;

public class SoundActivity extends AppCompatActivity {

    private int idMusic; // Поле с ID выбранной тематики музыки
    private HashMap<String, Integer> music; // Поле со списком музыки выбранной тематики

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        this.idMusic = getIntent().getIntExtra("idMusic", 0); // Получаем ID выбраной тематики музыки, из MainActivity
        this.music = (HashMap<String, Integer>) getIntent().getSerializableExtra("Music"); // Получаем список музыки выбранной тематики, из MainActivity
        SharedPreferences pref = getSharedPreferences("MAMeditationApp", MODE_PRIVATE); // Загружаем сохранения
        // Передаем список музыки и загружаем Адаптер в ListView
        SoundAdapter adapter = new SoundAdapter(this, this.music, pref, pref.getInt(String.valueOf(this.idMusic+1), 0));
        ListView mainList = findViewById(R.id.viewlistSounds);
        mainList.setAdapter(adapter);

        // При нажатие на кнопку "Выбрать", выбор пользователя сохраняется и перекидывает его обратно в MainActivity
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            SharedPreferences.Editor edit = pref.edit();
            edit.putInt(String.valueOf(idMusic+1), pref.getInt("tempMusic", 0));
            edit.putInt("tempMusic", -1);
            edit.apply();
            Intent intent = new Intent(SoundActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // При нажатие на кнопку со стелкой обратно, выбор пользователя НЕ сохраняется и перекидывает его обратно в MainActivity
        ImageButton buttonExit = findViewById(R.id.imageButton2);
        buttonExit.setOnClickListener(v -> {
            Intent intent = new Intent(SoundActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }
}