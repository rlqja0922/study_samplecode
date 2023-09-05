package com.rebornsoft.rbskakaoapisample;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedStore {

    public static void setMyEmail(Context c, String companySeq) {
        c.getSharedPreferences("MyData", 0).edit().putString("email", companySeq).apply();
    }
    public static String getMyEmail(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("email", 0);
        return sharedPreferences.getString("email", "");
    }
    public static void setMyName(Context c, String empSeq) {
        c.getSharedPreferences("MyData", 0).edit().putString("name", empSeq).apply();
    }
    public static String getMyName(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("name", 0);
        return sharedPreferences.getString("name", "");
    }
    public static void setMyProfileImage(Context c, String empSeq) {
        c.getSharedPreferences("MyData", 0).edit().putString("profileimage", empSeq).apply();
    }
    public static String getyProfileImage(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("profileimage", 0);
        return sharedPreferences.getString("profileimage", "");
    }
    public static void setThumNail(Context c, String empSeq) {
        c.getSharedPreferences("MyData", 0).edit().putString("name", empSeq).apply();
    }
    public static String getThumbnail(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("thumbnail", 0);
        return sharedPreferences.getString("thumbnail", "");
    }
}
