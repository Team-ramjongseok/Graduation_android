package com.example.graduation_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private final String TAG = "LocationMain";

    TextView userLat, userLng;
    Button getLocationBtn;
    TextView[] getLats = new TextView[100]; //received latitudes
    TextView[] getLngs = new TextView[100]; //received longitudes

    private Retrofit retrofit;
    private LocationServiceApi service;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_location);

        userLat = findViewById(R.id.user_lat_txt);
        userLng = findViewById(R.id.user_lng_txt);
        getLocationBtn = findViewById(R.id.location_btn_temp);
        for(int i=0; i<5; i++) { //우선 5개만 받아보자
            int lats = getResources().getIdentifier("get_lat"+(i+1), "id", getPackageName());
            int lngs = getResources().getIdentifier("get_lng"+(i+1), "id", getPackageName());

            getLats[i] = (TextView) findViewById(lats);
            getLngs[i] = (TextView) findViewById(lngs);
        }


        /* save user location with sharedPreferences */
        preferences = getSharedPreferences("Location", MODE_PRIVATE);


        /* LocationManager */
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /* permission check (whether granted or not) */
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

        /* get location if permission is granted */
        Location currentLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double currentLat = currentLocation.getLatitude();
        double currentLng = currentLocation.getLongitude();


        /* retrofit2 */
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL.toString())
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
                        StringBuffer coord = new StringBuffer();
                        coord.append(currentLng+","+currentLat);
                        requestGeocode(); // reverse geocoding start

                    }
                }).start();
                sendLocation(new LocationData(currentLat, currentLng));

                Toast.makeText(getApplicationContext(), "위치 받아오기 성공", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // reverse Geocoding
    public void requestGeocode() {
        StringBuffer coord = new StringBuffer();
        coord.append("127.068151,37.547369");

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
            Log.e(TAG, "received string: "+stringBuffer);

            regionJsonParser(stringBuffer);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // json 파싱
    public void regionJsonParser(StringBuffer stringBuffer) {
        try {
            StringBuffer userRegion =  new StringBuffer();
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject)jsonParser.parse(stringBuffer.toString());
            JsonArray jsonArray = (JsonArray) jsonObject.get("results");
            JsonObject regionObject = (JsonObject) jsonArray.get(0);
            JsonObject areaObject = regionObject.getAsJsonObject("region");
            Log.e(TAG, "received areaObject: "+ areaObject);


            String[] splitedUserRegion = new String[5]; //유저 위치 정보를 나눠서 저장할 공간
            String match = "[^\\uAC00-\\uD7A3xfe0-9a-zA-Z]"; //한글, 영어 외에 다 제거

            for (int i = 1; i<= 4; i++){
                JsonObject nameObject = areaObject.getAsJsonObject("area"+i);

                splitedUserRegion[i] = nameObject.get("name").toString().replaceAll(match, ""); //쓸데없는 것들 제거

                userRegion.append(nameObject.get("name").toString());
            }
            Log.e(TAG, "received userRegion: "+ userRegion);
            
            /* 받은 데이터들 중에서 '구'와 '동'만 뽑아서 써야지 */
            String currentUserRegion = splitedUserRegion[2] + " " + splitedUserRegion[3];

            /* save user location with sharedPreferences */
            editor = preferences.edit();
            editor.putString("userLocation", currentUserRegion);
            editor.commit();

        }catch (Exception e) {
            e.printStackTrace();
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
                    JsonObject jsonCafeObject = (JsonObject) jsonResult.get(i);
                    Log.e(TAG, "received json: "+ jsonCafeObject);

                    editor = preferences.edit();
                    editor.putString("cafe"+i, jsonCafeObject.get("name").getAsString());
                    editor.putString("seat"+i, jsonCafeObject.get("seat_empty").getAsString());
                    editor.commit();
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
