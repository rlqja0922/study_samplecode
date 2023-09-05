package com.example.gesture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {
    FrameLayout layout;
    TextView TextView;
    GestureDetector detector;
    float oldXvalue,oldYvalue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        layout = findViewById(R.id.layout1);
        TextView = findViewById(R.id.textView);
        layout.setOnTouchListener(new View.OnTouchListener() {
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
                Toast.makeText(MainActivity2.this,"onDown() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onDown() 호출됨");
                return true;
            }
            public void onShowPress(MotionEvent motionEvent) {
                //Toast.makeText(MainActivity.this,"onSHowPress() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onSHowPress() 호출됨");
                TextView.setText("onSHowPress() 호출됨");
            }
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                //  Toast.makeText(MainActivity.this,"onSingleTapUp() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onSingleTapUp() 호출됨");
                TextView.setText("onSingleTapUp() 호출됨");
                return true;
            }
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1){
                //  Toast.makeText(MainActivity.this,"onScroll() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onScroll() 호출됨: "+ v +"/"+  v1);
                TextView.setText("onScroll() 호출됨 : "+v+", "+v1);
                return true;
            }
            public void onLongPress(MotionEvent motionEvent) {
                //     Toast.makeText(MainActivity.this,"onLongPress() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onLongPress() 호출됨");
                TextView.setText("onLongPress() 호출됨");
            }
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1){
                // Toast.makeText(MainActivity.this,"onFling() 호출됨",Toast.LENGTH_LONG).show();
                Log.v("터치","onFling() 호출됨: "+ v +"/"+  v1);
                TextView.setText("onFling() 호출됨 : " +v+", "+v1);
                if (v > 1000 || v <-1000){
                    Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }
}