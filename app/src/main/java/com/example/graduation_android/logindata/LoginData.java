package com.example.graduation_android.logindata;

import com.google.gson.annotations.SerializedName;

/* 로그인 시에 서버에 보낼 데이터들 */
public class LoginData {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public LoginData(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
