package com.rebornsoft.rebornaihypervision;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MyUtils {
    public static Toast toast;

    public void closeAppDialog(Context context, String title, String content){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(content);
        builder.setCancelable(false);
        builder.setPositiveButton(Html.fromHtml("<font color='#000000'>" + "확인" + "</font>"), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                System.exit(0);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public static String getNowActivityName(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
        ComponentName componentName = info.get(0).topActivity;
        return componentName.getShortClassName().substring(1);
    }
//    public void loadPfofile(){
//        String Name = SharedStore.getProfileImgName(SettingActivity.this);
//        String Rank = SharedStore.getMyRank(SettingActivity.this);
//        tv_Name.setText(Name);
//        tv_Rank.setText(Rank);
//    }
    public static void showToastMsg(Context context, String msg) {
     try {
         toast.cancel();
     } catch (NullPointerException e) {

        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
    public String getBLE_PASS(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myNewPass", 0);
        return sharedPreferences.getString("password", "12345");
    }
    public static void deleteUserData(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("UserList", 0).edit();
            try{
                editor.clear().apply();
            }catch (Exception e){
                Log.d("deleteUserData", "예외 발생 : " + e.getMessage());
            }
    }
    public final void startVibrator(Context context) {
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(110, 100));
        } else {
            vibrator.vibrate(100);
        }
    }


    public static boolean checkEmptyString(String str){
        try{
            if (str.equals("null") || str.equals("") || str == null){
                return true;
            }
        }catch (NullPointerException e){
            return true;
        }
        return false;
    }
    public static String messageCheck(String str){
        String mes=str.substring(0,5);
        return mes;
    }
    /** 이모티콘이 있을경우 "" 리턴, 그렇지 않을 경우 null 리턴 **/
    public static InputFilter specialCharacterFilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) { // 이모티콘 패턴
                 Pattern unicodeOutliers = Pattern.compile("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+"); // '-' 입력 받고 싶을 경우 :
                 if(unicodeOutliers.matcher(source).matches()&& !source.toString().matches(".*-.*")) {
                     return "";
                 }
            } return null;
        }
    };

}
