package com.example.graduation_android.logindata;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/* 로그인 요청에 대한 서버의 응답으로 받을 데이터들
    로그인 성공하면 성공했다는 메세지와 로그인한 email을 받음 */

public class LoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("email")
    private String email;

<<<<<<< HEAD
=======
    @SerializedName("nickname")
    private String nickname;

>>>>>>> temp_branch
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

<<<<<<< HEAD
    @SerializedName("expireTime")
    private Date expireTime;
=======
    @SerializedName("expiresIn")
    private int expiresIn;
>>>>>>> temp_branch

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

<<<<<<< HEAD
=======
    public String getNickname() {
        return nickname;
    }

>>>>>>> temp_branch
    public String getAccessToken() { return accessToken; }

    public String getRefreshToken() { return refreshToken; }

<<<<<<< HEAD
    public Date getExpireTime() { return expireTime; }
=======
    public int getExpiresIn() {
        return expiresIn;
    }
>>>>>>> temp_branch
}
