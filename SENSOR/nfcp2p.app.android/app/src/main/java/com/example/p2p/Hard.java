package com.example.p2p;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Hard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard);
        TextView text = (TextView) findViewById(R.id.text);

        // NFC 기능이 시스템에 구현되어 있는지 점검
        boolean hasNfcFeature =
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);

        if(hasNfcFeature){
            text.setText("has NFC feature");
            Log.i("NFC03","has NFC feature");
        }else{
            text.setText("no NFC feature");
            Log.i("NFC03","no NFC feature");
        }
    }
}