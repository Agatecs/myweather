package com.agate.testcity.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.agate.testcity.model.City;
import com.agate.testcity.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agate on 2016/12/2.
 */

public class MyWeatherDB {
    public static final String DB_NAME = "my_weather";

    public static final int VERSION = 1;

    private static MyWeatherDB myWeatherDB;

    private SQLiteDatabase db;
    public MyWeatherDB(Context context){
        MyWeatherOpenHelper dbHelper = new MyWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static MyWeatherDB getInstance(Context context){
        if (myWeatherDB == null){
            myWeatherDB = new MyWeatherDB(context);
        }
        return myWeatherDB;
    }
    public void saveProvince(Province province){
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }
    public List<Province> loadProvinces(){
        List<Province> provinceList = new ArrayList<Province>();
        Cursor cursor =db.query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(province);
            }while (cursor.moveToNext());
        }
        return provinceList;
    }
    public void saveCity(City city){
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_code",city.getProvinceCode());
            db.insert("City",null,values);
        }
    }
    public List<City> loadcities(String provinceCode){
        List<City> cityList = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"province_code=?",new String[]{provinceCode},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                cityList.add(city);
            }while (cursor.moveToNext());

        }
        return  cityList;
    }
}
