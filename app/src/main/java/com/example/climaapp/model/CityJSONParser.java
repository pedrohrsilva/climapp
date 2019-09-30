package com.example.climaapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CityJSONParser {


    public static List<City> getCityList(String jsonText) throws JSONException {
        List<City> cityList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonText);
        JSONArray list = jsonObject.getJSONArray("list");
        for(int i=0; i<list.length(); i++){
            JSONObject cityJ = list.getJSONObject(i);
            String name = cityJ.getString("name");
            JSONObject main = cityJ.getJSONObject("main");
            String minString = main.getString("temp_min");
            String maxString = main.getString("temp_max");
            JSONArray weatherArray = cityJ.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String description = "";
            if(weather != null){
                description = weather.getString("description");
            }
            cityList.add(new City(name, description, Double.parseDouble(maxString), Double.parseDouble(minString)));
        }
        return cityList;
    }


}

