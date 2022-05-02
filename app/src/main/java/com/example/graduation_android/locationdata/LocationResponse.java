package com.example.graduation_android.locationdata;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/* 서버에서 받을 위도, 경도들 */
public class LocationResponse {
    @SerializedName("latitude")
    private ArrayList<String> latitude = new ArrayList<>();

    @SerializedName("longitude")
    private ArrayList<String> longitude = new ArrayList<>();

    public ArrayList getLatitude() { return latitude; }

    public ArrayList getLongitude() { return longitude; }
}
