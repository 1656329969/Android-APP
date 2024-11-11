package com.example.demo7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.util.List;

public class NowWeatherAdapter extends ArrayAdapter<NowWeather> {
    private Context context;
    private List<NowWeather> weatherList;

    public NowWeatherAdapter(Context context, List<NowWeather> weatherList) {
        super(context, R.layout.list_item_now_weather, weatherList);
        this.context = context;
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_now_weather, parent, false);
        }

        NowWeather nowWeather = weatherList.get(position);

        TextView tvWindDirection = convertView.findViewById(R.id.tvWindDirection);
        TextView tvAqi = convertView.findViewById(R.id.tvAqi);
        ImageView ivWeatherPic = convertView.findViewById(R.id.ivWeatherPic);
        TextView tvWindPower = convertView.findViewById(R.id.tvWindPower);
        TextView tvTemperatureTime = convertView.findViewById(R.id.tvTemperatureTime);
        TextView tvRain = convertView.findViewById(R.id.tvRain);
        TextView tvWeatherCode = convertView.findViewById(R.id.tvWeatherCode);
        TextView tvTemperature = convertView.findViewById(R.id.tvTemperature);
        TextView tvSd = convertView.findViewById(R.id.tvSd);
        TextView tvWeather = convertView.findViewById(R.id.tvWeather);

        tvWindDirection.setText("当前风向： " + nowWeather.getWindDirection());
        tvAqi.setText("空气质量指数： " + nowWeather.getAqi());
        tvWindPower.setText("风力： " + nowWeather.getWindPower());
        tvTemperatureTime.setText("上一次更新时间： " + nowWeather.getTemperatureTime());
        tvRain.setText("降雨强度： " + nowWeather.getRain());
        tvWeatherCode.setText("天气类型： " + nowWeather.getWeatherCode());
        tvTemperature.setText("气温： " + nowWeather.getTemperature());
        tvSd.setText("相对湿度： " + nowWeather.getSd());
        tvWeather.setText("当前天气： " + nowWeather.getWeather());

        new LoadImageTask(ivWeatherPic).execute(nowWeather.getWeatherPic());

        return convertView;
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                // 加载失败还有一个默认图片
                imageView.setImageResource(R.drawable.sun);
            }
        }
    }

}
