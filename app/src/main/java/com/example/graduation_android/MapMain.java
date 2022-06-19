//package com.example.graduation_android;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.FragmentActivity;
//
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;

//public class MapMain extends FragmentActivity implements OnMapReadyCallback {
//    GoogleMap gMap;
//    private SharedPreferences preferences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.map_main);
//
//        /* user location with sharedPreferences */
//        preferences = getSharedPreferences("Location", MODE_PRIVATE);
//
//        //프래그먼트에 핸들 가져오기 및 콜백 등록
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
//        mapFragment.getMapAsync(this);
//    }
//
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        gMap = googleMap; //구글 맵 객체 호출
//
//        for(int i=0; i<5; i++) {
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(new LatLng(Integer.parseInt(preferences.getString("cafe"+i, "")), ))
//        }
//    }
//}
