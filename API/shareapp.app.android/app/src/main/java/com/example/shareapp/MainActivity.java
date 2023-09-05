package com.example.shareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        LinearLayout share = (LinearLayout) findViewById(R.id.share);
        share.setOnClickListener(new LinearLayout.OnClickListener() {
            public void onClick(View view) {
                Intent msg = new Intent(Intent.ACTION_SEND);

                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.rebornsoft.onwalk");
                msg.putExtra(Intent.EXTRA_TITLE, "제목");
                msg.setType("text/plain");
                startActivity(Intent.createChooser(msg, "앱을 선택해 주세요"));

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,firebaseshare.class);
                startActivity(intent);
            }
        });
    }
}