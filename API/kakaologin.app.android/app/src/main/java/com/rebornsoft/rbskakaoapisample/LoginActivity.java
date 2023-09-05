package com.rebornsoft.rbskakaoapisample;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.ApiErrorCode;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private SessionCallback sessionCallback;
    LoginButton kakaoLogin;
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        kakaoLogin = findViewById(R.id.kaka_login);
        sessionCallback = new SessionCallback(); //SessionCallback 초기화
        Session.getCurrentSession().addCallback(sessionCallback); //현재 세션에 콜백 붙임
        kakaoLogin.setOnClickListener(v -> {
            if (Session.getCurrentSession().checkAndImplicitOpen()) {
                Log.d(TAG, "onClick: 로그인 세션살아있음");
                // 카카오 로그인 시도 (창이 안뜬다.)
                session.getCurrentSession().addCallback(sessionCallback);
//                Session.getCurrentSession().checkAndImplicitOpen(); //자동 로그인
            } else {
                Log.d(TAG, "onClick: 로그인 세션끝남");
                // 카카오 로그인 시도 (창이 뜬다.)
                session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
            }
        });

        getAppKeyHash(); // 해쉬키 구하기
    }
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback); //현재 액티비티 제거 시 콜백도 같이 제거


    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"로그인 도중 오류가 발생했습니다: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "로그인 세션 에러 :"+errorResult.getErrorMessage());
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(),"세션이 닫혔습니다. 다시 시도해 주세요: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "로그인 세션 에러 :"+errorResult.getErrorMessage());
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Log.i("KAKAO_API", "사용자 아이디: " + result.getId());

                    UserAccount kakaoAccount = result.getKakaoAccount(); // UserAccount가 계속 널값으로 들어와서 확인이필욯마

                    if (kakaoAccount != null) {

                        // 이메일
                        String email = kakaoAccount.getEmail();
                        SharedStore.setMyEmail(LoginActivity.this, email);
                        Intent profileIntent = new Intent(getApplicationContext(), MainActivity.class);
                        if (email != null) {
                            Log.i("KAKAO_API", "email: " + email);
                            profileIntent.putExtra("email", email);
                        } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                            // 동의 요청 후 이메일 획득 가능
                            // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.

                        } else {
                            // 이메일 획득 불가
                        }

                        // 프로필
                        Profile profile = kakaoAccount.getProfile();

                        if (profile != null) {

                            Log.d("KAKAO_API", "nickname: " + profile.getNickname());
                            Log.d("KAKAO_API", "profile image: " + profile.getProfileImageUrl());
                            Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());

                            profileIntent.putExtra("name",profile.getNickname());
                            profileIntent.putExtra("thumnail",profile.getThumbnailImageUrl());
                            profileIntent.putExtra("profile",profile.getProfileImageUrl());

                            startActivity(profileIntent);

                            SharedStore.setMyName(LoginActivity.this,profile.getThumbnailImageUrl() );
                            SharedStore.setMyName(LoginActivity.this,profile.getProfileImageUrl() );
                            SharedStore.setMyName(LoginActivity.this,profile.getNickname() );
                        } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                            // 동의 요청 후 프로필 정보 획득 가능

                        } else {
                            // 프로필 획득 불가
                        }
                    }
//                    Log.i("KAKAO_API", "사용자 아이디: " + result.getId());
//                    String id = String.valueOf(result.getId());
//                    UserAccount kakaoAccount = result.getKakaoAccount();
//                    Log.d("KAKAO_id", " id :  "+id);
//                    if (kakaoAccount != null) {
//
//                        // 이메일
//                        String email = kakaoAccount.getEmail();
//                        Profile profile = kakaoAccount.getProfile();
//                        if (profile ==null){
//                            Log.d("KAKAO_API", "onSuccess:profile null ");
//                        }else{
//                            Log.d("KAKAO_API", "onSuccess:getProfileImageUrl "+profile.getProfileImageUrl());
//                            Log.d("KAKAO_API", "onSuccess:getNickname "+profile.getNickname());
//                        }
//                        if (email != null) {
//
//                            Log.d("KAKAO_API", "onSuccess:email "+email);
//                        }
//
//                        // 프로필
//                        Profile _profile = kakaoAccount.getProfile();
//
//                        if (_profile != null) {
//
//                            Log.d("KAKAO_API", "nickname: " + _profile.getNickname());
//                            Log.d("KAKAO_API", "profile image: " + _profile.getProfileImageUrl());
//                            Log.d("KAKAO_API", "thumbnail image: " + _profile.getThumbnailImageUrl());
//
//                        } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
//                            // 동의 요청 후 프로필 정보 획득 가능
//
//                        } else {
//                            // 프로필 획득 불가
//                        }
//                    }else{
//                        Log.i("KAKAO_API", "onSuccess: kakaoAccount null");
//                    }
//                    intent.putExtra("name", result.getNickname());
//                    intent.putExtra("profile", result.getProfileImagePath());
//                    startActivity(intent);
//                    finish();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+e.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG,"로그인 인터넷 에러 : e "+e.toString());
        }
    }
}