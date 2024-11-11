package com.example.demo7;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TodayWeatherActivity extends AppCompatActivity {
    private LineChart lineChartTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_weather);

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        updateWeatherForCity(value);

        AppCompatImageButton back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(v -> finish());

        lineChartTemperature = findViewById(R.id.lineChartTemperature);

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

    private void updateWeatherForCity(String city) {
        String cityCode = getCityCode(city);
        if (!cityCode.isEmpty()) {
            String url = "https://route.showapi.com/9-2?needHourData=1&showapi_appid=1601350&showapi_sign=9cbb6fa7eb7c4e6c8ca0fefd5c3af2a9&areaCode=" + cityCode;
            new TodayWeatherActivity.DownloadTask().execute(url);
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
            }
        }
    }

    class IntegerValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    }

    private void updateCurrentWeatherAndImages(String jsonData) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        StringBuilder weatherInfoBuilder = new StringBuilder();
        StringBuilder weatherInfoBuildertwo = new StringBuilder();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject body = jsonObject.getJSONObject("showapi_res_body");
            JSONArray dataList = body.getJSONArray("hourDataList");


            if(dataList.length() > 24) {
                for (int i = 0; i <= 24; i++) {
                    JSONObject hourData = dataList.getJSONObject(i);
                    String temperature = hourData.getString("temperature");
                    String weather = hourData.getString("weather");

                    entries.add(new Entry(i, Float.parseFloat(temperature)));
                    labels.add(hourData.getString("temperature_time"));

                    weatherInfoBuilder.append(hourData.getString("temperature_time"))
                            .append("：")
                            .append(weather)
                            .append("\n");
                }
                for (int i = 25; i < dataList.length(); i++) {
                    JSONObject hourData = dataList.getJSONObject(i);
                    String temperature = hourData.getString("temperature");
                    String weather = hourData.getString("weather");

                    entries.add(new Entry(i, Float.parseFloat(temperature)));
                    labels.add(hourData.getString("temperature_time"));

                    weatherInfoBuildertwo.append(hourData.getString("temperature_time"))
                            .append("：")
                            .append(weather)
                            .append("\n");
                }
            }else{
                for (int i = 0; i <= dataList.length(); i++) {
                    JSONObject hourData = dataList.getJSONObject(i);
                    String temperature = hourData.getString("temperature");
                    String weather = hourData.getString("weather");

                    entries.add(new Entry(i, Float.parseFloat(temperature)));
                    labels.add(hourData.getString("temperature_time"));

                    weatherInfoBuilder.append(hourData.getString("temperature_time"))
                            .append("：")
                            .append(weather)
                            .append("\n");
                }
            }

            LineDataSet dataSet = new LineDataSet(entries, "Temperature");
            dataSet.setColor(Color.BLUE);
            dataSet.setValueTextColor(Color.RED);

            dataSet.setValueFormatter(new IntegerValueFormatter());


            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            LineData lineData = new LineData(dataSets);

            lineChartTemperature.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels));

            YAxis leftAxis = lineChartTemperature.getAxisLeft();
            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(40f);
            leftAxis.setGranularity(5f);
            leftAxis.setLabelCount(9);
            leftAxis.setDrawGridLines(false);

            XAxis bottomAxis = lineChartTemperature.getXAxis();
            bottomAxis.setDrawGridLines(false);

            Description description = new Description();
            description.setText("时间（小时）");
            lineChartTemperature.setDescription(description);

            lineChartTemperature.setData(lineData);

            lineChartTemperature.invalidate();

            TextView tvHourlyWeatherInfo = findViewById(R.id.tvHourlyWeatherInfo);
            TextView tvHourlyWeatherInfotwo = findViewById(R.id.tvHourlyWeatherInfotwo);
            tvHourlyWeatherInfo.setText(weatherInfoBuilder.toString());
            tvHourlyWeatherInfotwo.setText(weatherInfoBuildertwo.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
