package com.example.graduation_android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

public class DrawerMain extends AppCompatActivity {
    private final String TAG = "drawer";

    ImageView btnDrawerClose;
    NavigationView drawer;
    LinearLayout paymentManage;

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.drawer);

        btnDrawerClose = findViewById(R.id.drawer_close);
        drawer = findViewById(R.id.drawer_layout);
        paymentManage = findViewById(R.id.payment_manage);


        paymentManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "SE",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: HO");
            }
        });
        btnDrawerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        
        

    }
}
