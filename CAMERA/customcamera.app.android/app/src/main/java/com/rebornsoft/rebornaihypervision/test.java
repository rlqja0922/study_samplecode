package com.rebornsoft.rebornaihypervision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

public class test extends AppCompatActivity {

    public static ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        imageView = findViewById(R.id.testimg);
//        Intent intent = getIntent();
        String img = SharedStore.getCarImg(test.this);
//        Bitmap getBitmap = SharedStore.getBitmap(test.this, "MyData");
//        imageView.setImageBitmap(getBitmap);
        setExistImage(imageView, img);
//        imageView.setImageBitmap(SharedStore.getCarImg());
    }

    private void setExistImage(ImageView imageView, String base64String){
        if (!base64String.isEmpty()) {
            byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }
    }
}