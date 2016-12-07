package com.agate.testcity.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.R.attr.bitmap;

/**
 * Created by Agate on 2016/11/29.
 */

public class Httputil {
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    response.append("{result:");
                    String line;
                    int a=0;
                    while ((line = reader.readLine()) != null){
                        a++;
                        if(a > 1) {
                            response.append(line);
                        }
                    }
                        response.append("}");
                    if(listener != null){
                        listener.onFinish(response.toString());
                    }
                }catch (Exception e){
                    if(listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    public static void sendHttpRequest2(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    int a=0;
                    while ((line = reader.readLine()) != null){
                            response.append(line);
                    }
                    if(listener != null){
                        listener.onFinish(response.toString());
                    }
                }catch (Exception e){
                    if(listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
   public static void getHttpBitmap (final String weathercode,final HttpCallbackListener listener){

       new Thread(new Runnable() {
           @Override
           public void run() {
               Bitmap bitmap =null;
               String url = "http://files.heweather.com/cond_icon/"+weathercode+".png";
               try {
                   URL weathercondurl = new URL(url);
                   HttpURLConnection connection=(HttpURLConnection)weathercondurl.openConnection();
                   connection.setConnectTimeout(6000);
                   connection.getDoInput();
                   InputStream is =  connection.getInputStream();
                   bitmap = BitmapFactory.decodeStream(is);
                   is.close();
                   if (listener !=null){
                       listener.onFinish(bitmap);
                   }
               } catch (MalformedURLException e) {
                   e.printStackTrace();
               } catch (IOException e) {
                    listener.onError(e);
               }

           }
       }).start();

   }


}
