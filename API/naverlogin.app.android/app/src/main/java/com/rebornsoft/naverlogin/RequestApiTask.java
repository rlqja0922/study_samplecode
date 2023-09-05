package com.rebornsoft.naverlogin;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestApiTask extends AsyncTask<Void, Void, String> {
    private final Context mContext;
    private final OAuthLogin mOAuthLoginModule;
    public RequestApiTask(Context mContext, OAuthLogin mOAuthLoginModule) {
        this.mContext = mContext;
        this.mOAuthLoginModule = mOAuthLoginModule;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(Void... params) {
        String url = "https://openapi.naver.com/v1/nid/me";
        String at = mOAuthLoginModule.getAccessToken(mContext);
        return mOAuthLoginModule.requestApi(mContext, at, url);
    }

    protected void onPostExecute(String content) {
        try {
            JSONObject loginResult = new JSONObject(content);
            if (loginResult.getString("resultcode").equals("00")){
                JSONObject response = loginResult.getJSONObject("response");
                String profile_image = response.getString("profile_image");
                String name = response.getString("name");
                String email = response.getString("email");

                Intent intent =  new Intent(mContext, MainActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                intent.putExtra("profile_image",profile_image);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mContext.startActivity(intent);

                SharedStore.setMyEmail(mContext, email);
                SharedStore.setMyName(mContext, name);
                SharedStore.setMyImage(mContext, profile_image);
                Log.d("RequestAPi :"," profile_image"+SharedStore.getMyImage(mContext));
                Log.d("RequestAPi :"," name"+SharedStore.getMyName(mContext));
                Log.d("RequestAPi :"," email"+SharedStore.getMyEmail(mContext));
//                SharedStore.setMyID(mContext, id);
//                Toast.makeText(mContext, "id : "+id +" email : "+email, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}