package com.example.graduation_android.clientPayment;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("order_time")
    private String order_time;

    @SerializedName("amount")
    private int amount;

    @SerializedName("name")
    private String name;

    @SerializedName("location")
    private String location;

    @SerializedName("order_status")
    private String order_status;

    public String getOrder_time() {
        return order_time;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getOrder_status() {
        return order_status;
    }
}
