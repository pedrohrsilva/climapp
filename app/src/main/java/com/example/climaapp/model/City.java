package com.example.climaapp.model;

public class City {
    private final String name;
    private final String weatherDescription;
    private final double max;
    private final double min;

    public City(String name, String weatherDescription, double max, double min) {
        this.name = name;
        this.weatherDescription = weatherDescription;
        this.max = max;
        this.min = min;
    }

    public String getName() {
        return name;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public String toString(){
        return name;
    }
}
