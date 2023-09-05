package com.rebornsoft.retrofit2.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("m/login")
    Call<LoginData> getLogin(@Body LoginData loginData);

}