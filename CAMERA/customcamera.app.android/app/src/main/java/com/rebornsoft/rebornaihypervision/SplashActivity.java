package com.rebornsoft.rebornaihypervision;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    int standardSize_X, standardSize_Y;
    float density;
    private TextView splahText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splahText = findViewById(R.id.splahText);
        getStandardSize();// 텍스트 크기조절
        splahText.setTextSize((float) (standardSize_X) / 35);
        splahText.setTextSize((float) (standardSize_Y) / 35);
//        getSupportActionBar().hide();// 액션바 숨김

        Handler handler= new Handler(Looper.myLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);// 초
    }

    public void getStandardSize() {
        Point ScreenSize = getScreenSize(SplashActivity.this);
        density  = getResources().getDisplayMetrics().density;
        standardSize_X = (int) (ScreenSize.x / density);
        standardSize_Y = (int) (ScreenSize.y / density);
    }
    public Point getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return  size;
    }
}