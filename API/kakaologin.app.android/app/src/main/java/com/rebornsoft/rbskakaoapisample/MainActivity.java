package com.rebornsoft.rbskakaoapisample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.rebornsoft.rbskakaoapisample.Map.MapSerachActivity;

public class MainActivity extends AppCompatActivity {
    private Button logout;
    private sessionCallback sessionCallback = new sessionCallback();
    com.kakao.auth.Session session;
    ImageView img_profile;
    TextView tv_name, tv_thumnail, tv_profile, tv_email;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.logout);
        img_profile = findViewById(R.id.img_profile);
        tv_name = findViewById(R.id.profile_name);
        tv_email = findViewById(R.id.profile_email);
        Intent intent = getIntent();
        String name =  intent.getStringExtra("name");
        String thumnail = intent.getStringExtra("thumnail");
        String profile = intent.getStringExtra("profile");
        String email = intent.getStringExtra("email");
        Log.d(TAG,"name"+name);
        Log.d(TAG,"email"+email);
        Log.d(TAG,"thumnail"+thumnail);
        Log.d(TAG,"profile"+profile);

        tv_name.setText(name);
        tv_email.setText(email);
        Glide.with(MainActivity.this).load(thumnail).into(img_profile);
        sessionCallback = new sessionCallback(); //SessionCallback 초기화
        Session.getCurrentSession().addCallback(sessionCallback); //현재 세션에 콜백 붙임
        sessionCallback.requestMe();

//       com.kakao.auth.Session.getCurrentSession().addCallback(sessionCallback2); //현재 세션에 콜백 붙임


        logout.setOnClickListener(v -> {
            kakaoLogOut();

        });

        // 카카오 개발자 홈페이지에 등록할 해시키 구하기
//        getHashKey();
    }

    private void kakaoLogOut(){
        MyAlert.MyDialog(MainActivity.this, "로그아웃 하시겠습니까?", "카카오톡 세션이 종료 됩니다.", v -> {
            Log.d(TAG, "onCreate:click ");

            UserManagement.getInstance()
                    .requestUnlink(new UnLinkResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Toast.makeText(getApplicationContext(), "에러: "+errorResult+"로그인 세션이 닫혔습니다.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public  void onFailure(ErrorResult errorResult) {
                            int error = errorResult.getErrorCode();
                            if(error == ApiErrorCode.CLIENT_ERROR_CODE) {
                                Toast.makeText(getApplicationContext(), "에러: "+error+"\n네트워크 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "에러: "+error+"\n회원 탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onSuccess(Long result) {
                            com.kakao.auth.Session.getCurrentSession().removeCallback(sessionCallback);
                            Toast.makeText(getApplicationContext(), "성공: "+result+"\n회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
//            UserManagement.getInstance()
//                    .requestLogout(new LogoutResponseCallback() {
//                        @Override
//                        public void onSessionClosed(ErrorResult errorResult) {
//                            super.onSessionClosed(errorResult);
//                            Log.d(TAG, "onSessionClosed: "+errorResult.getErrorMessage());
//
//                        }
//                        @Override
//                        public void onCompleteLogout() {
//                            if (sessionCallback != null) {
//
//                                com.kakao.auth.Session.getCurrentSession().removeCallback(sessionCallback);
//                            }
//                            finish();
//                            Log.d(TAG, "onCompleteLogout:logout ");
//                        }
//                    });
        },null);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 세션 콜백 삭제
        com.kakao.auth.Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (com.kakao.auth.Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class sessionCallback implements ISessionCallback {

        // 로그인에 성공한 상태
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        // 로그인에 실패한 상태
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }


        // 사용자 정보 요청
        public void requestMe() {
            UserManagement.getInstance()
                    .me(new MeV2ResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                        }

                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                        }

                        @Override
                        public void onSuccess(MeV2Response result) {
                            Log.i("KAKAO_API", "사용자 아이디: " + result.getId());
                            String id = String.valueOf(result.getId());
                            UserAccount kakaoAccount = result.getKakaoAccount();
                            if (kakaoAccount != null) {

                                // 이메일
                                String email = kakaoAccount.getEmail();
                                Profile profile = kakaoAccount.getProfile();
                                if (profile ==null){
                                    Log.d("KAKAO_API", "onSuccess:profile null ");
                                }else{
                                    Log.d("KAKAO_API", "onSuccess:getProfileImageUrl "+profile.getProfileImageUrl());
                                    Log.d("KAKAO_API", "onSuccess:getNickname "+profile.getNickname());
                                }
                                if (email != null) {

                                    Log.d("KAKAO_API", "onSuccess:email "+email);
                                }

                                // 프로필
                                Profile _profile = kakaoAccount.getProfile();

                                if (_profile != null) {

                                    Log.d("KAKAO_API", "nickname: " + _profile.getNickname());
                                    Log.d("KAKAO_API", "profile image: " + _profile.getProfileImageUrl());
                                    Log.d("KAKAO_API", "thumbnail image: " + _profile.getThumbnailImageUrl());

                                } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // 동의 요청 후 프로필 정보 획득 가능

                                } else {
                                    // 프로필 획득 불가
                                }
                            }else{
                                Log.i("KAKAO_API", "onSuccess: kakaoAccount null");
                            }
                        }
                    });

        }
    }


}