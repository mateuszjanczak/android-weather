package com.mateuszjanczak.weather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String city;
    private EditText cityText;
    private TextView internetError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityText = findViewById(R.id.cityText);
        internetError = findViewById(R.id.internetError);
        loadData();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                setInternetStatus();
                handler.postDelayed(this, 1000);
            }
        }, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 400){
            assert data != null;
            String response = data.getStringExtra("code");
            Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            loadData();
        }
    }

    private void setInternetStatus() {
        if(!InternetTest.getInstance(this).isOnline()){
            internetError.setVisibility(View.VISIBLE);
            return;
        }
        internetError.setVisibility(View.GONE);
    }

    public void showWeather(View view){

        if(!InternetTest.getInstance(this).isOnline()){
            return;
        }

        EditText editText = findViewById(R.id.cityText);
        city = editText.getText().toString();

        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra("city", city);

        startActivityForResult(intent, 1);
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        city = sharedPreferences.getString("city", "");
        cityText.setText(city);
    }
}
