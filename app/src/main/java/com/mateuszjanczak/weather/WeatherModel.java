package com.mateuszjanczak.weather;

import com.google.gson.annotations.SerializedName;

public class WeatherModel {
    @SerializedName("main")
    Main main;
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