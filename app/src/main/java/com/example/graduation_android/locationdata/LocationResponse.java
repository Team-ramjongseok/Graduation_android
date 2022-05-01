package com.example.graduation_android.locationdata;

import com.google.gson.annotations.SerializedName;

/* 서버에서 받을 위도, 경도들 */
public class LocationResponse {
    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    public Double getLatitude() { return latitude; }

    public Double getLongitude() { return longitude; }
}
