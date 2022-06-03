package com.example.graduation_android.clientPayment;

import com.google.gson.annotations.SerializedName;

public class PaymentRequestData {

    public PaymentRequestData(String imp_uid, String merchant_uid) {
        this.imp_uid = imp_uid;
        this.merchant_uid = merchant_uid;
    }

    @SerializedName("imp_uid")
    private String imp_uid;

    @SerializedName("merchant_uid")
    private String merchant_uid;


}
