package com.example.p2p;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button pp,card,hard,nfc,pp2,pp3,pp4;
    private NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        pp = findViewById(R.id.pp);
        pp2 = findViewById(R.id.pp2);
        pp3 = findViewById(R.id.pp3);
        pp4 = findViewById(R.id.pp4);
        card = findViewById(R.id.card);
        hard = findViewById(R.id.hard);
        nfc = findViewById(R.id.nfc);

        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BeamData.class);
                startActivity(intent);
            }
        });
        pp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TagDispatch.class);
                startActivity(intent);
            }
        });
        pp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Receive.class);
                startActivity(intent);
            }
        });
        pp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, test.class);
                startActivity(intent);
            }
        });
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Card.class);
                startActivity(intent);
            }
        });
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Hard.class);
                startActivity(intent);
            }
        });
        nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MainActivity.this, NFC.class);
                startActivity(intent);
            }
        });
        nfcAdapter = NfcAdapter.getDefaultAdapter(this); // nfc를 지원하지않는 단말기에서는 null을 반환.
        if(nfcAdapter.isEnabled() == true) {

        }
        else {
            Intent intent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            } else {
                intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            }
            startActivity(intent);
        }
    }
}