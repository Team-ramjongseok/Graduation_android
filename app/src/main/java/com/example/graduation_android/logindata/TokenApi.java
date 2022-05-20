package com.example.graduation_android.logindata;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PATCH;

public interface TokenApi {
    @FormUrlEncoded
    @PATCH("/auth/token")
    Call<Map<String, String>> refreshToken(@Field("refreshToken") String refreshToken);
}
