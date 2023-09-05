package com.rebornsoft.naverlogin;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class SharedStore {

    public static String getMyName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", 0);
        return sharedPreferences.getString("name", "");
    }
    public static void setMyName(Context context, String name){
        if(name.equals("") || name == null)return;
        context.getSharedPreferences("MyData", 0).edit().putString("name", name).commit();
    }
    public static String getMyID(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", 0);
        return sharedPreferences.getString("Id", "");
    }
    public static void setMyID(Context context, String name){
        if(name.equals("") || name == null)return;
        context.getSharedPreferences("MyData", 0).edit().putString("Id", name).commit();
    }
    public static String getMyEmail(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", 0);
        return sharedPreferences.getString("email", "");
    }
    public static void setMyEmail(Context context, String name){
        if(name.equals("") || name == null)return;
        context.getSharedPreferences("MyData", 0).edit().putString("email", name).commit();
    }
    public static String getMyImage(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", 0);
        return sharedPreferences.getString("image", "");
    }
    public static void setMyImage(Context context, String name){
        if(name.equals("") || name == null)return;
        context.getSharedPreferences("MyData", 0).edit().putString("image", name).commit();
    }

    public static void clearData(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


}
