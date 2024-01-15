package ru.maxabrashov.meditationapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.collections.CollectionsKt;
import ru.maxabrashov.meditationapplication.R.layout;
import ru.maxabrashov.meditationapplication.R.drawable;
import ru.maxabrashov.meditationapplication.R.raw;

public final class MainActivity extends ComponentActivity {

    private SharedPreferences pref; // Поле для работы с сохранениями
    private List<HashMap<String, Integer>> MusicList = new ArrayList<>(); // Поле для списка музыки

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        init_pref(); // Инициализация сохранений
        loadMusic(); // Загрузка музыки
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_main);

        // Получаем поле для указания длительности медитации
        EditText input = this.findViewById(R.id.editTextNumber);
        // Загружаем длительность медитации из сохранений в поле ввода
        input.setText(String.valueOf(this.pref.getInt("timer", 1)));
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Если длина поля или пользователь укажет длительность медитации 0, то устанавливаем в поле 1
                if (s.length() == 0 || s.toString().equals("0")) { input.setText("1"); }
                else { // Если со значениями все нормально, то мы их сохраняем
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putInt("timer", Integer.parseInt(s.toString()));
                    edit.apply();
                }

            }
        });

        // Получаем список изображений
        List images = CollectionsKt.listOf(new Integer[]{drawable.m1, drawable.m2, drawable.m3, drawable.m4, drawable.m5, drawable.m6});
        // Загружаем Свайп Адаптер и вносим в него изображения
        ViewPagerAdapter adapter = new ViewPagerAdapter(images);
        ViewPager2 viewPager2 = this.findViewById(R.id.view_pager_2);
        viewPager2.setAdapter(adapter);

        // Получаем из сохранений выбранную ранее тему медитации и устанавливаем её
        viewPager2.setCurrentItem(this.pref.getInt("common", 0));

        // Получаем текст ниже ListView
        TextView text = findViewById(R.id.textView2);
        // Устанавливаем название музыки на основе сохраненного значения для этой темы ( Если нету, то устанавливаем 1 элемент )
        text.setText(String.valueOf(MusicList.get(viewPager2.getCurrentItem()).keySet().toArray()[pref.getInt(String.valueOf(viewPager2.getCurrentItem() + 1), 0)]));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                // При Свайпе ViewPager2, мы сохраняем сразу же выбранную тему и устанавливаем название музыки на основе сохраненного значения для этой темы ( Если нету, то устанавливаем 1 элемент )
                if (state == 0) {
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putInt("common", viewPager2.getCurrentItem());
                    edit.apply();
                    text.setText(String.valueOf(MusicList.get(viewPager2.getCurrentItem()).keySet().toArray()[pref.getInt(String.valueOf(viewPager2.getCurrentItem() + 1), 0)]));
                }
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Если пользователь нажал на текст с названием музыки - перекидываем его в интерфейс SoundActivity
                Intent intent = new Intent(MainActivity.this, SoundActivity.class);
                // Передаем выбранную ID выбранной тематики
                intent.putExtra("idMusic", viewPager2.getCurrentItem());
                // Передаем список музыки выбранной тематики
                intent.putExtra("Music", MusicList.get(viewPager2.getCurrentItem()));
                // Загружаем интерфейс
                startActivity(intent);
            }
        });

        Button angryButton = findViewById(R.id.button);
        angryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Если пользователь нажал на кнопку "Начать" - то перекидываем его в интерфейс MeditationActivity
                Intent intent = new Intent(MainActivity.this, MeditationActivity.class);
                // Передаем ID выбранной музыки
                intent.putExtra("music", MusicList.get(viewPager2.getCurrentItem()).get(String.valueOf(MusicList.get(viewPager2.getCurrentItem()).keySet().toArray()[pref.getInt(String.valueOf(viewPager2.getCurrentItem() + 1), 0)])));
                // Передаем длительность медитации
                intent.putExtra("time", Integer.parseInt(input.getText().toString()));
                // Загружаем интерфейс
                startActivity(intent);
            }
        });
    }

    private void loadMusic() { // Загрузка музыки

        HashMap<String, Integer> music = new HashMap<>(); // Создаем ХэшМап со Строкой и Числом
        music.put("Музыка гор 1", raw.one1); // Добавляем в ХэшМап Название и ID музыки из папки Resources
        music.put("Музыка гор 2", raw.one2);
        music.put("Музыка гор 3", raw.one3);
        music.put("Музыка гор 4", raw.one4);
        music.put("Музыка гор 5", raw.one5);
        this.MusicList.add(music); // Добавляем в список
        music = new HashMap<>();
        music.put("Музыка лягушки 1", raw.two1);
        music.put("Музыка лягушки 2", raw.two2);
        music.put("Музыка лягушки 3", raw.two3);
        music.put("Музыка лягушки 4", raw.two4);
        music.put("Музыка лягушки 5", raw.two5);
        this.MusicList.add(music);
        music = new HashMap<>();
        music.put("Музыка клубнички 1", raw.three1);
        music.put("Музыка клубнички 2", raw.three2);
        music.put("Музыка клубнички 3", raw.three3);
        music.put("Музыка клубнички 4", raw.three4);
        music.put("Музыка клубнички 5", raw.three5);
        this.MusicList.add(music);
        music = new HashMap<>();
        music.put("Музыка Будды 1", raw.four1);
        music.put("Музыка Будды 2", raw.four2);
        music.put("Музыка Будды 3", raw.four3);
        music.put("Музыка Будды 4", raw.four4);
        music.put("Музыка Будды 5", raw.four5);
        this.MusicList.add(music);
        music = new HashMap<>();
        music.put("Музыка Домашняя 1", raw.five1);
        music.put("Музыка Домашняя 2", raw.five2);
        music.put("Музыка Домашняя 3", raw.five3);
        music.put("Музыка Домашняя 4", raw.five4);
        music.put("Музыка Домашняя 5", raw.five5);
        this.MusicList.add(music);
        music = new HashMap<>();
        music.put("Музыка Кошки 1", raw.six1);
        music.put("Музыка Кошки 2", raw.six2);
        music.put("Музыка Кошки 3", raw.six3);
        music.put("Музыка Кошки 4", raw.six4);
        music.put("Музыка Кошки 5", raw.six5);
        this.MusicList.add(music);
    }

    private void init_pref() { // Инициализация сохранений
        this.pref = getSharedPreferences("MAMeditationApp", MODE_PRIVATE);
    }
}