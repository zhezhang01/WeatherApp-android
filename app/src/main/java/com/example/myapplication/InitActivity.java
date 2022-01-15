package com.example.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class InitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url_s = "https://ipinfo.io/?token=253cf24beef722";
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
                        JSONObject jsonObject = new JSONObject(text);
                        String data=jsonObject.getString("loc");
                        String cityName=jsonObject.getString("city")+","+jsonObject.getString("region");
                        String[] as = data.split(",");
                        float lat=Float.parseFloat(as[0]);
                        float lng=Float.parseFloat(as[1]);
                        callTomorrow(lat,lng,cityName);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void callTomorrow(float lat, float lng, String cityName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url_s = "https://zhangzhe04.wl.r.appspot.com/getAPIResponse"+"/?location="+lat+","+lng;
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
                        JSONObject jsonObject = new JSONObject(text);
//                        System.out.println(jsonObject);
                        Intent intent = new Intent(InitActivity.this,FavoriteActivity.class);
                        intent.putExtra("jsonData", text);
                        intent.putExtra("cityName", cityName);
                        startActivity(intent);
                        finish();
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
