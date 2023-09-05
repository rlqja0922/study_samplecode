package com.rebornsoft.rebornaihypervision;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpCarImage extends AsyncTask<String, Void, String> {
    private Context mContext;
    private static final String TAG = "HttpCarImage";
    private OkHttpClient client = new OkHttpClient.Builder().build();
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public HttpCarImage(Context context){
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        String url = "ai-rebornsoft.asuscomm.com:21701/analyze/license_plate/image";
        String licensePlateImg = strings[0];

        try {
            JSONObject requestData = new JSONObject();
            try {
                    requestData.put("licensePlateImg",licensePlateImg);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, requestData.toString());
            Log.d(TAG,"body : "+body);
            Request request = new Request.Builder()
                    .url("http://"+url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            result = response.body().string();
            Log.d(TAG,"result : "+result);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"err2 msg : "+e.getMessage());
            result = "error";
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals("") || result == null){
            Intent intent = new Intent(mContext, ResultActivity.class);
            mContext.startActivity(intent);
            MyUtils.showToastMsg(mContext, mContext.getString(R.string.di_ct_error));
            return;
        }

        switch (result){
            case "error" :
                Intent intent = new Intent(mContext, ResultActivity.class);
                mContext.startActivity(intent);
                MyUtils.showToastMsg(mContext, mContext.getString(R.string.di_ct_error));
                break;
        }
        try{
            JSONObject jsonData = new JSONObject(result);
            boolean status = jsonData.getBoolean("status");
            Log.d(TAG, "스테이터스?" + status);
            if (status) {
                String licensePlateStr = jsonData.getString("licensePlateStr");
                Log.d(TAG,"status true : "+status);
                Log.d(TAG,"licensePlateStr : "+licensePlateStr);
                SharedStore.setMyCarNumber(mContext, licensePlateStr);
                Intent PutIntent = new Intent(mContext, ResultActivity.class);
                mContext.startActivity(PutIntent);
            }else if(!status) {
                String message = jsonData.getString("message");
                String mess = MyUtils.messageCheck(message);
                Log.d(TAG,"status false : "+mess);
                Intent intent = new Intent(mContext, ResultActivity.class);
                mContext.startActivity(intent);

                if (mess.equals("I0000")){
//                    MyUtils.showToastMsg(mContext,"번호판이 인식되지 않았습니다.\n다시 촬영을 해주세요.");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
