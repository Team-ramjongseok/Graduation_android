package com.example.graduation_android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.graduation_android.locationdata.LocationData;
import com.example.graduation_android.locationdata.LocationResponse;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationMain extends AppCompatActivity {
    private final String URL = "http://10.0.2.2:8001"; //사용할 URL (localhost)
    private final String TAG = "LocationMain";

    TextView userLat, userLng;
    Button getLocationBtn;
    TextView[] getLats = new TextView[100]; //received latitudes
    TextView[] getLngs = new TextView[100]; //received langitudes

    private Retrofit retrofit;
    private LocationServiceApi service;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_location);

        userLat = findViewById(R.id.user_lat_txt);
        userLng = findViewById(R.id.user_lng_txt);
        getLocationBtn = findViewById(R.id.location_btn);
        for(int i=0; i<5; i++) { //우선 5개만 받아보자
            int lats = getResources().getIdentifier("get_lat"+(i+1), "id", getPackageName());
            int lngs = getResources().getIdentifier("get_lng"+(i+1), "id", getPackageName());

            getLats[i] = (TextView) findViewById(lats);
            getLngs[i] = (TextView) findViewById(lngs);
        }

        /* LocationManager */
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //permission check (whether granted or not)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "권한 필요", Toast.LENGTH_SHORT).show();

            //request permissions
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

        //get location if permission is granted
        Location currentLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double currentLat = currentLocation.getLatitude();
        double currentLng = currentLocation.getLongitude();


        /* retrofit2 */
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()) //json 분석하기 위해 추가
                .build();
        service = retrofit.create(LocationServiceApi.class);


        //시작 버튼 클릭 시
        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLat.setText(String.valueOf(currentLat));
                userLng.setText(String.valueOf(currentLng));

                sendLocation(new LocationData(currentLat, currentLng));
                //sendLocation(new LocationData(currentLat, currentLng));
            }
        });
    }

    /* 내 위치 서버에 전송 및 서버에서 위치 받기 */
    private void sendLocation(LocationData data) {
        service.userLocation(data).enqueue(new Callback<List<LocationResponse>>() {
            @Override
            public void onResponse(Call<List<LocationResponse>> call, Response<List<LocationResponse>> response) {
                List<LocationResponse> result = response.body(); //받아온 데이터들

                for(int i=0; i<result.size(); i++) {
                    Log.e(TAG, String.valueOf(result.get(i).getLatitude()));
                    Log.e(TAG, String.valueOf(result.get(i).getLongitude()));

                    getLats[i].setText(String.valueOf(result.get(i).getLatitude()));
                    getLngs[i].setText(String.valueOf(result.get(i).getLongitude()));
                    /*
                    double getLat = result.get(i).getLatitude();
                    double getLng = result.get(i).getLongitude();
                    Log.e(TAG, String.valueOf(getLat));
                    Log.e(TAG, String.valueOf(getLng));

                    getLats[i].setText(String.valueOf(getLat));
                    getLngs[i].setText(String.valueOf(getLng));

                     */
                }
            }

            @Override
            public void onFailure(Call<List<LocationResponse>> call, Throwable t) {
                Toast.makeText(LocationMain.this, "error while getting location", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "위치 가져오기 에러");
            }
        });

    }

}