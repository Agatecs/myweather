package com.agate.testcity.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agate.testcity.R;
import com.agate.testcity.service.AutoUpdateService;
import com.agate.testcity.util.HttpCallbackListener;
import com.agate.testcity.util.Httputil;
import com.agate.testcity.util.Utility;

import org.w3c.dom.Text;

import java.io.BufferedReader;

/**
 * Created by Agate on 2016/11/30.
 */

public class WeatherActivity extends Activity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;
    private SwipeRefreshLayout mSwipRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        publishText = (TextView)findViewById(R.id.publish_text);
        weatherDespText = (TextView)findViewById(R.id.weather_desp);
        temp1Text = (TextView)findViewById(R.id.temp1);
        currentDateText = (TextView)findViewById(R.id.current_date);
        switchCity = (Button)findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        mSwipRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        mSwipRefreshLayout.setOnRefreshListener(this);
        String cityName = getIntent().getStringExtra("city_name");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        if(!TextUtils.isEmpty(cityName)){
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.VISIBLE);
            cityNameText.setVisibility(View.VISIBLE);
            queryWeatherCode(cityName);
        }else{
            showWeather();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String cityName = prefs.getString("city_name","");
                if(!TextUtils.isEmpty(cityName)){
                    queryWeatherCode(cityName);
                }
                break;
            default:
                break;
        }
    }

    private void queryWeatherCode(String cityName){
        String address = "https://free-api.heweather.com/v5/now?city="+cityName+"&key=f49445346ba7479b84820d32806f7998";
        queryWeatherFromServer(address);
    }

    private void queryWeatherFromServer(String address) {
        Httputil.sendHttpRequest2(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if(!TextUtils.isEmpty(response)){
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1","")+"℃");
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishText.setText(prefs.getString("publish_time","")+" 发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String cityName = prefs.getString("city_name","");
                if(!TextUtils.isEmpty(cityName)){
                    queryWeatherCode(cityName);
                    mSwipRefreshLayout.setRefreshing(false);

                }
            }

        },2000);

    }
}
