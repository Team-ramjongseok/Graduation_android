package com.example.graduation_android.clientPayment;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface clientPaymentAPI {
    @GET("/payment/userPayment")
    Call<PaymentResponse> getUserPayment(@Query("userId") int userId);
}
