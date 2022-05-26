package com.example.graduation_android;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.iamport.sdk.data.sdk.IamPortRequest;
import com.iamport.sdk.data.sdk.PG;
import com.iamport.sdk.data.sdk.PayMethod;
import com.iamport.sdk.domain.core.Iamport;

import java.util.Date;

import kotlin.Unit;

public class Payment extends AppCompatActivity {
    private final String TAG = "Payment";
    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.payment_frag);

        //start
        Iamport.INSTANCE.init(this);

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
                .build();

        Iamport.INSTANCE.payment("iamport", null, null, request,
                iamPortApprove -> {
                    //(Optional) chai 결제 시 최종 결제 전 콜백 함수
                    return Unit.INSTANCE;
                }, iamPortResponse -> {
                    //최종 결제 결과 콜백 함수
                    String responseText = iamPortResponse.toString();
                    Log.d("Iamport_sample", responseText);
                    Toast.makeText(this, responseText, Toast.LENGTH_SHORT).show();
                    return Unit.INSTANCE;
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Iamport.INSTANCE.close();
    }
}


