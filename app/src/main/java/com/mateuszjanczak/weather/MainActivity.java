package com.mateuszjanczak.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showWeather(View view){
        EditText editText = findViewById(R.id.cityText);
        String city = editText.getText().toString();

        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra("city", city);

        startActivity(intent);
    }
}
