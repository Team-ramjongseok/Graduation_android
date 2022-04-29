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

    @SerializedName("phone")
    private String phone;

    public JoinData(String email, String nickname, String password, String phone) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
    }
}
