package com.example.climaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.climaapp.model.City;

public class CityActivty extends AppCompatActivity {
    private City city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_activty);
        city = Singleton.getSingleton().getActiveCity();
        TextView tvName = (TextView) findViewById(R.id.city_name);
        TextView tvMin = (TextView) findViewById(R.id.city_min);
        TextView tvMax = (TextView) findViewById(R.id.city_max);
        TextView tvDesc = (TextView) findViewById(R.id.city_description);
        tvName.setText(city.getName());
        tvMin.setText("MIN: "+city.getMin());
        tvMax.setText("MAX: " +city.getMax());
        tvDesc.setText(city.getWeatherDescription());
    }
}
