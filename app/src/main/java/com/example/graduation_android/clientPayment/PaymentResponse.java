package com.example.graduation_android.clientPayment;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {

    public PaymentResponse(int id,String order_time, int amount, String name, String location, String order_status) {
        this.id = id;
        this.order_time = order_time;
        this.amount = amount;
        this.name = name;
        this.location = location;
        this.order_status = order_status;
    }

    private int id;

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

    public int getId() {
        return id;
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
