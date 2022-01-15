package com.example.myapplication;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private List<JSONObject> mData;
    private List<String> cityList;
    private LayoutInflater mInflater;
    private ViewPager2 viewPager2;



    public ViewPagerAdapter(Context context, List<String> cityList, List<JSONObject> data, ViewPager2 viewPager2) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.cityList = cityList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycleview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {
        JSONObject jsonObject = mData.get(position);
        JSONObject jsonObject1 = null;
        String cityName = cityList.get(position);
        try {
            jsonObject1 = jsonObject.getJSONObject("data");
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
            holder.img1.setImageResource(findid(weatherCode));
            holder.card1_tx1.setText(temperature.substring(0,2)+"°F");
            holder.card1_tx2.setText(convertWeatherCode(weatherCode));
            holder.pressure.setText(pressure+"inHG");
            holder.humidity.setText(Double.valueOf(String.valueOf(humidity)).intValue()+"%");
            holder.windSpeed.setText(windSpeed+"mph");
            holder.visibility.setText(visibility+"mi");
            holder.cityName.setText(cityName);
            SimpleAdapter adapter = new SimpleAdapter(holder.context,getData(jsonarray),R.layout.listview_item,
                    new String[]{"date","icon","low_temp","high_temp"},
                    new int[]{R.id.date,R.id.icon,R.id.low_temp,R.id.high_temp});

            holder.listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent =new Intent();
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("jsonData", jsonObject.toString());
                intent.putExtra("cityName", cityName);
                intent.setClass(holder.context, TabbedActivity.class);
                holder.context.startActivity(intent);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) holder.itemView.findViewById(R.id.fab);
        List<String> name = AllCityApp.getInstance().cityNameList;
        if(name.contains(cityName)){
            fab.setImageResource(R.drawable.map_marker_minus);
        }
        if(position==0){
            fab.setVisibility(View.INVISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.contains(cityName)) {
                    //remove this city
                    fab.setImageResource(R.drawable.map_marker_plus);
                    cityList.remove(cityName);
                    mData.remove(jsonObject);
                    Toast.makeText(FavoriteActivity.getInstance(), cityName +
                            " was removed from your favorite", Toast.LENGTH_SHORT).show();

                }
                FavoriteActivity.getInstance().getMviewPager2().setAdapter(new ViewPagerAdapter(FavoriteActivity.getInstance().fav_context, AllCityApp.getInstance().getCityNameList(),
                        AllCityApp.getInstance().getCityWeatherList(),FavoriteActivity.getInstance().getMviewPager2()));
                new TabLayoutMediator(FavoriteActivity.getInstance().getTabLayout(),
                        FavoriteActivity.getInstance().getMviewPager2(), (tab, position) -> {
                }).attach();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pressure;
        TextView card1_tx1;
        TextView card1_tx2;
        ImageView img1;
        LinearLayout linearLayout;
        TextView humidity;
        TextView windSpeed;
        TextView visibility;
        TextView cityName;
        ListView listView;
        Context context;

        ViewHolder(View itemView) {
            super(itemView);
            pressure = itemView.findViewById(R.id.card2_pressure);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            img1 = itemView.findViewById(R.id.img1);
            card1_tx1 = itemView.findViewById(R.id.card1_tx1);
            card1_tx2 = itemView.findViewById(R.id.card1_tx2);
            humidity = itemView.findViewById(R.id.card2_humidity);
            windSpeed = itemView.findViewById(R.id.card2_windSpeed);
            visibility = itemView.findViewById(R.id.card2_visibility);
            cityName = itemView.findViewById(R.id.cityName);
            listView = itemView.findViewById(R.id.list_view);
            context = itemView.getContext();
        }
    }
}