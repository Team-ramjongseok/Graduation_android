package com.example.graduation_android.logindata;

import com.google.gson.annotations.SerializedName;

/* 회원가입 요청에 대한 응답으로 서버에서 받을 데이터 */
public class JoinResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("nickname")
    private String nickname;

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }
}
