package com.example.graduation_android.tokens;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class TokenResponse {
    @SerializedName("access-token")
    private String accessToken;

    @SerializedName("refresh-token")
    private String refreshToken;

    @SerializedName("email")
    private String email;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("expiresIn")
    private int expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public int getExpiresIn() {
        return expiresIn;
    }
}
