package com.example.demo7;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {

    private TextView song_title;
    private ListView song_list_view;
    private TextView song_info;
    private SeekBar seek_bar;
    private Button prev_button;
    private Button play_pause_button;
    private Button next_button;
    private EditText searchEditText;

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private int[] songResIds = {R.raw.one, R.raw.two, R.raw.three};
    private String[] songTitles = {"多远都要在一起", "晴天", "抖音名曲"};
    private String[] songInfos = {"多远都要在一起的歌曲信息", "晴天的歌曲信息", "抖音名曲的歌曲信息"};
    private int currentSongIndex = 1;

    private ArrayList<String> songTitleList = new ArrayList<>();
    private ArrayList<String> filteredSongTitleList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");

        AppCompatImageButton back_button = findViewById(R.id.back_button);
        song_title = findViewById(R.id.song_title);
        song_list_view = findViewById(R.id.song_list_view);
        song_info = findViewById(R.id.song_info);
        seek_bar = findViewById(R.id.seek_bar);
        prev_button = findViewById(R.id.prev_button);
        play_pause_button = findViewById(R.id.play_pause_button);
        next_button = findViewById(R.id.next_button);
        searchEditText = findViewById(R.id.searchEditText);

        for (String songTitle : songTitles) {
            songTitleList.add(songTitle);
        }
        filteredSongTitleList.addAll(songTitleList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredSongTitleList);
        song_list_view.setAdapter(adapter);

        song_list_view.setOnItemClickListener((parent, view, position, id) -> playSong(songTitleList.indexOf(filteredSongTitleList.get(position))));

        back_button.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Intent intent_two = new Intent(MusicPlayerActivity.this, MainActivity.class);
            intent_two.putExtra("city_name", value);
            startActivity(intent_two);
        });

        prev_button.setOnClickListener(v -> playPreviousSong());

        play_pause_button.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                play_pause_button.setText("播放");
            } else {
                mediaPlayer.start();
                play_pause_button.setText("暂停");
            }
        });

        next_button.setOnClickListener(v -> playNextSong());

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterSongList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        playSong(currentSongIndex);
    }

    private void playSong(int index) {
        currentSongIndex = index;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, songResIds[index]);
        song_title.setText(songTitles[index]);
        song_info.setText(songInfos[index]);
        seek_bar.setMax(mediaPlayer.getDuration());

        mediaPlayer.setOnPreparedListener(mp -> {
            mediaPlayer.start();
            play_pause_button.setText("暂停");
            updateSeekBar();
        });

        mediaPlayer.setOnCompletionListener(mp -> playNextSong());
    }

    private void playPreviousSong() {
        currentSongIndex = (currentSongIndex - 1 + songResIds.length) % songResIds.length;
        playSong(currentSongIndex);
    }

    private void playNextSong() {
        currentSongIndex = (currentSongIndex + 1) % songResIds.length;
        playSong(currentSongIndex);
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            seek_bar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private void filterSongList(String query) {
        filteredSongTitleList.clear();
        for (String songTitle : songTitleList) {
            if (songTitle.toLowerCase().contains(query.toLowerCase())) {
                filteredSongTitleList.add(songTitle);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
