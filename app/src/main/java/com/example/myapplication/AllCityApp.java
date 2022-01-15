package com.example.myapplication;

import android.app.Application;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllCityApp extends Application {

    static AllCityApp myAppInstance;
    public AllCityApp() {
        myAppInstance = this;
    }
    public static AllCityApp getInstance() {
        return myAppInstance;
    }
    public List<JSONObject> getCityWeatherList() {
        return cityWeatherList;
    }
    public List<String> getCityNameList() {
        return cityNameList;
    }

    public List<JSONObject> cityWeatherList;
    public List<String> cityNameList;

    @Override
    public void onCreate() {
        super.onCreate();
        cityWeatherList = new ArrayList<>();
        cityNameList = new ArrayList<>();
    }

}
