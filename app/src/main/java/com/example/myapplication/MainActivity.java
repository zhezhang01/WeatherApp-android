package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.myapplication.ui.main.SectionsPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDHsyWyLVRF6HsMoiADtUaHviXwquJk1Do", Locale.US);
        }
        String cityName = null;
        Intent intent = getIntent();
        try {
            cityName = intent.getStringExtra("cityName");
            TextView city = (TextView)this.findViewById(R.id.cityName);
            city.setText(cityName);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View card1 = findViewById(R.id.linearLayout);
        card1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try {
                    String cityTemp = intent.getStringExtra("cityName");
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("jsonData"));
                    intent.putExtra("jsonData", jsonObject.toString());
                    intent.putExtra("cityName", cityTemp);
                    intent.setClass(MainActivity.this,TabbedActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_button:
                auto_Complete();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 22 && resultCode==RESULT_OK){
            Place city = Autocomplete.getPlaceFromIntent(data);
            String cityAndstate = city.getAddress();
            String[] as = cityAndstate.split(",");
            cityAndstate = as[0]+","+stateConvert(as[1]);
            LatLng queryData = city.getLatLng();
            callTomorrow(queryData,cityAndstate);
        }
    }

    private void auto_Complete() {
        List<Place.Field> res = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,res).setCountry("US").setTypeFilter(TypeFilter.CITIES).build(this);
        startActivityForResult(intent, 22);
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

    private void callTomorrow(LatLng queryData, String cityAndstate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url_s = "https://zhangzhe04.wl.r.appspot.com/getAPIResponse"+"/?location="+queryData.latitude+","+queryData.longitude;
                    URL url = new URL(url_s);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    //Connect
                    conn.connect();
                    //Receive data
                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader input = new InputStreamReader(inputStream);
                    BufferedReader buffer = new BufferedReader(input);
                    if(conn.getResponseCode() == 200){
                        String inputLine;
                        StringBuffer resultData  = new StringBuffer();
                        while((inputLine = buffer.readLine())!= null){
                            resultData.append(inputLine);
                        }
                        String text = resultData.toString();
//                        System.out.println(jsonObject);
                        Intent intent = new Intent();
                        intent.putExtra("cityAndstate", cityAndstate);
                        intent.putExtra("jsonData", text);
                        intent.setClass(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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

    private String stateConvert(String state){
        HashMap<String,String> map = new HashMap<>();
        map.put("AL", "Alabama");
        map.put("AK", "Alaska");
        map.put("AS", "American Samoa");
        map.put("AZ", "Arizona");
        map.put("AR", "Arkansas");
        map.put("CA", "California");
        map.put("CO", "Colorado");
        map.put("CT", "Connecticut");
        map.put("DE", "Delaware");
        map.put("DC", "District of Columbia");
        map.put("FL", "Florida");
        map.put("GA", "Georgia");
        map.put("GU", "Guam");
        map.put("HI", "Hawaii");
        map.put("ID", "Idaho");
        map.put("IL", "Illinois");
        map.put("IN", "Indiana");
        map.put("IA", "Iowa");
        map.put("KS", "Kansas");
        map.put("KY", "Kentucky");
        map.put("LA", "Louisiana");
        map.put("ME", "Maine");
        map.put("MD", "Maryland");
        map.put("MA", "Massachusetts");
        map.put("MI", "Michigan");
        map.put("MN", "Minnesota");
        map.put("MS", "Mississippi");
        map.put("MO", "Missouri");
        map.put("MT", "Montana");
        map.put("NE", "Nebraska");
        map.put("NV", "Nevada");
        map.put("NH", "New Hampshire");
        map.put("NJ", "New Jersey");
        map.put("NM", "New Mexico");
        map.put("NY", "New York");
        map.put("NC", "North Carolina");
        map.put("ND", "North Dakota");
        map.put("OH", "Ohio");
        map.put("OK", "Oklahoma");
        map.put("OR", "Oregon");
        map.put("PA", "Pennsylvania");
        map.put("PR", "Puerto Rico");
        map.put("RI", "Rhode Island");
        map.put("SC", "South Carolina");
        map.put("SD", "South Dakota");
        map.put("TN", "Tennessee");
        map.put("TX", "Texas");
        map.put("UT", "Utah");
        map.put("VT", "Vermont");
        map.put("VI", "Virgin Islands");
        map.put("VA", "Virginia");
        map.put("WA", "Washington");
        map.put("WV", "West Virginia");
        map.put("WI", "Wisconsin");
        map.put("WY", "Wyoming");
        return map.get(state.trim());
    }

}