package com.mateuszjanczak.weather;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherModel {
    @SerializedName("weather")
    public ArrayList<Weather> weather = new ArrayList<Weather>();
    @SerializedName("main")
    Main main;
    @SerializedName("name")
    String name;
}

class Weather {
    @SerializedName("id")
    int id;
    @SerializedName("main")
    String main;
    @SerializedName("description")
    String description;
    @SerializedName("icon")
    String icon;
}

class Main {
    @SerializedName("temp")
    double temp;
    @SerializedName("humidity")
    double humidity;
    @SerializedName("pressure")
    double pressure;
    @SerializedName("temp_min")
    double temp_min;
    @SerializedName("temp_max")
    double temp_max;
}