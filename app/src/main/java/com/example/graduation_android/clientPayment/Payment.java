package com.example.graduation_android.clientPayment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduation_android.LoginServiceApi;
import com.example.graduation_android.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iamport.sdk.data.sdk.IamPortRequest;
import com.iamport.sdk.data.sdk.IamPortResponse;
import com.iamport.sdk.data.sdk.PG;
import com.iamport.sdk.data.sdk.PayMethod;
import com.iamport.sdk.domain.core.Iamport;

import org.json.JSONArray;

import java.util.Date;

import kotlin.Unit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Payment extends AppCompatActivity {
    private final String TAG = "Payment";
    private final String URL = "http://3.38.128.16:8001/"; //사용할 URL

    private Retrofit retrofit;
    private clientPaymentAPI service;

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.payment_frag);

        /* retrofit */

        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()) //json 분석하기 위해 추가
                .build();
        service = retrofit.create(clientPaymentAPI.class);


        //start
        Iamport.INSTANCE.init(this);


        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", "anna");
        jsonObject.addProperty("cafeId", 1);
        jsonObject.addProperty("order_list", "[1,2]");
        jsonObject.addProperty("memo", "아이스 아메리카노 얼음은 넣어주지 마세요~");
        String jsonStr = gson.toJson(jsonObject);


        //서버에 보낼 결제 정보들
        IamPortRequest request = IamPortRequest.builder()
                .pg(PG.kcp.makePgRawName(""))          //PG사
                .pay_method(PayMethod.card.name())          //결제수단
                .name("아메리카노, 에스프레소")                 //주문명
                .merchant_uid(""+(new Date()).getTime())    //주문번호
                .amount("11000")                            //결제금액
                .buyer_email("gildong@gmail.com")
                .buyer_name("hong")
                .buyer_tel("010-4242-4242")
                .buyer_addr("서울특별시 강남구 신사동")
                .buyer_postcode("01181")
                .custom_data(jsonStr)
                .build();
        /*
            - toDo 무조건 넣어야함!! -
                customdata에 대한 설명
                userId : 현재 유저의 아이디
                cafeId : 구매하려고 하는 카페의 id값.
                order_list : 구매하려고 하는 메뉴의 id값. -> 여러개가 배열로 들어가야함.
                memo : 메모 사항.
                여기서, 여러개를 구매할 수 있기 때문에, 배열로 받아들여야한다.
        */

        Iamport.INSTANCE.payment("imp51918627", null, null, request,
                iamPortApprove -> {
                    //(Optional) chai 결제 시 최종 결제 전 콜백 함수
                    return Unit.INSTANCE;
                }, iamPortResponse -> {
                    //최종 결제 결과 콜백 함수

                    String imp_uid = iamPortResponse.getImp_uid();
                    String merchant_uid = iamPortResponse.getMerchant_uid();

                    Log.d("uid", imp_uid + " , " + merchant_uid);
                    PostPaymentComp(new PaymentRequestData(imp_uid, merchant_uid));

                    String responseText = iamPortResponse.toString();
                    Log.d("Iamport_sample", responseText);
                    Toast.makeText(this, responseText, Toast.LENGTH_SHORT).show();
                    return Unit.INSTANCE;
                }
        );
    }

    private void PostPaymentComp(PaymentRequestData data) {
        service.postPaymentComplete(data).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(Payment.this, "결제 성공", Toast.LENGTH_SHORT).show();
                String result = response.body();
                Log.e("결제 result : ", result);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Payment.this, "결제 에러", Toast.LENGTH_SHORT).show();
                Log.e("결제 에러 발생", t.getMessage());
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Iamport.INSTANCE.close();
    }
}


