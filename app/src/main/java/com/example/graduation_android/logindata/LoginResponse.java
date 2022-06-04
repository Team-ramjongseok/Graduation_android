package com.example.graduation_android.logindata;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/* 로그인 요청에 대한 서버의 응답으로 받을 데이터들
    로그인 성공하면 성공했다는 메세지와 로그인한 email을 받음 */

public class LoginResponse {


    @SerializedName("id")
    private int id;

    @SerializedName("message")
    private String message;

    @SerializedName("email")
    private String email;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("expiresIn")
    private int expiresIn;

    @SerializedName("expireTime")
    private Date expireTime;
  

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public int getid() { return id; }

    public String getNickname() {
        return nickname;
    }

    public String getAccessToken() { return accessToken; }

    public String getRefreshToken() { return refreshToken; }

    public int getExpiresIn() {
        return expiresIn;
    }
}
