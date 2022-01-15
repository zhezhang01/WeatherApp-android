package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BlankFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private JSONObject res;

    public BlankFragment(JSONObject res) {
        // Required empty public constructor
        this.res=res;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        System.out.println(res);
        try {
                JSONObject jsonObject = res;
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONArray jsonarray = jsonObject1.getJSONArray("timelines");
                jsonObject1 = (JSONObject) jsonarray.get(0);
                jsonarray = jsonObject1.getJSONArray("intervals");
                jsonObject1 = (JSONObject) jsonarray.get(0);
                JSONObject jsonTemp = jsonObject1.getJSONObject("values");
                String temperature = jsonTemp.getString("temperature");
                String weatherCode = jsonTemp.getString("weatherCode");
                String precipitationIntensity = jsonTemp.getString("precipitationIntensity");
                String humidity = jsonTemp.getString("humidity");
                String windSpeed = jsonTemp.getString("windSpeed");
                String visibility = jsonTemp.getString("visibility");
                String pressure = jsonTemp.getString("pressureSeaLevel");
                String cloudCover = jsonTemp.getString("cloudCover");
                String uvIndex = jsonTemp.getString("uvIndex");
                TextView wind = view.findViewById(R.id.card1_weatherWind);
                wind.setText(windSpeed + "mph");
                TextView press = view.findViewById(R.id.card1_gauge);
                press.setText(pressure + "inHG");
                TextView rain = view.findViewById(R.id.card1_weatherPouring);
                rain.setText(Double.valueOf(Double.valueOf(String.valueOf(precipitationIntensity))*100).intValue()+ "%");
                TextView temp = view.findViewById(R.id.card1_thermometer);
                temp.setText(temperature.substring(0,2) + "°F");
                ImageView img1 = view.findViewById(R.id.img1_5);
                img1.setImageDrawable(getResources().getDrawable(findid(weatherCode)));
                TextView wea_status = view.findViewById(R.id.card1_status);
                wea_status.setText(convertWeatherCode(weatherCode));
                TextView hum = view.findViewById(R.id.card1_waterPercent);
                hum.setText(Double.valueOf(String.valueOf(humidity)).intValue()+"%");
                TextView vis = view.findViewById(R.id.card1_eyeOutline);
                vis.setText(visibility + "mi");
                TextView cc = view.findViewById(R.id.card1_WeatherFog);
                cc.setText(cloudCover + "%");
                TextView oz = view.findViewById(R.id.card1_Earth);
                oz.setText(uvIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
    private int findid(String weatherCode) {
        if(weatherCode.equals("1000")){
            return R.drawable.ic_clear_day;
        }else if(weatherCode.equals("1100")){
            return R.drawable.ic_mostly_clear_day;
        }else if(weatherCode.equals("1101")){
            return R.drawable.ic_partly_cloudy_day;
        }else if(weatherCode.equals("1102")){
            return R.drawable.ic_mostly_cloudy;
        }else if(weatherCode.equals("1001")){
            return R.drawable.ic_cloudy;
        }else if(weatherCode.equals("2000")){
            return R.drawable.ic_fog;
        }else if(weatherCode.equals("2100")){
            return R.drawable.ic_fog_light;
        }else if(weatherCode.equals("8000")){
            return R.drawable.ic_tstorm;
        }else if(weatherCode.equals("5001")){
            return R.drawable.ic_flurries;
        }else if(weatherCode.equals("5100")){
            return R.drawable.ic_snow_light;
        }else if(weatherCode.equals("5000")){
            return R.drawable.ic_snow;
        }else if(weatherCode.equals("5101")){
            return R.drawable.ic_snow_heavy;
        }else if(weatherCode.equals("7102")){
            return R.drawable.ic_ice_pellets_light;
        }else if(weatherCode.equals("7000")){
            return R.drawable.ic_ice_pellets;
        }else if(weatherCode.equals("7101")){
            return R.drawable.ic_ice_pellets_heavy;
        }else if(weatherCode.equals("4000")){
            return R.drawable.ic_drizzle;
        }else if(weatherCode.equals("6000")){
            return R.drawable.ic_freezing_drizzle;
        }else if(weatherCode.equals("6200")){
            return R.drawable.ic_freezing_rain_light;
        }else if(weatherCode.equals("6001")){
            return R.drawable.ic_freezing_rain;
        }else if(weatherCode.equals("6201")){
            return R.drawable.ic_freezing_rain_heavy;
        }else if(weatherCode.equals("4200")){
            return R.drawable.ic_rain_light;
        }else if(weatherCode.equals("4001")){
            return R.drawable.ic_rain;
        }else{
            return R.drawable.ic_rain_heavy;
        }
    }

    private String convertWeatherCode(String weatherCode) {
        if(weatherCode.equals("1000")){
            return "Clear";
        }else if(weatherCode.equals("1100")){
            return "Mostly Clear";
        }else if(weatherCode.equals("1101")){
            return "Partly Cloudy";
        }else if(weatherCode.equals("1102")){
            return "Mostly Cloudy";
        }else if(weatherCode.equals("1001")){
            return "Cloudy";
        }else if(weatherCode.equals("2000")){
            return "Fog";
        }else if(weatherCode.equals("2100")){
            return "Light Fog";
        }else if(weatherCode.equals("8000")){
            return "Thunderstorm";
        }else if(weatherCode.equals("5001")){
            return "Flurries";
        }else if(weatherCode.equals("5100")){
            return "Light Snow";
        }else if(weatherCode.equals("5000")){
            return "Snow";
        }else if(weatherCode.equals("5101")){
            return "Heavy Snow";
        }else if(weatherCode.equals("7102")){
            return "Light Ice Pellets";
        }else if(weatherCode.equals("7000")){
            return "Ice Pellets";
        }else if(weatherCode.equals("7101")){
            return "Heavy Ice Pellets";
        }else if(weatherCode.equals("4000")){
            return "Drizzle";
        }else if(weatherCode.equals("6000")){
            return "Freezing Drizzle";
        }else if(weatherCode.equals("6200")){
            return "Light Freezing Rain";
        }else if(weatherCode.equals("6001")){
            return "Freezing Rain";
        }else if(weatherCode.equals("6201")){
            return "Heavy Freezing Rain";
        }else if(weatherCode.equals("4200")){
            return "Light Rain";
        }else if(weatherCode.equals("4001")){
            return "Rain";
        }else if(weatherCode.equals("3000")){
            return "Light Wind";
        }else if(weatherCode.equals("3001")){
            return "Wind";
        }else if(weatherCode.equals("3002")){
            return "Strong Wind";
        }else{
            return "Heavy Rain";
        }
    }
}