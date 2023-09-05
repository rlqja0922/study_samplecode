package com.rebornsoft.navermap;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";

    Button btn_map1, btn_map2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_map1 = findViewById(R.id.btn_map1);
        btn_map2 = findViewById(R.id.btn_map2);

        MapView(); // 기본 맵뷰
        MapSearchView(); // 검색기능 맵뷰

    }

    public void MapView(){
        btn_map1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MapViewActivity.class);
                startActivity(intent);
            }
        });
    }
    public void MapSearchView(){
        btn_map2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MapSearchActivity.class);
                startActivity(intent);
            }
        });
    }
}
