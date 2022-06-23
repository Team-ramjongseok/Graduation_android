package com.example.graduation_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMain extends AppCompatActivity implements OnMapReadyCallback {
    private final String TAG = "MapMain";

    private SharedPreferences preferences;
    ImageView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        goBack = findViewById(R.id.map_go_back);

        /* user location with sharedPreferences */
        preferences = getSharedPreferences("Location", MODE_PRIVATE);

        //프래그먼트에 핸들 가져오기 및 콜백 등록
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MarkerOptions markerOptions = new MarkerOptions();

        LatLng GunIp = new LatLng(37.5401, 127.0704); //test용 건입 위치

        markerOptions.position(GunIp);
        markerOptions.title("건입");
        markerOptions.snippet("test용 건입 마커");
        googleMap.addMarker(markerOptions);


        for(int i=0; i<5; i++) {
            if(i==0 || i==3 || i==4) {
                LatLng cafeLocation = new LatLng(Double.parseDouble(preferences.getString("lat"+i, "")), Double.parseDouble(preferences.getString("lng"+i, "")));
                double emptySeat = Double.parseDouble(preferences.getString("seat"+i, ""));
                markerOptions.position(cafeLocation);
                markerOptions.title("cafe "+(i+1));
                markerOptions.snippet("empty "+emptySeat);
                googleMap.addMarker(markerOptions);
            }
        }


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(GunIp));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}
