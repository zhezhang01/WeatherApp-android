package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivitySearchBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        try {
            String cityAndstate = intent.getStringExtra("cityAndstate");
            TextView city = (TextView)this.findViewById(R.id.cityName);
            city.setText(cityAndstate);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(cityAndstate);
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("jsonData"));
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            JSONArray jsonarray = jsonObject1.getJSONArray("timelines");
            jsonObject1= (JSONObject) jsonarray.get(0);
            jsonarray=jsonObject1.getJSONArray("intervals");
            jsonObject1= (JSONObject) jsonarray.get(0);
            JSONObject jsonTemp=jsonObject1.getJSONObject("values");
            String temperature = jsonTemp.getString("temperature");
            String weatherCode = jsonTemp.getString("weatherCode");
            String humidity = jsonTemp.getString("humidity");
            String windSpeed = jsonTemp.getString("windSpeed");
            String visibility = jsonTemp.getString("visibility");
            String pressure = jsonTemp.getString("pressureSeaLevel");
            ImageView img1 = (ImageView) this.findViewById(R.id.img1);
            img1.setImageDrawable(getResources().getDrawable(findid(weatherCode)));
            TextView card1_tx1 = (TextView)this.findViewById(R.id.card1_tx1);
            card1_tx1.setText(temperature.substring(0,2)+"°F");
            TextView card1_tx2 = (TextView)this.findViewById(R.id.card1_tx2);
            card1_tx2.setText(convertWeatherCode(weatherCode));
            TextView card2_humidity = (TextView)this.findViewById(R.id.card2_humidity);
            card2_humidity.setText(Double.valueOf(String.valueOf(humidity)).intValue()+"%");
            TextView card1_windSpeed = (TextView)this.findViewById(R.id.card2_windSpeed);
            card1_windSpeed.setText(windSpeed+"mph");
            TextView card2_visibility = (TextView)this.findViewById(R.id.card2_visibility);
            card2_visibility.setText(visibility+"mi");
            TextView card2_pressure = (TextView)this.findViewById(R.id.card2_pressure);
            card2_pressure.setText(pressure+"inHG");
            //set third card
            SimpleAdapter adapter = new SimpleAdapter(this,getData(jsonarray),R.layout.listview_item,
                    new String[]{"date","icon","low_temp","high_temp"},
                    new int[]{R.id.date,R.id.icon,R.id.low_temp,R.id.high_temp});

            ListView listView = (ListView) findViewById(R.id.list_view);
            listView.setAdapter(adapter);
            listenOnFab1(cityAndstate, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View card1 = findViewById(R.id.linearLayout1);
        card1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try {
                    String cityTemp = intent.getStringExtra("cityAndstate");
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("jsonData"));
                    intent.putExtra("jsonData", jsonObject.toString());
                    intent.putExtra("cityName", cityTemp);
                    intent.setClass(SearchActivity.this,TabbedActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void listenOnFab1(String cityAndstate, JSONObject jsonObject) {
        FloatingActionButton fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        List<String> name = AllCityApp.getInstance().cityNameList;
        if(name.contains(cityAndstate)){
            fab1.setImageResource(R.drawable.map_marker_minus);
        }else{
            fab1.setImageResource(R.drawable.map_marker_plus);
        }
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> name = AllCityApp.getInstance().cityNameList;
                List<JSONObject> cityWeatherlist = AllCityApp.getInstance().cityWeatherList;
                if (name.contains(cityAndstate)) {
                    //remove
                    fab1.setImageResource(R.drawable.map_marker_plus);
                    name.remove(cityAndstate);
                    cityWeatherlist.remove(jsonObject);
                    Toast.makeText(FavoriteActivity.getInstance(), cityAndstate +
                            " was removed from your favorite", Toast.LENGTH_SHORT).show();
                }else{
                    //add
                    name.add(cityAndstate);
                    cityWeatherlist.add(jsonObject);
                    fab1.setImageResource(R.drawable.map_marker_minus);
                    Toast.makeText(FavoriteActivity.getInstance(), cityAndstate +
                            " was added to your favorite", Toast.LENGTH_SHORT).show();
                }
                FavoriteActivity.getInstance().getMviewPager2().setAdapter(new ViewPagerAdapter(FavoriteActivity.getInstance().fav_context, name,
                        cityWeatherlist,FavoriteActivity.getInstance().getMviewPager2()));
                new TabLayoutMediator(FavoriteActivity.getInstance().getTabLayout(),
                        FavoriteActivity.getInstance().getMviewPager2(), (tab, position) -> {
                }).attach();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FavoriteActivity.getInstance().getMviewPager2().setAdapter(new ViewPagerAdapter(FavoriteActivity.getInstance().fav_context, AllCityApp.getInstance().getCityNameList(),
                        AllCityApp.getInstance().getCityWeatherList(),FavoriteActivity.getInstance().getMviewPager2()));
                new TabLayoutMediator(FavoriteActivity.getInstance().getTabLayout(),
                        FavoriteActivity.getInstance().getMviewPager2(), (tab, position) -> {
                }).attach();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Map<String, Object>> getData(JSONArray jsonarray) throws JSONException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 7; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            JSONObject jsonObject1= (JSONObject) jsonarray.get(i);
            String startTime=jsonObject1.getString("startTime");
            JSONObject jsonTemp=jsonObject1.getJSONObject("values");
            String weatherCode = jsonTemp.getString("weatherCode");
            String min_temperature = jsonTemp.getString("temperatureMin");
            String max_temperature = jsonTemp.getString("temperatureMax");
            map.put("date", startTime.substring(0,10));
            map.put("icon", findid(weatherCode));
            map.put("low_temp", min_temperature.substring(0,2));
            map.put("high_temp", max_temperature.substring(0,2));
            list.add(map);
        }
        return list;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(SearchActivity.this,FavoriteActivity.class);
        intent.putExtra("jsonData", String.valueOf(new JSONObject()));
        intent.putExtra("cityName", "");
        startActivity(intent);
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