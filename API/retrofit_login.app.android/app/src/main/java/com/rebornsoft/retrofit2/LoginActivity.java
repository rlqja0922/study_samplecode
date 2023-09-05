package com.rebornsoft.retrofit2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.rebornsoft.retrofit2.retrofit.LoginData;
import com.rebornsoft.retrofit2.retrofit.RetrofitInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Toast toast;
    private Context mContext;

    EditText et_IpPort, et_IdNum, et_password,et_CompanyCode;
    Button btn_login;
    CheckBox check_autoLogin;
    private TextView tv_ip_port;
    private LinearLayout linearIp;

    private int failCount = 0;
    private long backPressedTime = 0;
    private String ipPort = "";
    private String empNum;
    private String password;
    private String companycode;
    private Boolean autoCheck;

    private RetrofitInterface retrofitInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;
        findviewById();
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

    }

    public void findviewById() {
        et_IpPort = findViewById(R.id.et_IpPort);
        et_IdNum = findViewById(R.id.et_IdNum);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        check_autoLogin = (CheckBox) findViewById(R.id.check_autoLogin);
        tv_ip_port = findViewById(R.id.tv_ip_port);
        linearIp = findViewById(R.id.linearIp);
        et_CompanyCode = findViewById(R.id.et_CompanyCode);
        //이모티콘 필터링
//        et_IpPort.setFilters(new InputFilter[]{specialCharacterFilter});
//        et_CompanyCode.setFilters(new InputFilter[]{specialCharacterFilter});
//        et_IdNum.setFilters(new InputFilter[]{specialCharacterFilter});

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogin();
            }
        });
        //유지보수를 위해 동일한 이벤트 형식으로 맞춤

        if (SharedStore.getMyIP(mContext).equals("ip를 설정해주세요.")){
            visibleIP(true);
        }else{
            visibleIP(false);
        }
        Intent intent = getIntent();
        if (intent.getExtras() ==null){

        }else if(intent.getExtras().getString("ip") != null){
            String ip = intent.getExtras().getString("ip");
            visibleIP(true);
            et_IpPort.setText(ip);
            ipPort = ip;
            Log.d("ip","ip"+ip);
        }
        else if ( intent.getExtras().getString("err")!=null){
            if(intent.getExtras().getString("err").equals("E0000")){
                visibleIP(true);
            }
        }

    }
    public static String messageCheck(String str){
        String mes=str.substring(0,5);
        return mes;
    }
    public void onClickLogin() {
        try{
            ipPort = et_IpPort.getText().toString();
        } catch (NullPointerException e){ }
        empNum = et_IdNum.getText().toString();
        password = et_password.getText().toString();
        companycode =et_CompanyCode.getText().toString();
        autoCheck = check_autoLogin.isChecked();//warper class 로 받지 않으면 tostring 불가

        Login();
    }

    public void Login() {
        String ipStr = ipPort.equals("") ? SharedStore.getMyIP(mContext) : ipPort;
        if(empNum.length()==0 || empNum ==null){
            MyAlert.MyDialog_single(LoginActivity.this, getString(R.string.di_ti_info), "사원번호를 입력해주세요", null);
        }else if(password.length()==0 || password ==null){
            MyAlert.MyDialog_single(LoginActivity.this, getString(R.string.di_ti_info), "비밀번호를 입력해주세요", null);
        }else if(companycode.length()==0 ||companycode==null){
            MyAlert.MyDialog_single(LoginActivity.this, getString(R.string.di_ti_info), "회사 코드를 입력해 주세요", null);
        }
        else {
            Log.d(TAG, "Login: " + ipStr);
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://" + ipStr)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                retrofitInterface = retrofit.create(RetrofitInterface.class);

                LoginData loginData = new LoginData(empNum, password,companycode);

                Call<LoginData> call = retrofitInterface.getLogin(loginData);
                call.enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                        if (response.isSuccessful()) {
                            LoginData loginResult = response.body();
                            Boolean status = loginResult.getStatus();
                            String mode = loginResult.getMode();
                            String message = loginResult.getMessage();
                            Log.d(TAG, "rrr" + status + mode + message);
                            if (status == true && !mode.equals("")) {
                                SharedStore.setEmpSeq(mContext,loginResult.getEmpSeq());
                                SharedStore.setCompanySeq(mContext,loginResult.getCompanySeq());
                                SharedStore.setMyIP(mContext, ipPort);
                                SharedStore.setMyEmployeeNum(mContext, empNum);
                                SharedStore.setMyPassWord(mContext, password);
                                SharedStore.setAutoLogin(mContext, autoCheck.toString() == "true"?true:false);
                                visibleIP(false);
                                Log.d(TAG,"사원번호 : "+ SharedStore.getMyEmployeeNum(mContext));
                                startActivity(new Intent(mContext, MainActivity.class));
                                failCount = 0;
                            }

                            else if (status == false) {
                                String submsg = messageCheck(message);
                                if (submsg.equals("I0301")) {
                                    failCount++;
                                    if (failCount>=3) {
                                        SharedStore.deleteMyIP(mContext);
                                        visibleIP(true);
                                    }
                                    MyAlert.MyDialog_single(LoginActivity.this,getString(R.string.di_ti_info), "퇴직", null);
                                }
                                else if (submsg.equals("I0300")) {
                                    failCount++;
                                    if (failCount >= 3) {
                                        SharedStore.deleteMyIP(mContext);
                                        visibleIP(true);
                                    }
                                    MyAlert.MyDialog_single(LoginActivity.this, getString(R.string.di_ti_info), getString(R.string.di_ct_error_join_id_pw), null);
                                }else if (submsg.equals("I0801"))  {
                                    failCount++;
                                    if (failCount >= 3) {
                                        SharedStore.deleteMyIP(mContext);
                                        visibleIP(true);
                                    }
                                    MyAlert.MyDialog_single(LoginActivity.this, getString(R.string.di_ti_info),"정확한 회사 코드를 입력해주세요.", null);
                                }else if (submsg.equals("E0000"))  {
                                    MyAlert.MyDialog_single(mContext, getString(R.string.di_ti_info),getString(R.string.E0000), null);
                                }else if (submsg.equals("E0001"))  {
                                    MyAlert.MyDialog_single(mContext, getString(R.string.di_ti_info),getString(R.string.E0001), null);
                                }else if (submsg.equals("E0003"))  {
                                    MyAlert.MyDialog_single(mContext, getString(R.string.di_ti_info),getString(R.string.E0001), null);
                                }else if (submsg.equals("E0004"))  {
                                    MyAlert.MyDialog_single(mContext, getString(R.string.di_ti_info),getString(R.string.E0001), null);
                                }else if (submsg.equals("E0005"))  {
                                    MyAlert.MyDialog_single(mContext, getString(R.string.di_ti_info),getString(R.string.E0001), null);
                                }else if (submsg.equals("E0002"))  {
                                    MyAlert.MyDialog_single(mContext, getString(R.string.di_ti_info),getString(R.string.E0002), null);
                                }else if (submsg.equals("E9999"))  {
                                    MyAlert.MyDialog_single(mContext, getString(R.string.di_ti_info),getString(R.string.E9999), null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginData> call, Throwable t) {
                        Log.d(TAG,"에러메세지"+t.getMessage());
                        t.getMessage();
                        visibleIP(true);
                        MyAlert.MyDialog_single(LoginActivity.this, getString(R.string.di_ti_info), getString(R.string.di_ct_error_join_ip), null);
                    }
                });

            }
            catch (Exception e) {
                e.printStackTrace();
                visibleIP(true);
                MyAlert.MyDialog_single(LoginActivity.this, getString(R.string.di_ti_info), getString(R.string.di_ct_error_join_ip), null);
            }
        }
    }

    //뒤로가기 두번으로 앱 종료
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backPressedTime + 2000){
            backPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(LoginActivity.this, "종료 하시겠습니까?", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= backPressedTime + 2000){
            try{
                finishAffinity();
                System.runFinalization();
                System.exit(0);
                finish();
            }catch(Exception e){
                Log.d(TAG, e.getMessage());
            }
            toast.cancel();
        }
    }

    private void visibleIP (boolean flag) {
        runOnUiThread(()->{
            if (flag){
                tv_ip_port.setVisibility(View.VISIBLE);
                linearIp.setVisibility(View.VISIBLE);
            }else{
                tv_ip_port.setVisibility(View.INVISIBLE);
                linearIp.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}