package com.rebornsoft.naversearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button btn_movie, btn_blog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_blog = findViewById(R.id.btn_blog);
        btn_movie = findViewById(R.id.btn_movie);

        MovieSearch();
        BlogSearch();
    }

    private void MovieSearch(){
        btn_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });
    }
    private void BlogSearch(){
        btn_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, BlogActivity.class);
                startActivity(intent);
            }
        });
    }
}