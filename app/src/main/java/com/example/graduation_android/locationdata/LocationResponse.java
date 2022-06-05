package com.example.graduation_android.locationdata;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/* 서버에서 받을 위도, 경도들 */
public class LocationResponse {

    @SerializedName("latitude")
    private ArrayList<Double> latitude = new ArrayList<>();

    @SerializedName("longitude")
    private ArrayList<Double> longitude = new ArrayList<>();

    @SerializedName("distanceResult")
    private ArrayList<Cafe> distanceResult = new ArrayList<>();

    public ArrayList getLatitude() { return latitude; }

    public ArrayList getLongitude() { return longitude; }
    public ArrayList getCafe() {return distanceResult;}
}

class Cafe {
    @SerializedName("cafe_info")
    public String cafe_info;

    @SerializedName("location")
    public String location;

    @SerializedName("seat_empty")
    public int seat_empty;

    @SerializedName("seat_all")
    public int seat_all;

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("distance")
    public Double distance;
}
