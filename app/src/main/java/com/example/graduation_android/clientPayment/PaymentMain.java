package com.example.graduation_android.clientPayment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
    ListView listView;
    MyAdapter myAdapter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_main);

        /* 초기화 영역 */
        listView = findViewById(R.id.paymentList);
        myAdapter = new MyAdapter();


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

                for(int i = paymentArrayList.size()-1; i >= 0; i--){
//                for(PaymentResponse item : paymentArrayList) {
                    PaymentResponse item = paymentArrayList.get(i);
                    String temp = item.getOrder_time();
                    String order_time = temp.replace("T", " / ").replace("Z", " ");
                    Log.e(TAG, order_time);
                    myAdapter.addItem(new PaymentResponse(i+1, order_time,item.getAmount(),item.getName(),item.getLocation(),item.getOrder_status()));
                    listView.setAdapter(myAdapter);
                }
//                listView.setAdapter(myAdapter);
                Toast.makeText(PaymentMain.this, "안뇽?" + paymentArrayList.get(0).getLocation(), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Call<List<PaymentResponse>> call, Throwable t) { // 응답 실패시
                Toast.makeText(PaymentMain.this, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                Log.e("Payment 가져오는 도중 에러 발생", t.getMessage());
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        private ArrayList<PaymentResponse> items = new ArrayList<>();

        public void addItem(PaymentResponse item){
            items.add(item);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public PaymentResponse getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup viewGroup) {
            PaymentItemView view = new PaymentItemView(getApplicationContext());

            PaymentResponse item = items.get(position);

            view.setId(item.getId());
            view.setLocation(item.getLocation());
            view.setName(item.getName());
            view.setOrder_status(item.getOrder_status());
            view.setOrder_time(item.getOrder_time());
            view.setAmount(item.getAmount());

            return view;
        }
    }

}
