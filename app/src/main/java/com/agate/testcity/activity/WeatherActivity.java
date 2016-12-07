package com.agate.testcity.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView pm2d5Text;
    private TextView qltyText;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;
    private SwipeRefreshLayout mSwipRefreshLayout;
    private ImageView weatherIcon;
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
        pm2d5Text = (TextView)findViewById(R.id.pm2d5);
        qltyText = (TextView)findViewById(R.id.qlty);
        mSwipRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        weatherIcon = (ImageView)findViewById(R.id.weathericon);
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
        String address = "https://free-api.heweather.com/v5/weather?city="+cityName+"&key=f49445346ba7479b84820d32806f7998";
        queryWeatherFromServer(address);
    }

    private void queryWeatherFromServer(String address) {
        Httputil.sendHttpRequest2(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if(!TextUtils.isEmpty(response)){
                    Utility.handleAllWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onFinish(Bitmap bitmap) {

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
        weatherDespText.setText(prefs.getString("weather_desp","")+" 丨");
        publishText.setText(prefs.getString("publish_time","")+" 发布");
        currentDateText.setText(prefs.getString("current_date",""));
        pm2d5Text.setText("PM2.5: "+prefs.getString("pm2d5",""));

        if(prefs.getString("qlty","").equals("中度污染".toString())){
            qltyText.setTextColor(Color.YELLOW);
            qltyText.setText("空气质量: "+prefs.getString("qlty",""));
        }else if(prefs.getString("qlty","").equals("重度污染".toString())){

            qltyText.setTextColor(Color.RED);
            qltyText.setText("空气质量: "+prefs.getString("qlty",""));

        }else {
            qltyText.setTextColor(Color.WHITE);
            qltyText.setText("空气质量: "+prefs.getString("qlty",""));
        }
        String weatherCode = prefs.getString("weather_code","");
        Httputil.getHttpBitmap(weatherCode, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //不用实现
            }

            @Override
            public void onFinish(final Bitmap bitmap) {
                if (bitmap != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weatherIcon.setImageBitmap(bitmap);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"图片加载失败！", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onRefresh() {
        publishText.setText("同步中...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String cityName;
                if(getIntent().getStringExtra("city_name") !=   null)
                {
                    cityName = getIntent().getStringExtra("city_name");
                }else {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                    cityName = prefs.getString("city_name", "");
                }
                if(!TextUtils.isEmpty(cityName)){

                    queryWeatherCode(cityName);

                    mSwipRefreshLayout.setRefreshing(false);

                }
            }

        },2000);

    }
}
