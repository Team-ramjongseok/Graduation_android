package com.example.graduation_android;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import com.example.graduation_android.locationdata.LocationData;
import com.example.graduation_android.locationdata.LocationResponse;
import com.example.graduation_android.tokens.TokenData;

import java.util.List;

public interface LocationServiceApi {
    @POST("/main/gps")
    Call<Object> userLocation(@Body LocationData data); //위도 경도 주고받기 -> 여러개니까 list로
}
