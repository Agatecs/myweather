package com.agate.testcity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Agate on 2016/12/2.
 */

public class MyWeatherOpenHelper extends SQLiteOpenHelper {
    public static final String CREATE_PROVINCE = "create table Province ("
                + "id integer primary key autoincrement, "
                + "province_name text, "
                + "province_code text)";
    public static final String CREATE_CITY = "create table City ("
                + "id integer primary key autoincrement, "
                + "province_code text, "
                + "city_name text, "
                + "city_code text, "
                + "province_id integer)";


    public MyWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
