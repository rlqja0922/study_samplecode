package com.rebornsoft.rbskakaoapisample.Map;

import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.rebornsoft.rbskakaoapisample.R;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MapSerachActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_serach);

        MapView MapView = new MapView(MapSerachActivity.this);

        RelativeLayout mapViewContainer = findViewById(R.id.MapView);

        mapViewContainer.addView(MapView);
    }
}