package com.example.graduation_android.locationdata;

import com.example.graduation_android.LocationMain;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/* 서버에서 받을 위도, 경도들 */
public class LocationResponse {

    @SerializedName("latitude")
    private ArrayList<Double> latitude = new ArrayList<>();

    @SerializedName("longitude")
    private ArrayList<Double> longitude = new ArrayList<>();

    @SerializedName("distanceResult")
    private ArrayList<LocationMain.Cafe> distanceResult = new ArrayList<>();

    public ArrayList getLatitude() { return latitude; }

    public ArrayList getLongitude() { return longitude; }
    public ArrayList getCafe() {return distanceResult;}
}

