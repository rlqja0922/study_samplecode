package com.rebornsoft.naverlogin;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.data.OAuthLoginState;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    public OAuthLogin mOAuthLoginModule;

    private final String TAG = "MainActivity";
    Button logout;
    ImageView profile_img;
    TextView tv_name2, tv_email2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        mOAuthLoginModule = OAuthLogin.getInstance();
        new RequestApiTask(MainActivity.this,mOAuthLoginModule);
        logout = findViewById(R.id.logout);
       logout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               try {
                   Log.d(TAG,"authloginmodule : "+mOAuthLoginModule);
                   Log.d(TAG,"mContext+ : "+mContext);
//                   boolean isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(mContext);
//
//                   if (!isSuccessDeleteToken) { // 연동해제
//                       // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
//                       // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
//                       Log.d(TAG, "errorCode:" + mOAuthLoginModule.getLastErrorCode(mContext));
//                       Log.d(TAG, "errorDesc:" + mOAuthLoginModule.getLastErrorDesc(mContext));
//                   }
                       mOAuthLoginModule.logout(mContext);
                       Toast.makeText(mContext, "로그아웃 하셨습니다." , Toast.LENGTH_SHORT).show();
                       SharedStore.clearData(mContext);
                       String name = SharedStore.getMyName(MainActivity.this);
                       String email = SharedStore.getMyEmail(MainActivity.this);
                       String img_profile = SharedStore.getMyImage(MainActivity.this);
                       Log.d(TAG, "logout name :"+name);
                       Log.d(TAG, "logout email :"+email);
                       Log.d(TAG, "logout profileImage :"+img_profile);
//                   Log.d(TAG, "logout isSuccessDeleteToken :"+isSuccessDeleteToken);
                       finish();
                       Toast.makeText(mContext, "로그아웃 하셨습니다." , Toast.LENGTH_SHORT).show();

//                   mOAuthLoginModule.logout(mContext);

               }catch (Exception e){
                   e.printStackTrace();
               }
           }
       });
        findViewIdFunc();

    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewIdFunc();
    }

    public void findViewIdFunc(){
        tv_name2 = findViewById(R.id.tv_name2);
        tv_email2 = findViewById(R.id.tv_email2);
        profile_img = findViewById(R.id.img_profile);

        Intent getUserIntent = getIntent();
        String MyName = getUserIntent.getStringExtra("name");
        String MyEmail = getUserIntent.getStringExtra("email");
        String MyImage = getUserIntent.getStringExtra("profile_image");

        String name = SharedStore.getMyName(MainActivity.this);
        String email = SharedStore.getMyEmail(MainActivity.this);
        String img_profile = SharedStore.getMyImage(MainActivity.this);
        Log.d(TAG, "name :"+name);
        Log.d(TAG, "email :"+email);
        Log.d(TAG, "profileImage :"+img_profile);
        Log.d(TAG, "MyName :"+MyName);
        Log.d(TAG, "MyEmail :"+MyEmail);
        Log.d(TAG, "MyImage :"+MyImage);
        tv_name2.setText(MyName); // 이름
        tv_email2.setText(MyEmail); // 이메일

        Glide.with(MainActivity.this).load(MyImage).into(profile_img); // 이미지




    }

}