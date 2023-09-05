package com.rebornsoft.okhttp;

import static com.rebornsoft.okhttp.LoginActivity.autoCheck;
import static com.rebornsoft.okhttp.LoginActivity.visibleIP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.rebornsoft.okhttp.LoginActivity;
public class OkHttpLogin extends AsyncTask<String, Void, String> {

    private static final String TAG = "OkHttpExample";
    private OkHttpClient client = new OkHttpClient.Builder().build();
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Activity activity= new LoginActivity();
    private Context mContext;
    String ip = LoginActivity.ipStr;
    String companyCode = "";
    String empNum = "";
    String password = "";
    public OkHttpLogin(Context mContext) {
        this.mContext = mContext;


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... data) {

        String result = "";
        companyCode = data[0];
        empNum = data[1];
        password = data[2];
        try {
            JSONObject requestData = new JSONObject();
            try {
                requestData.put("companyCode", companyCode); // 보낼 데이터 입력
                requestData.put("empNum", empNum); // 보낼 데이터 입력
                requestData.put("password", password); // 보낼 데이터 입력

            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, requestData.toString());
            Request request = new Request.Builder()
                    .url("http://" + ip)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            result = response.body().string();
            Log.d(TAG, "result :  " + result);
        } catch (Exception e) {
            e.printStackTrace();
            result = "error";

        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) { // 서버통신 결과를 처리하는 곳
        super.onPostExecute(result);
        if (result.equals("") || result == null) { // 서버통신 실패

            return;
        }
        try {
            JSONObject jsonData = new JSONObject(result); // 서버통신 성공시 데이터 담겨옴
            Boolean status = jsonData.getBoolean("status");
            String companySeq = jsonData.getString("companySeq");
            String empSeq = jsonData.getString("empSeq");
            String mode = jsonData.getString("mode");

            Log.d(TAG,"companySep"+companySeq);
            Log.d(TAG,"empSep"+empSeq);
            if (status && !mode.equals("")) {
                LoginActivity.visibleIP(false);
                SharedStore.setEmpSeq(mContext,empSeq);
                SharedStore.setCompanySeq(mContext,companySeq);
                SharedStore.setMyIP(mContext, ip);
                SharedStore.setMyEmployeeNum(mContext,empNum);
                SharedStore.setMyPassWord(mContext, password);
                SharedStore.setAutoLogin(mContext, autoCheck.toString() == "true"?true:false);
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                LoginActivity.failCount = 0;
            } else if (!status) {
                String message = jsonData.getString("message");
                String submsg = messageCheck(message);
                if (submsg.equals("I0301")) {
                    LoginActivity.failCount++;
                    if (LoginActivity.failCount >= 3) {

                        visibleIP(true);
                    }
                    MyAlert.MyDialog_single(mContext, mContext.getString(R.string.di_ti_info), "퇴직", null);
                } else if (submsg.equals("I0300")) {
                    LoginActivity.failCount++;
                    if (LoginActivity.failCount >= 3) {

                        visibleIP(true);
                    }
                    MyAlert.MyDialog_single(mContext, mContext.getString(R.string.di_ti_info), mContext.getString(R.string.di_ct_error_join_id_pw), null);
                } else if (submsg.equals("I0801")) {
                    LoginActivity.failCount++;
                    if (LoginActivity.failCount >= 3) {

                        visibleIP(true);
                    }
                    MyAlert.MyDialog_single(mContext, mContext.getString(R.string.di_ti_info), "정확한 회사 코드를 입력해주세요.", null);
                }

                Log.d(TAG, "-------서버통신 성공");

            }

            switch (result) {
                case "error":
                    Log.d(TAG, "-------에러발생");
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static String messageCheck(String str){
        String mes=str.substring(0,5);
        return mes;
    }
}
