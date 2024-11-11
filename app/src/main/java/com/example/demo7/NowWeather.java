package com.example.demo7;

public class NowWeather {
    private String windDirection;
    private String aqi;
    private String weatherPic;
    private String windPower;
    private String temperatureTime;
    private String rain;
    private String weatherCode;
    private String temperature;
    private String sd;
    private String weather;

    public NowWeather(String windDirection, String aqi, String weatherPic, String windPower, String temperatureTime, String rain, String weatherCode, String temperature, String sd, String weather) {
        this.windDirection = windDirection;
        this.aqi = aqi;
        this.weatherPic = weatherPic;
        this.windPower = windPower;
        this.temperatureTime = temperatureTime;
        this.rain = rain;
        this.weatherCode = weatherCode;
        this.temperature = temperature;
        this.sd = sd;
        this.weather = weather;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getWeatherPic() {
        return weatherPic;
    }

    public void setWeatherPic(String weatherPic) {
        this.weatherPic = weatherPic;
    }

    public String getWindPower() {
        return windPower;
    }

    public void setWindPower(String windPower) {
        this.windPower = windPower;
    }

    public String getTemperatureTime() {
        return temperatureTime;
    }

    public void setTemperatureTime(String temperatureTime) {
        this.temperatureTime = temperatureTime;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
