package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.common.hichartsclasses.HIArearange;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HILegend;
import com.highsoft.highcharts.common.hichartsclasses.HILine;
import com.highsoft.highcharts.common.hichartsclasses.HIMarker;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HITooltip;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

public class BlankFragment2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private JSONObject res;

    public BlankFragment2(JSONObject res) {
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
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        ArrayList<Object[]> finalData = new ArrayList<>();
        try {
            JSONObject jsonObject = res;
            JSONObject jsonObject1 = null;
            jsonObject1 = jsonObject.getJSONObject("data");
            JSONArray jsonarray = jsonObject1.getJSONArray("timelines");
            jsonObject1 = (JSONObject) jsonarray.get(0);
            jsonarray = jsonObject1.getJSONArray("intervals");
            for(int i=0;i<12;i++) {
                jsonObject1 = (JSONObject) jsonarray.get(i);
                String startTime = jsonObject1.getString("startTime");
                JSONObject jsonTemp = jsonObject1.getJSONObject("values");
                Double min_temperature = jsonTemp.getDouble("temperatureMin");
                Double max_temperature = jsonTemp.getDouble("temperatureMax");
                Object[] obj={startTime.substring(0,10),min_temperature,max_temperature};
                finalData.add(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HIChartView chartView = view.findViewById(R.id.hc);


        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setType("arearange");
        options.setChart(chart);

        HITitle title = new HITitle();
        title.setText("Temperature variation by day");
        options.setTitle(title);

        HIXAxis xaxis = new HIXAxis();
        xaxis.setType("linear");
        options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));

        HIYAxis yaxis = new HIYAxis();
        yaxis.setTitle(new HITitle());
        yaxis.getTitle().setText("");
        options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

        HITooltip tooltip = new HITooltip();
        tooltip.setShared(true);
        tooltip.setValueSuffix("°F");
        options.setTooltip(tooltip);

        HILegend legend = new HILegend();
        options.setLegend(legend);

        HIArearange arearange = new HIArearange();
        arearange.setType("arearange");
        arearange.setName("Tempreatures");
        arearange.setLineWidth(0);
        arearange.setLinkedTo(":previous");
        LinkedList<HIStop> color = new LinkedList<>();
        color.add(new HIStop((float)0, HIColor.initWithHexValue("ffa702")));
        color.add(new HIStop((float)1, HIColor.initWithHexValue("a8d8f8")));
        HIGradient hi = new HIGradient(0,(float)0.1,0,(float)0.6);
        HIColor temp = HIColor.initWithLinearGradient(hi, color);
        arearange.setFillColor(temp);
        arearange.setFillOpacity(0.3);
        arearange.setZIndex(0);
        arearange.setData(finalData);

        options.setSeries(new ArrayList<>(Arrays.asList(arearange)));

        chartView.setOptions(options);
        return view;
    }
}