package com.example.gesture;

import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;

import android.view.GestureDetector;

import android.view.GestureDetector.SimpleOnGestureListener;

import android.view.MotionEvent; import android.view.View;

import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    float oldXvalue,oldYvalue;
    Button button;
    TextView TextView,TextView1,TextView2,TextView3;
    GestureDetector detector;
    FrameLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.layout);
        TextView = findViewById(R.id.Text1);
        TextView1 = findViewById(R.id.text3);
        TextView2= findViewById(R.id.text4);
        TextView3 = findViewById(R.id.text5);
        TextView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int parentWidth = ((ViewGroup)v.getParent()).getWidth();    // 부모 View 의 Width
                int parentHeight = ((ViewGroup)v.getParent()).getHeight();    // 부모 View 의 Height

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    // 뷰 누름
                    oldXvalue = event.getX();
                    oldYvalue = event.getY();
                    Log.d("viewTest", "oldXvalue : "+ oldXvalue + " oldYvalue : " + oldYvalue);    // View 내부에서 터치한 지점의 상대 좌표값.
                    Log.d("viewTest", "v.getX() : "+v.getX());    // View 의 좌측 상단이 되는 지점의 절대 좌표값.
                    Log.d("viewTest", "RawX : " + event.getRawX() +" RawY : " + event.getRawY());    // View 를 터치한 지점의 절대 좌표값.
                    Log.d("viewTest", "v.getHeight : " + v.getHeight() + " v.getWidth : " + v.getWidth());    // View 의 Width, Height

                }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    // 뷰 이동 중
                    v.setX(v.getX() + (event.getX()) - (v.getWidth()/2));
                    v.setY(v.getY() + (event.getY()) - (v.getHeight()/2));

                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    // 뷰에서 손을 뗌

                    if(v.getX() < 0){
                        v.setX(0);
                    }else if((v.getX() + v.getWidth()) > parentWidth){
                        v.setX(parentWidth - v.getWidth());
                    }

                    if(v.getY() < 0){
                        v.setY(0);
                    }else if((v.getY() + v.getHeight()) > parentHeight){
                        v.setY(parentHeight - v.getHeight());
                    }

                }
                return true;
            }
        });

        layout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float curX = event.getX();
                float curY = event.getY();
                detector.onTouchEvent(event);
                return true;
            }
        });
        //추가 코드 여기서 부터
        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            public boolean onDown(MotionEvent motionEvent) {
                Toast.makeText(MainActivity.this,"onDown() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onDown() 호출됨");
                return true;
            }
            
            public void onShowPress(MotionEvent motionEvent) {
                //Toast.makeText(MainActivity.this,"onSHowPress() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onSHowPress() 호출됨");
                TextView3.setText("onSHowPress() 호출됨");
            }
            public boolean onSingleTapUp(MotionEvent motionEvent) {
              //  Toast.makeText(MainActivity.this,"onSingleTapUp() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onSingleTapUp() 호출됨");
                TextView3.setText("onSingleTapUp() 호출됨");
                return true;
            }
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1){
              //  Toast.makeText(MainActivity.this,"onScroll() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onScroll() 호출됨: "+ v +"/"+  v1);
                TextView1.setText("onScroll() 호출됨 : "+v+", "+v1);
                return true;
            }
            public void onLongPress(MotionEvent motionEvent) {
           //     Toast.makeText(MainActivity.this,"onLongPress() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onLongPress() 호출됨");
                TextView2.setText("onLongPress() 호출됨");
            }
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                // Toast.makeText(MainActivity.this,"onFling() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치", "onFling() 호출됨: " + v + "/" + v1);
                TextView1.setText("onFling() 호출됨 : " + v + ", " + v1);
                if (v > 1000 || v < -1000) {
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

}