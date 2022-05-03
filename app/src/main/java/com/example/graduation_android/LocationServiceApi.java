package com.example.graduation_android;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import com.example.graduation_android.locationdata.LocationData;
import com.example.graduation_android.locationdata.LocationResponse;

import java.util.List;

public interface LocationServiceApi {
    @POST("/main/gps")
    Call<List<LocationResponse>> userLocation(@Body LocationData data); //위도 경도 주고받기 -> 여러개니까 list로
}
