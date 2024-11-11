package com.example.demo7;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FutureWeatherActivity extends AppCompatActivity {
    private List<String> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_weather);

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        updateWeatherForCity(value);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        ListView listView = findViewById(R.id.city_list_view);
        listView.setAdapter(adapter);

        AppCompatImageButton back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(v -> finish());

    }

    private void updateWeatherForCity(String city) {
        String cityCode = getCityCode(city);
        if (!cityCode.isEmpty()) {
            String url = "https://route.showapi.com/9-2?needMoreDay=1&showapi_appid=1601350&showapi_sign=9cbb6fa7eb7c4e6c8ca0fefd5c3af2a9&areaCode=" + cityCode;
            new FutureWeatherActivity.DownloadTask().execute(url);
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
                    JSONObject showapiResBody = jsonObject.getJSONObject("showapi_res_body");
                    JSONArray dataArray = showapiResBody.names();

                    List<JSONObject> dataList = new ArrayList<>();

                    for (int i = 0; i < dataArray.length(); i++) {
                        String key = dataArray.getString(i);
                        if (!key.equals("remark") && !key.equals("time") && !key.equals("cityInfo") && !key.equals("ret_code")) {
                            dataList.add(showapiResBody.getJSONObject(key));
                        }
                    }

                    Collections.sort(dataList, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject o1, JSONObject o2) {
                            try {
                                return o1.getString("day").compareTo(o2.getString("day"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }
                    });

                    for (JSONObject item : dataList) {
                        try {
                            String message = "日期: " + item.getString("day") + ", 天气: " + item.getString("day_weather") + ", 温度: " + item.getString("day_air_temperature");
                            messages.add(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(FutureWeatherActivity.this, android.R.layout.simple_list_item_1, messages);
                    ListView listView = findViewById(R.id.city_list_view);
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
