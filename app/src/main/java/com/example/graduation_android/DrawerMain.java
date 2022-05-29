package com.example.graduation_android;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

public class DrawerMain extends AppCompatActivity {
    private final String TAG = "drawer";

    ImageView btnDrawerClose;
    NavigationView drawer;

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.drawer);

        btnDrawerClose = findViewById(R.id.drawer_close);
        drawer = findViewById(R.id.drawer_layout);

        btnDrawerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
