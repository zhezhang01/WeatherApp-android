package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.UserHandle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.ui.main.SectionsPagerAdapter;
import com.example.myapplication.databinding.ActivityTabbedBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TabbedActivity extends AppCompatActivity {

    private ActivityTabbedBinding binding;
    private TabLayout tabLayout;
    private int i=0;
    private int[] tabIcons = {
            R.drawable.calendar_today,
            R.drawable.trending_up,
            R.drawable.thermometer_low
    };

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTabbedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        JSONObject jsonObject=null;
        Intent intent = getIntent();
        try {
            jsonObject = new JSONObject(intent.getStringExtra("jsonData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cityName = intent.getStringExtra("cityName");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(cityName);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),jsonObject);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        setupTabIcons();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.twitter:
                try {
                    Intent intent = getIntent();
                    String cityName = intent.getStringExtra("cityName");
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("jsonData"));
                    JSONObject jsonObject1 = null;
                    jsonObject1 = jsonObject.getJSONObject("data");
                    JSONArray jsonarray = jsonObject1.getJSONArray("timelines");
                    jsonObject1= (JSONObject) jsonarray.get(0);
                    jsonarray=jsonObject1.getJSONArray("intervals");
                    jsonObject1= (JSONObject) jsonarray.get(0);
                    JSONObject jsonTemp=jsonObject1.getJSONObject("values");
                    String temperature = jsonTemp.getString("temperature");
                    String twitterUri = "https://twitter.com/intent/tweet?text=";
                    String marketUri = "Check+out+"+cityName+"+!+It+is+"+temperature.substring(0,2)+"+°F!%23CSCI571WeatherForecast";
                    Intent shareOnFacebookIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(twitterUri + marketUri));
                    startActivity(shareOnFacebookIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(TabbedActivity.this,FavoriteActivity.class);
        intent.putExtra("jsonData", String.valueOf(new JSONObject()));
        intent.putExtra("cityName", "");
        startActivity(intent);
    }

    private void setupTabIcons() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
    }
}