package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class Gravity extends AppCompatActivity {
    private SensorEventListener gravityListener;
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private double total;
    TextView tvXaxis, tvYaxis, tvZaxis, tvGravity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity);
        tvXaxis = (TextView)findViewById(R.id.tvXaxis);
        tvYaxis = (TextView)findViewById(R.id.tvYaxis);
        tvZaxis = (TextView)findViewById(R.id.tvZaxis);



        tvGravity = (TextView)findViewById(R.id.tvGravity);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        gravityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor == gravitySensor) {

                    total = Math.sqrt(Math.pow(event.values[0],2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2] , 2));

                    tvXaxis.setText("X axis : " + String.format("%.2f", event.values[0]));
                    tvYaxis.setText("Y axis : " + String.format("%.2f", event.values[1]));
                    tvZaxis.setText("Z axis : " + String.format("%.2f", event.values[2]));
                    tvGravity.setText("Total Gravity : "  + String.format("%.2f", total) + " m/s\u00B2");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }
    protected void onResume() {
        super.onResume();
        //센서 리스터 실행
        sensorManager.registerListener(gravityListener,gravitySensor,SensorManager.SENSOR_DELAY_UI);
    }
    protected void onPause(){
        super.onPause();
        //센서 리스너 종료
        sensorManager.unregisterListener(gravityListener);
    }
}
