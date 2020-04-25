package com.mateuszjanczak.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather")
    Call<WeatherModel> getCurrentWeatherData(@Query("q") String city, @Query("APPID") String app_id, @Query("units") String units);
}
