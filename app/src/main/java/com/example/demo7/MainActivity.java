package com.example.demo7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CITY_SELECTION = 1;
    private static final int REQUEST_SETTINGS = 2;

    private String city = "沈阳市";

    private ImageView demo_background_image;
    private ImageView demo_weather_icon;
    private TextView demo_current_weather;
    private Button demo_city_selection;
    private Button demo_today_weather;
    private Button demo_future_weather;
    private Button demo_music_player;
    private Button demo_schedule;
    private Button demo_settings;
    private Button more_information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        demo_background_image = findViewById(R.id.demo_background_image);
        demo_weather_icon = findViewById(R.id.demo_weather_icon);
        demo_current_weather = findViewById(R.id.demo_current_weather);
        demo_city_selection = findViewById(R.id.demo_city_selection);
        demo_today_weather = findViewById(R.id.demo_today_weather);
        demo_future_weather = findViewById(R.id.demo_future_weather);
        demo_music_player = findViewById(R.id.demo_music_player);
        demo_schedule = findViewById(R.id.demo_schedule);
        demo_settings = findViewById(R.id.demo_settings);
        more_information = findViewById(R.id.more_information);

        Intent intent = getIntent();
        String value = intent.getStringExtra("city_name");
        if(value != null){
            city = value;
        }


        updateWeatherForCity(city);


        setButtonIcons();


        setButtonListeners();

    }

    private void setButtonIcons() {
        demo_city_selection.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_city_selection, 0, 0);
        demo_today_weather.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_today_weather, 0, 0);
        demo_future_weather.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_future_weather, 0, 0);
        demo_music_player.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_music_player, 0, 0);
        demo_schedule.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_schedule, 0, 0);
        demo_settings.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_settings, 0, 0);
    }

    private void setButtonListeners() {
        demo_city_selection.setOnClickListener(v -> {
            // 启动城市选择Activity
            Intent intent = new Intent(MainActivity.this, CitySelectionActivity.class);
            startActivityForResult(intent, REQUEST_CITY_SELECTION);
        });
        demo_music_player.setOnClickListener(v -> {
            // 启动音乐播放器Activity
            Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
            intent.putExtra("key", city);
            startActivity(intent);
        });
        demo_settings.setOnClickListener(v -> {
            // 启动设置Activity
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("key", city);
            startActivityForResult(intent, REQUEST_SETTINGS);
        });
        demo_schedule.setOnClickListener(v -> {
            // 启动日程表Activity
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            intent.putExtra("key", city);
            startActivity(intent);
        });
        demo_today_weather.setOnClickListener(v -> {
            // 今日天气Activity
            Intent intent = new Intent(MainActivity.this, TodayWeatherActivity.class);
            intent.putExtra("key", city);
            startActivity(intent);
        });
        more_information.setOnClickListener(v -> {
            // 详细天气Activity
            Intent intent = new Intent(MainActivity.this, MoreActivity.class);
            intent.putExtra("key", city);
            startActivity(intent);
        });
        demo_future_weather.setOnClickListener(v -> {
            // 未来天气Activity
            Intent intent = new Intent(MainActivity.this, FutureWeatherActivity.class);
            intent.putExtra("key", city);
            startActivity(intent);
        });

    }

    private void updateImagesForWeather(String weatherCondition) {
        int backgroundResId;
        int iconResId;

        switch (weatherCondition) {
            case "晴":
                backgroundResId = R.drawable.bg_sunny;
                iconResId = R.drawable.icon_sunny;
                break;
            case "多云":
            case "阴":
            case "霾":
                backgroundResId = R.drawable.bg_cloudy;
                iconResId = R.drawable.icon_cloudy;
                break;
            case "雨":
                backgroundResId = R.drawable.bg_rainy;
                iconResId = R.drawable.icon_rainy;
                break;
            default:
                backgroundResId = R.drawable.bg_default;
                iconResId = R.drawable.icon_default;
                break;
        }

        demo_background_image.setImageDrawable(ContextCompat.getDrawable(this, backgroundResId));
        demo_weather_icon.setImageDrawable(ContextCompat.getDrawable(this, iconResId));
    }

    private void updateWeatherForCity(String city) {
        String cityCode = getCityCode(city);
        if (!cityCode.isEmpty()) {
            String url = "https://route.showapi.com/9-2?showapi_appid=1601350&showapi_sign=9cbb6fa7eb7c4e6c8ca0fefd5c3af2a9&areaCode=" + cityCode;
            new DownloadTask().execute(url);
        }
    }

    private String getCityCode(String city) {
        switch (city) {
            case "霍林郭勒市":
                return "150581";
            case "沈阳市":
                return "210100";
            case "北京市":
                return "110000";
            case "大连市":
                return "210200";
            case "哈尔滨市":
                return "230100";
            default:
                return "";
        }
    }

    class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String defaultUrl = urls[0];
            return fetchUrl(defaultUrl);
        }

        private String fetchUrl(String url) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                updateCurrentWeatherAndImages(result);
            } else {
                demo_current_weather.setText("获取天气失败");
            }
        }
    }

    private void updateCurrentWeatherAndImages(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject body = jsonObject.getJSONObject("showapi_res_body");
            JSONObject now = body.getJSONObject("now");
            JSONObject cityInfo = body.getJSONObject("cityInfo");

            String cityName = cityInfo.getString("c5");
            String currentWeather = cityName + "，" + now.getString("weather") + "，" + now.getString("temperature") + "°C\n" + now.getString("wind_direction") + "，" + now.getString("wind_power");
            demo_current_weather.setText(currentWeather);

            updateImagesForWeather(now.getString("weather"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CITY_SELECTION && resultCode == RESULT_OK && data != null) {
            String selectedCity = data.getStringExtra("selectedCity");
            city = selectedCity;
            updateWeatherForCity(selectedCity);
        } else if (requestCode == REQUEST_SETTINGS && resultCode == RESULT_OK && data != null) {
            // 从设置界面返回，更新字体颜色
            String textColor = data.getStringExtra("textColor");
            if (textColor != null) {
                if (textColor.equals("white")) {
                    demo_current_weather.setTextColor(ContextCompat.getColor(this, R.color.white));
                } else {
                    demo_current_weather.setTextColor(ContextCompat.getColor(this, R.color.black));
                }
            }
        }
    }


}
