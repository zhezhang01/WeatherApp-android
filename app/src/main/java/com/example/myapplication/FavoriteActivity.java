package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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

public class FavoriteActivity extends AppCompatActivity {

    ViewPager2 mviewPager2;
    TabLayout tabLayout;
    Context fav_context;
    LinearLayout fav_loading;
    static FavoriteActivity favoriteActivityInstance;
    public FavoriteActivity() {
        favoriteActivityInstance = this;
    }
    public static FavoriteActivity getInstance() {
        return favoriteActivityInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDHsyWyLVRF6HsMoiADtUaHviXwquJk1Do", Locale.US);
        }
        mviewPager2 = findViewById(R.id.viewpager2);
        tabLayout = findViewById(R.id.tabDots);
        fav_context = this;
        fav_loading = findViewById(R.id.fav_loading);
        fav_loading.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fav_loading.setVisibility(View.GONE);
            }
        }, 1000);
        AllCityApp app = (AllCityApp) getApplication();
        Intent intent = getIntent();
        List<JSONObject> list = app.getCityWeatherList();
        List<String> cityList = app.getCityNameList();
        String city = intent.getStringExtra("cityName");
        if(!city.equals("")){
            cityList.add(city);
            try {
                JSONObject jsonObject = new JSONObject(intent.getStringExtra("jsonData"));
                list.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mviewPager2.setAdapter(new ViewPagerAdapter(this, cityList, list, mviewPager2));
            new TabLayoutMediator(tabLayout, mviewPager2, (tab, position) -> {}).attach();
        }else{
            mviewPager2.setAdapter(new ViewPagerAdapter(this, cityList, list, mviewPager2));
            new TabLayoutMediator(tabLayout, mviewPager2, (tab, position) -> {}).attach();
        }
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
            fav_loading = findViewById(R.id.fav_loading);
            fav_loading.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fav_loading.setVisibility(View.GONE);
                }
            }, 1000);
            callTomorrow(queryData,cityAndstate);
        }
    }

    private void auto_Complete() {
        List<Place.Field> res = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,res).setCountry("US").setTypeFilter(TypeFilter.CITIES).build(this);
        startActivityForResult(intent, 22);
    }

    public TabLayout getTabLayout(){
        return tabLayout;
    }

    public ViewPager2 getMviewPager2(){
        return mviewPager2;
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
                        intent.setClass(FavoriteActivity.this, SearchActivity.class);
                        startActivity(intent);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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