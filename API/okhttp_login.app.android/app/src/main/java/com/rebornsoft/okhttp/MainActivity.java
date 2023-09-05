package com.rebornsoft.okhttp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }
    private void logOut() {

        MyAlert.MyDialog(MainActivity.this,

                "로그아웃 하시겠습니까?",
                "자동로그인정보가 삭제됩니다.",
                v -> {
                    SharedStore.setAutoLogin(MainActivity.this, false); // 자동로그인 삭제
                    Log.d(TAG, "자동로그인 값 확인" + SharedStore.getAutoLogin(MainActivity.this));

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                },
                null);
    }
}
