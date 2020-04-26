package com.mateuszjanczak.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = "749561a315b14523a8f5f1ef95e45864";
    private String city;

    private ImageView weatherIcon;

    private TextView time;

    private TextView cityName;
    private TextView temperature;
    private TextView pressure;
    private TextView humidity;
    private TextView temperatureMin;
    private TextView temperatureMax;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Intent intent = getIntent();
        city = intent.getStringExtra("city");

        time = findViewById(R.id.time);
        cityName = findViewById(R.id.cityName);
        weatherIcon = findViewById(R.id.weatherIcon);
        temperature = findViewById(R.id.temperature);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        temperatureMin = findViewById(R.id.temperatureMin);
        temperatureMax = findViewById(R.id.temperatureMax);

        checkWeather();
    }

    void checkWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherModel> call = service.getCurrentWeatherData(city, AppId, "metric");
        call.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(@NonNull Call<WeatherModel> call, @NonNull Response<WeatherModel> response) {

                if(!response.isSuccessful()){
                    Intent data = new Intent();
                    data.putExtra("code", "Error: " + String.valueOf(response.message()));

                    if(response.code() == 404) {
                        data.putExtra("code", "City not found");
                    }

                    setResult(400, data);
                    finish();
                    return;
                }

                WeatherModel weatherResponse = response.body();
                assert weatherResponse != null;


                String icon = weatherResponse.weather.get(0).icon;
                String iconUrl = "http://fastup.pl/data/mj/WeatherIcons/" + icon + ".png";

                Picasso.get().setLoggingEnabled(true);
                Picasso.get().load(iconUrl).into(weatherIcon);

                cityName.setText(String.valueOf(weatherResponse.name));
                temperature.setText(String.valueOf((int)weatherResponse.main.temp + "℃"));
                pressure.setText(String.valueOf((int)weatherResponse.main.pressure + " hPa"));
                humidity.setText(String.valueOf((int)weatherResponse.main.humidity + "%"));
                temperatureMin.setText(String.valueOf((int)weatherResponse.main.temp_min + "℃"));
                temperatureMax.setText(String.valueOf((int)weatherResponse.main.temp_max + "℃"));
                setTime(weatherResponse.timezone);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        setTime(weatherResponse.timezone);
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);


            }

            @Override
            public void onFailure(@NonNull Call<WeatherModel> call, @NonNull Throwable t) {
                Intent data = new Intent();
                data.putExtra("code", "Error: " + String.valueOf(t.getMessage()));

                setResult(400, data);
                finish();
            }
        });
    }

    private void setTime(int shift) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        TimeZone UTC = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(UTC);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, shift);
        time.setText(dateFormat.format(cal.getTime()));
    }
}
