package com.rebornsoft.naverlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

public class LoginActivity extends AppCompatActivity {

        Button btn_login;

        OAuthLogin mOAuthLoginModule;
        Context mContext;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            mContext = getApplicationContext();



            btn_login = findViewById(R.id.btn_login);

            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOAuthLoginModule = OAuthLogin.getInstance();
                    mOAuthLoginModule.init(
                            mContext
                            ,getString(R.string.naver_client_id)
                            ,getString(R.string.naver_client_secret)
                            ,getString(R.string.naver_client_name)

                    );
                    @SuppressLint("HandlerLeak")
                    OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
                        @Override
                        public void run(boolean success) {
                            if (success) {
                                String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                                String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                                long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                                String tokenType = mOAuthLoginModule.getTokenType(mContext);

                                Log.i("LoginData","accessToken : "+ accessToken);
                                Log.i("LoginData","refreshToken : "+ refreshToken);
                                Log.i("LoginData","expiresAt : "+ expiresAt);
                                Log.i("LoginData","tokenType : "+ tokenType);
                                new RequestApiTask(mContext, mOAuthLoginModule).execute();
//                                try {
//                                    Thread.sleep(1);
//                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);      // 로그인 성공후 이동할 화면
//                                    startActivity(intent);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }


                            } else {
                                String errorCode = mOAuthLoginModule
                                        .getLastErrorCode(mContext).getCode();
                                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                                Toast.makeText(mContext, "errorCode:" + errorCode
                                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                            }
                        };
                    };

                    mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
                }

            });


        }
    }
