package com.example.graduation_android;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CafeMenuResponse {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private Integer price;

    @SerializedName("cafe_id")
    private Integer cafe_id;

//    @SerializedName("menuResult")
//    private Menu menuResult = new Menu();

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getCafeId() {
        return cafe_id;
    }
}

class Menu {
    @SerializedName("id")
    public Integer id;

    @SerializedName("name")
    public String name;

    @SerializedName("cafe_img")
    public String cafe_img;

    @SerializedName("cafe_info")
    public String cafe_info;

    @SerializedName("operation")
    public String operation;

    @SerializedName("location")
    public String location;

    @SerializedName("seat_empty")
    public Integer seat_empty;

    @SerializedName("seat_all")
    public Integer seat_all;

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("price")
    public Integer price;

    @SerializedName("CafeId")
    public Integer CafeId;
}
