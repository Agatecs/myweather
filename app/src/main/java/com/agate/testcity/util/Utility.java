package com.agate.testcity.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.agate.testcity.db.MyWeatherDB;
import com.agate.testcity.model.City;
import com.agate.testcity.model.Province;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by Agate on 2016/11/29.
 */

public class Utility {

    public static List<Province> handleProvinceResponse(MyWeatherDB myWeatherDB, String response){
        List<Province> provinceList = new ArrayList<Province>();
        if(!TextUtils.isEmpty(response)){
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject temp = jsonArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(temp.getString("provinceZh"));
                    province.setProvinceCode(temp.getString("id").substring(5,7));
                    provinceList.add(province);
                    //myWeatherDB.saveProvince(province);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return provinceList;
    }
    public static List<City> handleCityResponse(MyWeatherDB myWeatherDB,String response){
        List<City> cityList = new ArrayList<City>();
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject temp = jsonArray.getJSONObject(i);
                    City city = new City();
                    city.setCityName(temp.getString("cityZh"));
                    city.setProvinceCode(temp.getString("id").substring(5,7));
                    city.setCityCode(temp.getString("id").substring(7,9));
                    city.setCountyCode(temp.getString("id").substring(9));
                    cityList.add(city);
                   // myWeatherDB.saveCity(city);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return cityList;
    }
   public static void handleWeatherResponse(Context context,String response){
       try{
           JSONObject jsonObject = new JSONObject(response);
           JSONArray HeWeather5 = jsonObject.getJSONArray("HeWeather5");
           JSONObject jso = HeWeather5.getJSONObject(0);
           JSONObject basic = jso.getJSONObject("basic");
           JSONObject update =basic.getJSONObject("update");
           JSONObject now = jso.getJSONObject("now");
           JSONObject cond = now.getJSONObject("cond");
           String publishTime = update.getString("loc");
           String cityName = basic.getString("city");
           String weatherCode = basic.getString("id");
           String temp1 = now.getString("tmp");
           String weatherDesp = cond.getString("txt");
           String[] publishTimehour = publishTime.split(" ");
           publishTime = publishTimehour[1];
           saveWeatherInfo(context,cityName,weatherCode,temp1,weatherDesp,publishTime);
       }catch (Exception e){
            e.printStackTrace();
       }
   }

    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }

}
