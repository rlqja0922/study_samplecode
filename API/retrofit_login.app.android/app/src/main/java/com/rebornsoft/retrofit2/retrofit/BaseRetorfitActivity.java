package com.rebornsoft.retrofit2.retrofit;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BaseRetorfitActivity extends AppCompatActivity {
    private static final String TAG = "BaseRetrofit";
    public static RetrofitInterface retrofitInterface;
    public static Context mcontext;
    public static String Ip = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static Context getAppContext() {
        return BaseRetorfitActivity.mcontext;
    }

    public static void retrofitURL() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Ip)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    retrofitInterface = retrofit.create(RetrofitInterface.class);
    }
}

