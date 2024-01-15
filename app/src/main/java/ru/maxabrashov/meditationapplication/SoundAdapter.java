package ru.maxabrashov.meditationapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SoundAdapter extends ArrayAdapter<String> {

    private Context context; // Поле основного Activity
    private HashMap<String, Integer> music; // Поле с музыкой

    private MediaPlayer mediaPlayer = new MediaPlayer(); // Поле медиаплеера, для предварительного прослушивания музыки
    private View lastViewSelect = null; // Поле с Последним выбранным элементом
    private SharedPreferences pref; // Поле с Сохранениями
    private int selectID; // Поле выбранного ID

    public SoundAdapter(Context context, HashMap<String, Integer> music, SharedPreferences pref, int selectID) {
        super(context, R.layout.item_list_sound, new ArrayList<>(music.keySet()));
        this.context = context;
        this.music = music;
        this.pref = pref;
        this.selectID = selectID;
    }

    // Запускаем создание элементов внутри SoundAdapter
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_list_sound, parent, false);
        List<String> keys = new ArrayList<>(this.music.keySet()); // Получаем все названия из списка музыки
        // Получаем текст с названием музыки из шаблона item_list_sound и устанавливаем в него название
        TextView textview = view.findViewById(R.id.nameSound);
        textview.setText(keys.get(position));
        // Если элемент является уже выбранной музыкой, то мы устанавливаем его как выбранным
        if (position == this.selectID) {
            view.setBackgroundColor(Color.parseColor("#bbbbbb"));
            this.lastViewSelect = view;
        }
        view.setOnClickListener(v -> {
            try {
                // Если пользователь нажал на эелемент, то перекрашиваем его в выбранный
                if (lastViewSelect != null) {
                    lastViewSelect.setBackgroundColor(0);
                    v.setBackgroundColor(Color.parseColor("#bbbbbb"));
                    lastViewSelect = v;
                }
                else {
                    v.setBackgroundColor(Color.parseColor("#bbbbbb"));
                    lastViewSelect = v;
                }
                // Сохраняем выбор во временный конфиг
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("tempMusic", position);
                edit.apply();
                // Запускаем предварительное проигрывание музыки
                if(mediaPlayer != null) { mediaPlayer.stop(); }
                mediaPlayer = new MediaPlayer();
                // Получаем ссылку на музыку и устанавливаем её в проигрыватель
                Uri mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/"+music.get(keys.get(position)));
                mediaPlayer.setDataSource(context, mediaPath);

                mediaPlayer.prepare(); // Подготаливаем музыку к запуску
                mediaPlayer.setVolume(1f, 1f); // Устанавливаем громкость
                mediaPlayer.setLooping(false); // Убираем цикличность музыки
                mediaPlayer.start(); // Запускаем проигрывание

            } catch (Exception e) { e.printStackTrace(); }
        });
        return view;
    }
}
