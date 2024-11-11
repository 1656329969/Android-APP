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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CitySelectionActivity extends AppCompatActivity {

    private String ip;
    private String[] cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        AppCompatImageButton back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(v -> finish());

        cities = new String[]{"霍林郭勒市", "沈阳市", "北京市", "大连市", "哈尔滨市", "IP属地：" + ip};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        ListView listView = findViewById(R.id.city_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取点击的城市名称
                String selectedCity;
                if(position <= 4) {
                    selectedCity = cities[position];
                }
                else {
                    selectedCity = ip;
                }
                Intent intent = new Intent();
                intent.putExtra("selectedCity", selectedCity);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        new DownloadTask().execute("https://www.ip.cn/api/index?ip&type=0");
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

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
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    String address = jsonObject.getString("address");
                    String[] addressParts = address.split("\\s+");
                    ip = addressParts[2];

                    cities[5] = "IP属地：" + ip;
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CitySelectionActivity.this, android.R.layout.simple_list_item_1, cities);
                    ListView listView = findViewById(R.id.city_list_view);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ip = "无";
                cities[5] = "IP属地：" + ip;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CitySelectionActivity.this, android.R.layout.simple_list_item_1, cities);
                ListView listView = findViewById(R.id.city_list_view);
                listView.setAdapter(adapter);
            }
        }

    }
}
