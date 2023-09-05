package com.rebornsoft.retrofit2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SharedStore {

    public static String getMyIP(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", 0);
        return sharedPreferences.getString("ip", "ip를 설정해주세요.");
    }
    public static void setMyIP(Context context, String ip){
        if(ip.equals("") || ip == null)return;
        context.getSharedPreferences("MyData", 0).edit().putString("ip", ip).commit();
    }
    public static void deleteMyIP(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", 0);
        SharedPreferences.Editor dleEditor = sharedPreferences.edit();
        dleEditor.clear();
        dleEditor.commit();
    }
    public static void setCompanySeq(Context c, String companySeq) {
        c.getSharedPreferences("MyData", 0).edit().putString("companySeq", companySeq).apply();
    }
    public static String getCompanySeq(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("MyData", 0);
        return sharedPreferences.getString("companySeq", "");//샵 붙여야함
    }
    public static void setEmpSeq(Context c, String empSeq) {
        c.getSharedPreferences("MyData", 0).edit().putString("empSeq", empSeq).apply();
    }
    public static String getEmpSeq(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("MyData", 0);
        return sharedPreferences.getString("empSeq", "");//샵 붙여야함
    }

    public static void setCompanyName(Context c, String companyName) {
        c.getSharedPreferences("MyData", 0).edit().putString("companyName", companyName).apply();
    }
    public static String getCompanyName(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("MyData", 0);
        return sharedPreferences.getString("companyName", "리본 소프트");//샵 붙여야함
    }

    public static String getMyEmployeeNum(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", 0);
        return sharedPreferences.getString("employeeNum", "-");//0 덕우씨, 1 진호씨, 2훈사
    }
    public static void setMyEmployeeNum(Context context, String employeeNum){
        if(employeeNum.equals("") || employeeNum == null)return;
        context.getSharedPreferences("MyData", 0).edit().putString("employeeNum", employeeNum).commit();
    }

    public static String getMyPassWord(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", 0);
        return sharedPreferences.getString("password", "-");//0 덕우씨, 1 진호씨, 2훈사
    }
    public static void setMyPassWord(Context context, String pw){
        if(pw.equals("") || pw == null)return;
        context.getSharedPreferences("MyData", 0).edit().putString("password", pw).commit();
    }

    public static boolean getAutoLogin(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", 0);
        return sharedPreferences.getBoolean("isAutoLogin", false);
    }
    public static void setAutoLogin(Context context, boolean isAutoLogin){
        Log.d("autoCheck", "autoCheck : " + isAutoLogin);
        context.getSharedPreferences("MyData", 0).edit().putBoolean("isAutoLogin", isAutoLogin).commit();
    }



}
