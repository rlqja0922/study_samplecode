package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;

public class time extends AppCompatActivity {
    Button cold,hot,back;
    Disposable disposable,disposable1,disposable2,disposable3;
    TextView cold1,cold2,hot1,hot2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        cold = findViewById(R.id.cold);
        hot = findViewById(R.id.hot);
        back = findViewById(R.id.back);
        cold1 = findViewById(R.id.cold1);
        cold2 = findViewById(R.id.cold2);
        hot1 = findViewById(R.id.hot1);
        hot2 = findViewById(R.id.hot2);

        Observable src = Observable.interval(1, TimeUnit.SECONDS);
        ConnectableObservable src1 =
                Observable.interval(1, TimeUnit.SECONDS)
                        .publish();


        cold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disposable = src.subscribe(value ->{ System.out.println("cold1: " + value); cold1.setText(value.toString());
                        }
                       );
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                disposable1 = src.subscribe(value ->{ System.out.println("cold2: " + value); cold2.setText(value.toString());
                        }
                );
            }
        });
        hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                src1.connect();
                disposable2 = src1.subscribe(value -> {System.out.println("hot1: " + value); hot1.setText(value.toString());}); try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                disposable3 = src1.subscribe(value -> {System.out.println("hot2: " + value); hot2.setText(value.toString());});
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disposable != null){
                    disposable.dispose();
                }
                if (disposable1 != null){
                    disposable1.dispose();
                }
                if (disposable2 != null){
                    disposable2.dispose();
                }
                if (disposable3 != null){
                    disposable3.dispose();
                }
                Intent intent = new Intent(time.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}