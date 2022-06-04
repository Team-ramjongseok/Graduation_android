package com.example.graduation_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduation_android.clientPayment.Payment;

import org.jetbrains.annotations.Nullable;

public class CafeDetail extends AppCompatActivity {

    TextView cafeName, cafeSeat, cafePhone, cafeLocation;
    Button gotoPayment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafe_details);


        cafeName = findViewById(R.id.cafe_details_cafename);
        cafeSeat = findViewById(R.id.cafe_details_seat);
        cafePhone = findViewById(R.id.cafe_details_phone);
        cafeLocation = findViewById(R.id.cafe_details_location);
        gotoPayment = findViewById(R.id.button_goto_payment);


        gotoPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Payment.class);
                startActivity(intent);
            }
        });





    }
}
