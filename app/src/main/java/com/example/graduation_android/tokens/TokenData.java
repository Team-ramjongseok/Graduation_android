package com.example.graduation_android.tokens;

import com.google.gson.annotations.SerializedName;

public class TokenData {
    @SerializedName("access-token")
    private String accessToken;

    @SerializedName("refresh-token")
    private String refreshToken;

    public TokenData(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
