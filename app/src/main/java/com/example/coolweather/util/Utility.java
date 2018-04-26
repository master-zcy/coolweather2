package com.example.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018/4/25.
 * 公用类
 */

public class Utility {
    public static final String API_KEY = "8bdeda21439444f9acab316f2d781158";

    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(
             String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                Log.e("zcy", "allProvinces.length" + allProvinces.length());

                if (allProvinces != null && allProvinces.length() > 0) {
                    for (int i = 0; i < allProvinces.length(); i++) {
                        JSONObject jsonObject = allProvinces.getJSONObject(i);
                        Log.e("zcy", "name " + jsonObject.getString("name"));
                        Province province = new Province();
                        province.setProvinceCode(jsonObject.getInt("id"));
                        province.setProvinceName(jsonObject.getString("name"));
                        // 将解析出来的数据存储到Province表
                        province.save();
                    }
                    return true;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */

    public synchronized static boolean handleCitiesResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            JSONArray allCities;
            try {
                allCities = new JSONArray(response);
                Log.e("zcy","allCities.length"+allCities.length());

                if (allCities != null && allCities.length() > 0) {
                    for (int i = 0; i < allCities.length(); i++) {
                        JSONObject jsonObject = allCities.getJSONObject(i);
                        Log.e("zcy","name "+jsonObject.getString("name"));
                        City city = new City();
                        city.setCityCode(jsonObject.getInt("id"));
                        city.setCityName(jsonObject.getString("name"));
                        city.setProvinceId(provinceId);
                        // 将解析出来的数据存储到City表
                        city.save();
                    }
                    return true;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */

    public synchronized static boolean handleCountiesResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            JSONArray allCounties;
            try {
                allCounties = new JSONArray(response);
                Log.e("zcy","allCities.length"+allCounties.length());

                if (allCounties != null && allCounties.length() > 0) {
                    for (int i = 0; i < allCounties.length(); i++) {
                        JSONObject jsonObject = allCounties.getJSONObject(i);
                        //Log.e("zcy","name "+jsonObject.getString("name"));
                        County county = new County();
                        county.setCountyName(jsonObject.getString("name"));
                        county.setWeatherId(jsonObject.getString("weather_id"));
                        county.setCityId(cityId);
                        // 将解析出来的数据存储到County表
                       county.save();
                    }
                    return true;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return false;
    }

    /*
     * 解析服务器返回的JSON数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);

            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中
     */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode
            ,String max,String min,String weatherDesp,String publishTime){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日 ", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        editor.putBoolean("city_selectd", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", max);
        editor.putString("temp2", min);
        editor.putString("publish_time", publishTime);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("current_data", sdf.format(new Date()));
        Log.e("zcy", "aaa publish_time："+publishTime);
        editor.commit();
    }

}
