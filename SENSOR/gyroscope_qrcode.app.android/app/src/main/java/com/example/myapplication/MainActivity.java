package com.example.myapplication;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorPrivacyManager;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.hardware.Sensor;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
public class MainActivity extends AppCompatActivity {

    private SensorManager mySensorManager;
    private SensorEventListener gyroListener;
    private Sensor MyGyroscope;
    public static final String TAG = MainActivity.class.getSimpleName();
    TextView tv,tv1;
    EditText ed;
    Button bt, qr,qr2,nfc,gravity;
    //Roll and Pitch
    private double pitch;
    private double roll;
    private double yaw;
    private double pitch2 =0;
    private double roll2 =0;
    private double yaw2 =0;
    private int show = 0;
    //timestamp and dt
    private double timestamp;
    private double dt;
    private int sen;
    // for radian -> dgree
    private double RAD2DGR = 180 / Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        tv1 = findViewById(R.id.tv1);
        ed = findViewById(R.id.ed);
        bt = findViewById(R.id.bt);
        qr = findViewById(R.id.qr);
        qr2 = findViewById(R.id.qr2);
        nfc = findViewById(R.id.nfc);
        gravity = findViewById(R.id.gravity);
        sen = 0;
        mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        MyGyroscope = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //내장 자이로센서 값 출력
        gyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                double gyroX = sensorEvent.values[0];
                double gyroY = sensorEvent.values[1];
                double gyroZ = sensorEvent.values[2];
                dt = (sensorEvent.timestamp - timestamp) * NS2S;
                timestamp = sensorEvent.timestamp;
                if (show < 50 ){
                    show += 1;
                }else if (show == 50){
                    show =0;
                    Log.v("센서", "GYROSCOPE           [X]:" + String.format("%.4f", sensorEvent.values[0])
                            + "           [Y]:" + String.format("%.4f", sensorEvent.values[1])
                            + "           [Z]:" + String.format("%.4f", sensorEvent.values[2])
                            + "           [Pitch]: " + String.format("%.1f", pitch * RAD2DGR)
                            + "           [Roll]: " + String.format("%.1f", roll * RAD2DGR)
                            + "           [Yaw]: " + String.format("%.1f", yaw * RAD2DGR)
                            + "           [dt]: " + String.format("%.4f", dt));
                    String ttt = "[Pitch]: " + String.format("%.1f", pitch * RAD2DGR)
                            + "  [Roll]: " + String.format("%.1f", roll * RAD2DGR)
                            + "  [Yaw]: " + String.format("%.1f", yaw * RAD2DGR);
                    tv1.setText(ttt);
                }

                /* 맨 센서 인식을 활성화 하여 처음 timestamp가 0일때는 dt값이 올바르지 않으므로 넘어간다. */
                if (dt - timestamp * NS2S != 0) {

                    /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
                     * 여기까지의 pitch, roll의 단위는 '라디안'이다.
                     * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */
                    pitch = pitch + gyroY * dt;
                    roll = roll + gyroX * dt;
                    yaw = yaw + gyroZ * dt;
                    if (pitch2 == 0) {
                        pitch2 = pitch * RAD2DGR;
                        roll2 = roll * RAD2DGR;
                        yaw2 = yaw * RAD2DGR;
                    }else {
                        //처음 센서값과 그다음센서값의 차이를비교
                        if (pitch* RAD2DGR-pitch2 > 20 || pitch* RAD2DGR - pitch2 < -20 ||
                                roll* RAD2DGR-roll2 > 20 || roll* RAD2DGR - roll2 < -20 ||
                                yaw* RAD2DGR-yaw2 > 20 || yaw* RAD2DGR - yaw2 < -20){
                            Log.v("센서", "GYROSCOPE           [X]:" + String.format("%.4f", sensorEvent.values[0])
                                    + "           [Y]:" + String.format("%.4f", sensorEvent.values[1])
                                    + "           [Z]:" + String.format("%.4f", sensorEvent.values[2])
                                    + "           [Pitch]: " + String.format("%.1f", pitch * RAD2DGR)
                                    + "           [Roll]: " + String.format("%.1f", roll * RAD2DGR)
                                    + "           [Yaw]: " + String.format("%.1f", yaw * RAD2DGR)
                                    + "           [dt]: " + String.format("%.4f", dt));
                            sen = sen +1;

                            String ttt = "[Pitch]: " + String.format("%.1f", pitch * RAD2DGR)
                                    + "  [Roll]: " + String.format("%.1f", roll * RAD2DGR)
                                    + "  [Yaw]: " + String.format("%.1f", yaw * RAD2DGR);
                            tv1.setText(ttt);
                            //센서값이 3연속으로 20이상 움직였을때 실행되는로직
                            if (sen == 3){
                                Intent intent = new Intent(MainActivity.this, QRscan.class);
                                startActivity(intent);
                            }
                        }else {
                            sen =0;
                        }
                        pitch2 = pitch * RAD2DGR;
                        roll2 = roll * RAD2DGR;
                        yaw2 = yaw * RAD2DGR;
                    }
             //       Log.v("센서", Double.toString(timestamp));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        Future<String> future = Executors.newSingleThreadExecutor()
                .submit(() -> {
                    Thread.sleep(3000);
                    return "This is the future 버튼누르고 3초후나옴";
                });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySensorManager.unregisterListener(gyroListener);

                Observable source = Observable.fromFuture(future);
                source.subscribe(System.out::println); //블로킹되어 기다림
                Intent intent = new Intent(MainActivity.this, time.class);
                startActivity(intent);
            }
        });
        nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, nfc.class);
                startActivity(intent);
            }
        });
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QRscan.class);
                startActivity(intent);
            }
        });
        qr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateQR.class);
                startActivity(intent);
            }
        });
        gravity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Gravity.class);
                startActivity(intent);
            }
        });
        //에디트 텍스트 RX 기능
        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력전 (에디트텍스트 클릭후 키보드 입력시, 텍스트 표현전)
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String tex = "" + ed.getText();
                tv1.setText(tex);
                // 텍스트가 변하는 동시에 일어나는 로직
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력후에 일어나는 로직 (채인지 이후에)
            }
        });

//옵져버 생성, 시작
        Observable<String> observable = Observable.create(emitter -> {
                    emitter.onNext(Thread.currentThread().getName() + "\n: RxJava Observer Test");
                    emitter.onComplete();
                }
        );

        observable.subscribeOn(Schedulers.io()).subscribe(observer); // io스레드에서 실행
        observable.subscribe(System.out::println, throwable -> System.out.println("Good bye"));
    }
    protected void onResume() {
        super.onResume();
        //센서 리스터 실행
        mySensorManager.registerListener(gyroListener,MyGyroscope,SensorManager.SENSOR_DELAY_UI);
    }
    protected void onPause(){
        super.onPause();
        //센서 리스너 종료
        mySensorManager.unregisterListener(gyroListener);
    }
    DisposableObserver<String> observer = new DisposableObserver<String>() {
        @Override
        public void onNext(@NonNull String s) {
            tv.setText(s);
            tv1.setText("..");
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d("TEST", "Observer Error...");
        }

        @Override
        public void onComplete() {
            Log.d("TEST", "Observer Complete!");
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}