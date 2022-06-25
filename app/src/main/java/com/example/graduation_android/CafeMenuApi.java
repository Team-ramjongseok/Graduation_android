package com.example.graduation_android;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CafeMenuApi {
    @GET("/cafe")
    Call<List<CafeMenuResponse>> getMenus1(@Query("cafeId") Integer data);

    @POST("/cafe")
    Call<Object> getMenus(@Body CafeMenuData data);
}
