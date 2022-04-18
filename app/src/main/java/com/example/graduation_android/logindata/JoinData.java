package com.example.graduation_android.logindata;

import com.google.gson.annotations.SerializedName;

/* 회원가입 시에 보낼 데이터 */
public class JoinData {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("nickname")
    private String nickname;

    public JoinData(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
