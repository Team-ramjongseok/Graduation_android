package com.example.graduation_android.logindata;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class TokenResponse {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("expireTime")
    private Date expireTime;

    public String getAccessToken() { return accessToken; }

    public String getRefreshToken() { return refreshToken; }

    public Date getExpireTime() { return expireTime; }
}
