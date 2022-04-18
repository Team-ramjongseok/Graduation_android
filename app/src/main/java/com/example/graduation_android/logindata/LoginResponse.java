package com.example.graduation_android.logindata;

import com.google.gson.annotations.SerializedName;

/* 로그인 요청에 대한 서버의 응답으로 받을 데이터들
    로그인 성공하면 성공했다는 메세지와 로그인한 email을 받음 */

public class LoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("email")
    private String email;

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }
}
