package com.rebornsoft.rebornaihypervision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;

import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    public ImageButton mainbtn;
    private long backPressedTime = 0;
    final String TAG = "MainActivity";
    public Context mContext;
    public Toast toast;

    int standardSize_X, standardSize_Y;
    float density;
    public TextView tv_main_top, tv_main_top2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedStore.deleteUserData(MainActivity.this);

        // 상태바를 안보이도록 합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getStandardSize();// 텍스트 크기조절
        tv_main_top = findViewById(R.id.tv_main_top);
        tv_main_top2 = findViewById(R.id.tv_main_top2);
        tv_main_top.setTextSize((float) (standardSize_X) / 35);
        tv_main_top.setTextSize((float) (standardSize_Y) / 35);

        tv_main_top2.setTextSize((float) (standardSize_X) / 55);
        tv_main_top2.setTextSize((float) (standardSize_Y) / 55);
        mainbtn = findViewById(R.id.mainbtn);
        mainbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);

            }
        });
    }

    public Point getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }


    public void getStandardSize() {
        Point ScreenSize = getScreenSize(MainActivity.this);
        density = getResources().getDisplayMetrics().density;
        standardSize_X = (int) (ScreenSize.x / density);
        standardSize_Y = (int) (ScreenSize.y / density);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(MainActivity.this, "'뒤로' 버튼을 \n한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backPressedTime + 2000) {
            try {
//                onDestroy();
                ActivityCompat.finishAffinity(MainActivity.this);
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(intent.CATEGORY_HOME);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            toast.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedStore.deleteUserData(MainActivity.this);
    }
}