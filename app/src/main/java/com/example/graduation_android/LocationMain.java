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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import org.json.*;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import okhttp3.Address;
import okhttp3.Interceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class LocationMain extends AppCompatActivity {
    private final String URL = "http://10.0.2.2:8001/"; //사용할 URL (localhost)
    private final String TAG = "LocationMain";

    TextView userLat, userLng;
    Button getLocationBtn;
    TextView[] getLats = new TextView[100]; //received latitudes
    TextView[] getLngs = new TextView[100]; //received longitudes

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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        requestGeocode(); // reverse geocoding start
                    }
                }).start();
                sendLocation(new LocationData(currentLat, currentLng));
            }
        });
    }

    // reverse Geocoding
    public void requestGeocode() {
        String coord = "127.1234308,37.3850143";
        String query = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords="
                    + coord + "&sourcecrs=epsg:4326&output=json&orders=addr&output=json";
        String API_ID = BuildConfig.API_ID;
        String API_KEY = BuildConfig.API_KEY;


        try {
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", API_ID);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY",API_KEY );
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader bufferedReader;

            if (responseCode == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
               }

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
            }

            jsonParser(stringBuffer);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // json 파싱
    public void jsonParser(StringBuffer stringBuffer) {
        try {
            StringBuffer userRegion =  new StringBuffer();
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject)jsonParser.parse(stringBuffer.toString());
            JsonArray jsonArray = (JsonArray) jsonObject.get("results");
            JsonObject regionObject = (JsonObject) jsonArray.get(0);
            JsonObject areaObject = regionObject.getAsJsonObject("region");
            Log.e(TAG, "received: "+ areaObject);

            for (int i = 1; i<= 4; i++){
                JsonObject nameObject = areaObject.getAsJsonObject("area"+i);
                userRegion.append(nameObject.get("name").toString());
            }
            Log.e(TAG, "received: "+ userRegion);

        }catch (Exception e) {

        }
    }

    private void sendLocation(LocationData data) {
        service.userLocation(data).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Object result = response.body();
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonArray = (JsonArray) jsonParser.parse(new Gson().toJson(result));
                JsonObject jsonObject = (JsonObject) jsonArray.get(0);
                JsonArray jsonResult = jsonObject.getAsJsonArray("distanceResult");

                for (int i = 0; i< jsonResult.size(); i++){
                    Log.e(TAG, "received: "+ jsonResult.get(i));

                }
                // 각 객체의 값들 알고싶을때
                /*
                jsonResult.get(i).getAsJsonObject().get("cafe_info").getAsString()
                Log.e(TAG, "received: "+ jsonObject.get("distanceResult"));
                ArrayList<Cafe> test =  new ArrayList<>();
                test.add(jsonObject.get("distanceResult"))
                Log.e(TAG, "received: "+test);

                for(int i=0; i<jsonArray.size(); i++) {
                    JsonObject jsonObject = (JsonObject) jsonArray.get(i);
                    String get_info = jsonObject.get("cafe_info").getAsString();
                    String get_location = jsonObject.get("location").getAsString();
                    Double get_lat = jsonObject.get("latitude").getAsDouble();
                    Double get_lng = jsonObject.get("longitude").getAsDouble();

                    Log.e(TAG, "cafe_info: "+get_info);
                    Log.e(TAG, "location: "+get_location);
                    Log.e(TAG, "latitude : "+get_lat);
                    Log.e(TAG, "longitude : "+get_lng);
                }
                */



            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "connection error");
            }
        });
    }

}
