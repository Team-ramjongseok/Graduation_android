package com.example.graduation_android;

import android.graphics.Paint;

import com.google.gson.annotations.SerializedName;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginServiceApi {
    @GET("/")
    Call<ResponseBody> getFunc(@Query("data") String data); //get은 쿼리 형태로 보낸다

    @FormUrlEncoded
    @POST("/user/join")
    Call<JoinResponse> login(@Body JoinData data); //신규 회원가입 시에 서버에 보낼 이메일과 비밀번호 정보

    @FormUrlEncoded
    @POST("/user/login")
    Call<LoginResponse> login(@Body LoginData data); //로그인 시에 서버에 보낼 이메일과 비밀번호 정보

    @FormUrlEncoded
    @PUT("/{id}")
    Call<ResponseBody> putFunc(@Path("id") String id,
                               @Field("data") String data);

    @DELETE("/{id}")
    Call<ResponseBody> deleteFunc(@Path("id") String id);
}