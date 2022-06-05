package com.example.graduation_android.locationdata;

import com.google.gson.annotations.SerializedName;

/* 서버에 보낼 현재 나의 위도 경도 */
public class LocationData {
    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("nickName")
    private String nickName;


    public LocationData(Double latitude, Double longitude, String nickName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.nickName = nickName;

    }
}
