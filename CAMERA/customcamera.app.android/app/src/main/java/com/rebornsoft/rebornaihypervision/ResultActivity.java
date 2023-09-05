package com.rebornsoft.rebornaihypervision;

import static com.rebornsoft.rebornaihypervision.R.drawable.resultback1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    private final String TAG = "ResultActivity";

    public static Context mContext;
    public static TextView tv_resultTop, tv_result_succ1, tv_result_succ2, tv_result_succ3, tv_result_fail1, tv_result_fail2,
                            tv_result_info1, tv_result_info2, tv_result_point1, tv_result_point2,succtext;
    public ConstraintLayout const_gohome, const_restart,fail1,fail2,succ1,succ2,succ3,background,background1;
    int standardSize_X, standardSize_Y;
    float density;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
//        String CarImg = SharedStore.getCarImg(this);

        getStandardSize();// 텍스트 크기조절
        findId();
    }

    public void findId(){

//        String CarImg = SharedStore.getCarImg(this);
//        Log.d(TAG, "Base64 이미지 값 : "+ CarImg);
//        new HttpCarImage(this).execute(CarImg);
        String MyCarNumber = SharedStore.getMyCarNumber(ResultActivity.this);
        Log.d(TAG, "MyCarNumber 값 : "+ MyCarNumber);
        fail1 = findViewById(R.id.fail1);
        fail2 = findViewById(R.id.fail2);
        succ1 = findViewById(R.id.suc1);
        succ2 = findViewById(R.id.suc2);
        succ3 = findViewById(R.id.suc3);
        background = findViewById(R.id.background);
        background1 = findViewById(R.id.background1);
        succtext = findViewById(R.id.succtext2);
        const_restart = findViewById(R.id.const_restart);
        const_gohome = findViewById(R.id.const_gohome);
        tv_resultTop = findViewById(R.id.tv_resultTop);
        tv_result_info1 = findViewById(R.id.tv_resultInfo1);
        tv_result_info2 = findViewById(R.id.tv_resultInfo2);

        tv_resultTop.setTextSize((float)(standardSize_X / 33));
        tv_resultTop.setTextSize((float)(standardSize_Y / 33));
        tv_result_info1.setTextSize((float)(standardSize_X / 60));
        tv_result_info1.setTextSize((float)(standardSize_Y / 60));
        tv_result_info2.setTextSize((float)(standardSize_X / 60));
        tv_result_info2.setTextSize((float)(standardSize_Y / 60));

        if (MyCarNumber.equals("") || MyCarNumber.equals(null)){
            ResultActivity.tv_result_info1.setVisibility(View.VISIBLE);
            ResultActivity.tv_result_info2.setVisibility(View.VISIBLE);
            fail1.setVisibility(View.VISIBLE);
            fail2.setVisibility(View.VISIBLE);
            succ1.setVisibility(View.GONE);
            succ2.setVisibility(View.GONE);
            succ3.setVisibility(View.GONE);
            background.setVisibility(View.GONE);
            background1.setVisibility(View.VISIBLE);
        }else {
            ResultActivity.tv_result_info1.setVisibility(View.GONE);
            ResultActivity.tv_result_info2.setVisibility(View.GONE);
            fail1.setVisibility(View.GONE);
            fail2.setVisibility(View.GONE);
            succ1.setVisibility(View.VISIBLE);
            succ2.setVisibility(View.VISIBLE);
            succ3.setVisibility(View.VISIBLE);
            background.setVisibility(View.VISIBLE);
            background1.setVisibility(View.GONE);
            ResultActivity.succtext.setText(MyCarNumber);
        }

        const_gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedStore.deleteUserData(ResultActivity.this);
                Intent intentHome = new Intent(ResultActivity.this, MainActivity.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHome);
            }
        });
        const_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedStore.deleteUserData(ResultActivity.this);
                Intent intentRestart = new Intent(ResultActivity.this, CameraActivity.class);
                intentRestart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentRestart);
            }
        });
    }
    public Point getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return  size;
    }


    public void getStandardSize() {
        Point ScreenSize = getScreenSize(ResultActivity.this);
        density  = getResources().getDisplayMetrics().density;
        standardSize_X = (int) (ScreenSize.x / density);
        standardSize_Y = (int) (ScreenSize.y / density);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedStore.deleteUserData(ResultActivity.this);
//        Log.d(TAG, "삭제후 데이터 1 : "+deleteData1);
//        Log.d(TAG, "삭제후 데이터 2 : "+deleteData2);
    }

    @Override
    public void onBackPressed() {
        SharedStore.deleteUserData(ResultActivity.this);
        Intent intentHome = new Intent(ResultActivity.this, MainActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentHome);
    }
}