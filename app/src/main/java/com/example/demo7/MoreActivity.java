package com.example.demo7;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoreActivity extends AppCompatActivity {

    private ListView listView;
    private NowWeatherAdapter weatherAdapter;
    private List<NowWeather> weatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        updateWeatherForCity(value);

        AppCompatImageButton back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(v -> finish());

        listView = findViewById(R.id.listView);
        weatherList = new ArrayList<>();

        weatherAdapter = new NowWeatherAdapter(this, weatherList);
        listView.setAdapter(weatherAdapter);
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
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject resBody = jsonObject.getJSONObject("showapi_res_body");
                    JSONObject now = resBody.getJSONObject("now");

                    String windDirection = now.getString("wind_direction");
                    String aqi = now.getString("aqi");
                    String weatherPic = now.getString("weather_pic");
                    String windPower = now.getString("wind_power");
                    String temperatureTime = now.getString("temperature_time");
                    String rain = now.getString("rain");
                    String weatherCode = now.getString("weather_code");
                    String temperature = now.getString("temperature");
                    String sd = now.getString("sd");
                    String weather = now.getString("weather");

                    NowWeather nowWeather = new NowWeather(windDirection, aqi, weatherPic, windPower, temperatureTime, rain, weatherCode, temperature, sd, weather);
                    weatherList.add(nowWeather);

                    weatherAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
