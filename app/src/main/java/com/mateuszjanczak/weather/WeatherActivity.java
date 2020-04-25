package com.mateuszjanczak.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = "749561a315b14523a8f5f1ef95e45864";
    private String city;
    private TextView statusText;
    private ImageView weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Intent intent = getIntent();
        city = intent.getStringExtra("city");

        //statusText = findViewById(R.id.statusText);
        weatherIcon = findViewById(R.id.weatherIcon);
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


                String stringBuilder =
                        "Temperature: " +
                        weatherResponse.main.temp +
                        "\n" +
                        "Temperature(Min): " +
                        weatherResponse.main.temp_min +
                        "\n" +
                        "Temperature(Max): " +
                        weatherResponse.main.temp_max +
                        "\n" +
                        "Humidity: " +
                        weatherResponse.main.humidity +
                        "\n" +
                        "Pressure: " +
                        weatherResponse.main.pressure;

                //statusText.setText(stringBuilder);

            }

            @Override
            public void onFailure(@NonNull Call<WeatherModel> call, @NonNull Throwable t) {
                statusText.setText(t.getMessage());

                Intent data = new Intent();
                data.putExtra("code", "Error: " + String.valueOf(t.getMessage()));

                setResult(400, data);
                finish();
            }
        });
    }
}
