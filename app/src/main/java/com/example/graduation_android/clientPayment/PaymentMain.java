package com.example.graduation_android.clientPayment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduation_android.LoginServiceApi;
import com.example.graduation_android.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 유저 id를 사용하여 Payment를 요청하는 코드.
public class PaymentMain extends AppCompatActivity {

    private final String URL = "http://3.38.128.16:8001/"; //사용할 URL
    private final String TAG = "PaymentMain";

    private Retrofit retrofit;
    private clientPaymentAPI service;
    private SharedPreferences preferences; //토큰 저장을 위한 sharedPreferences
    TextView paymentText;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_main);

        paymentText = findViewById(R.id.PaymentText);
        preferences = getSharedPreferences("Tokens", MODE_PRIVATE);
        int userId = preferences.getInt("id", -1);

        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()) //json 분석하기 위해 추가
                .build();

        service = retrofit.create(clientPaymentAPI.class); // retrofit 관련 인터페이스를 class로 지정.
        getPayment(userId);
    }

    private void getPayment(int userId) {
        service.getUserPayment(userId).enqueue(new Callback<List<PaymentResponse>>() {
            @Override
            public void onResponse(Call<List<PaymentResponse>> call, Response<List<PaymentResponse>> response) { // 응답 성공시
                

                List<PaymentResponse> paymentArrayList = response.body();

                for(PaymentResponse item : paymentArrayList) {
                    Log.e(TAG, "안뇽? " + item.getOrder_status());
                }
                Toast.makeText(PaymentMain.this, "안뇽?" + paymentArrayList.get(0).getLocation(), Toast.LENGTH_SHORT).show();
                paymentText.setText(paymentArrayList.get(0).getLocation());

            }

            @Override
            public void onFailure(Call<List<PaymentResponse>> call, Throwable t) { // 응답 실패시
                Toast.makeText(PaymentMain.this, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                Log.e("Payment 가져오는 도중 에러 발생", t.getMessage());
            }
        });
    }

}
