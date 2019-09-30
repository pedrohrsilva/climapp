package com.example.climaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.climaapp.model.City;

import java.util.List;

public class CityListActivity extends AppCompatActivity {
    private List<City> cityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        ListView listView = (ListView) findViewById(R.id.list_cities);

        cityList = Singleton.getSingleton().getCityList();
        ArrayAdapter<City> adapter = new ArrayAdapter<City>(this, android.R.layout.simple_list_item_1, cityList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openCityActivity(position);
            }
        });
    }

    private void openCityActivity(int index){
        Singleton.getSingleton().setActiveCity(index);
        Intent intent = new Intent(this, CityActivty.class);
        startActivity(intent);
    }

}
