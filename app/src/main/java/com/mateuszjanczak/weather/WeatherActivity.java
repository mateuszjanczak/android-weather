package com.mateuszjanczak.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
    private TextView cityName;
    private TextView time;
    private TextView temperature;
    private TextView pressure;
    private TextView humidity;
    private TextView temperatureMin;
    private TextView temperatureMax;
    private TextView updateTime;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Intent intent = getIntent();
        city = intent.getStringExtra("city");

        weatherIcon = findViewById(R.id.weatherIcon);
        cityName = findViewById(R.id.cityName);
        time = findViewById(R.id.time);
        temperature = findViewById(R.id.temperature);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        temperatureMin = findViewById(R.id.temperatureMin);
        temperatureMax = findViewById(R.id.temperatureMax);
        updateTime = findViewById(R.id.updateTime);

        checkWeather();
        autoRefresh5Minute();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh();
            }
        });
    }


    void pullToRefresh(){
        checkWeather();
        swipeContainer.setRefreshing(false);
    }

    private void autoRefresh5Minute() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                checkWeather();
                handler.postDelayed(this, 1500000);
            }
        }, 15000);
    }

    void checkWeather() {

        if(!InternetTest.getInstance(this).isOnline()){
            finish();
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BaseUrl).addConverterFactory(GsonConverterFactory.create()).build();
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

                saveData(weatherResponse.name);

                cityName.setText(String.valueOf(weatherResponse.name));
                temperature.setText(String.valueOf((int)weatherResponse.main.temp + "℃"));
                pressure.setText(String.valueOf((int)weatherResponse.main.pressure + " hPa"));
                humidity.setText(String.valueOf((int)weatherResponse.main.humidity + "%"));
                temperatureMin.setText(String.valueOf((int)weatherResponse.main.temp_min + "℃"));
                temperatureMax.setText(String.valueOf((int)weatherResponse.main.temp_max + "℃"));
                updateTime.setText("Last update: " + setTime(weatherResponse.timezone));

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

    private String setTime(int shift) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        TimeZone UTC = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(UTC);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, shift);
        time.setText(dateFormat.format(cal.getTime()));
        return dateFormat.format(cal.getTime());
    }

    private void saveData(String city) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("city", city);
        editor.apply();
    }
}
