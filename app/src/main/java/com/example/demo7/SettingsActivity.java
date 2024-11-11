package com.example.demo7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class SettingsActivity extends AppCompatActivity {
    private ListView settings_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        AppCompatImageButton back_button = findViewById(R.id.back_button);
        settings_list_view = findViewById(R.id.settings_list_view);

        String[] settingsOptions = {"切换白色字体", "切换黑色字体"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, settingsOptions);
        settings_list_view.setAdapter(adapter);

        settings_list_view.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    applySettings("white");
                    break;
                case 1:
                    applySettings("black");
                    break;
            }
        });

        back_button.setOnClickListener(v -> finish());
    }

    private void applySettings(String textColor) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("textColor", textColor);
        editor.apply();

        Intent intent = new Intent();
        intent.putExtra("textColor", textColor);
        setResult(RESULT_OK, intent);
        finish();
    }

}
