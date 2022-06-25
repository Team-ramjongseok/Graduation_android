package com.example.graduation_android;

import com.google.gson.annotations.SerializedName;

public class CafeMenuData {
    @SerializedName("cafeId")
    private Integer cafeId;

    public CafeMenuData(Integer cafeId) {
        this.cafeId = cafeId;
    }
}
