package com.example.graduation_android.clientPayment;

import com.google.gson.annotations.SerializedName;

public class PaymentRequestData {
    @SerializedName("imp_uid")
    private String imp_uid;

    @SerializedName("merchant_uid")
    private String merchant_uid;
}
