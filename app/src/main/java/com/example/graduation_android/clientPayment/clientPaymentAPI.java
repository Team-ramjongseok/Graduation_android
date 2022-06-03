package com.example.graduation_android.clientPayment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface clientPaymentAPI {
    @GET("/payment/userPayment")
    Call<List<PaymentResponse>> getUserPayment(@Query("userId") int userId);
}
