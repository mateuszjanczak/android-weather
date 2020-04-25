package com.mateuszjanczak.weather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String city;
    private EditText cityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityText = findViewById(R.id.cityText);
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 400){
            assert data != null;
            String response = data.getStringExtra("code");
            Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
        }
    }

    public void showWeather(View view){
        EditText editText = findViewById(R.id.cityText);
        city = editText.getText().toString();

        saveData(city);

        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra("city", city);

        startActivityForResult(intent, 1);
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        city = sharedPreferences.getString("city", "Pusto");
        cityText.setText(city);
    }

    private void saveData(String city) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("city", city);
        editor.apply();
    }
}
