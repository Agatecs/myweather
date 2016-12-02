package com.agate.testcity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.agate.testcity.R;
import com.agate.testcity.db.MyWeatherDB;
import com.agate.testcity.model.City;
import com.agate.testcity.model.Province;
import com.agate.testcity.util.HttpCallbackListener;
import com.agate.testcity.util.Httputil;
import com.agate.testcity.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agate on 2016/11/29.
 */

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY =2;
    private int currentLevel;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private List<Province> provinceList;
    private List<City> cityList;
    private Province selectedProvince;
    private String address;
    private List<Province> provinceList2 = new ArrayList<Province>();
    private List<City> cityList2;
    private Boolean isFromWeatherActivity;
    private MyWeatherDB myWeatherDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",false);
        myWeatherDB = MyWeatherDB.getInstance(this);
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(prefs.getBoolean("city_selected",false) && !isFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList2.get(position);
                    queryCitiesFromServer(selectedProvince.getProvinceCode());
                    //Toast.makeText(ChooseAreaActivity.this,selectedProvince.getProvinceName(),Toast.LENGTH_SHORT);
                }else if(currentLevel == LEVEL_CITY){
                    String cityName = cityList2.get(position).getCityName();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("city_name",cityName);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(ChooseAreaActivity.this,"Error",Toast.LENGTH_SHORT);
                }
            }
        });
        queryProvinceFromServer();
    }

    private void queryCitiesFromServer(final String provinceCode) {
        cityList2 = myWeatherDB.loadcities(provinceCode);
        if (cityList2.size() == 0) {
            address = "http://files.heweather.com/china-city-list.json";
            showProgressDialog();
            Httputil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    cityList = Utility.handleCityResponse(myWeatherDB, response);
                    if (!cityList.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                queryCities(provinceCode);
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            dataList.clear();
            for(City city : cityList2){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }
    }

    private void queryCities(String provinceCode) {
        if(cityList.size() > 0){
            dataList.clear();
            String countyCode = "01";
            for(City city : cityList){
                if(city.getProvinceCode().equals(provinceCode) && (city.getCountyCode().equals(countyCode) || city.getCountyCode().equals("00"))){
                    dataList.add(city.getCityName());
                    cityList2.add(city);
                    myWeatherDB.saveCity(city);
                }
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            Toast.makeText(ChooseAreaActivity.this,"no city",Toast.LENGTH_SHORT);
        }
    }

    private void queryProvinceFromServer(){
        provinceList2 = myWeatherDB.loadProvinces();
        if (provinceList2.size() == 0) {
            address = "http://files.heweather.com/china-city-list.json";
            showProgressDialog();
            Httputil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    boolean result = false;
                    provinceList = Utility.handleProvinceResponse(myWeatherDB, response);
                    if (!provinceList.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                queryProvinces();
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else{
            dataList.clear();
            for (Province province : provinceList2){
                dataList.add(province.getProvinceName());
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                titleText.setText("中国");
                currentLevel = LEVEL_PROVINCE;
            }
        }
    }

    private void queryProvinces() {

        if(provinceList.size() > 0 ){
            dataList.clear();
            for(Province province : provinceList){
                if(!dataList.contains(province.getProvinceName())){
                    provinceList2.add(province);
                    myWeatherDB.saveProvince(province);
                    dataList.add(province.getProvinceName());
                }
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;

        }
    }

    private void closeProgressDialog() {
        if(progressDialog !=null){
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_CITY){
            queryProvinceFromServer();

        }else{
            if(isFromWeatherActivity){
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
