package com.example.graduation_android;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduation_android.clientPayment.Payment;
import com.example.graduation_android.clientPayment.PaymentMain;
import com.example.graduation_android.locationdata.LocationData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    Button btnLogin, btnPayment, btnLogout;
    LinearLayout btnLocation, paymentManage;                     //νμ¬ μμΉ
    TextView userProfile, currentLocation;
    TextView showMore;                                           //μ§λλ‘ λμ΄κ°κΈ°
    SharedPreferences preferences, prefLocation;
    Button clearLocation;                                        //(μμ) νμ¬ μμΉ μ κ±° λ²νΌ
    ImageView btnDrawer, btnCloseDrawer;                         //λ©μΈνλ©΄ μ¬μ΄λ λ°
    DrawerLayout drawer;                                         //λ©μΈνλ©΄ μ¬μ΄λ λ°
    FloatingActionButton fabMain, fabStart, fabLoading, fabDone; //λ©μΈνλ©΄ νλ‘ν λ²νΌ
    Animation floatingOpen, floatingClose;                       //λ©μΈνλ©΄ νλ‘ν λ²νΌ μ λλ©μ΄μ
    RelativeLayout mainLayout;                                   //λ©μΈνλ©΄
    TextView cafeListEmptyText;                                  //μΉ΄ν λ¦¬μ€νΈκ° λΉμ΄μμ μ νλ©΄μ λνλ¨
    TextView drawerUserName;



    private RecyclerView mRecyclerView;                          //cafe list viewer on main page
    private ArrayList<RecyclerViewItem> mList;                   //cafe list
    private RecyclerViewAdapter mRecyclerViewAdapter;            //cafe list viewer adapter

    private int isFabOpen = 0;                                   //check whether floating button is expanded

    private Retrofit retrofit;                                   //retrofit
    private LocationServiceApi service;                          //service api


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        btnLogin = findViewById(R.id.goto_login);
        btnLocation = findViewById(R.id.goto_location);
        userProfile = findViewById(R.id.main_user_nickname);
        showMore = findViewById(R.id.main_show_more);
        btnLogout = findViewById(R.id.button_logout);
        currentLocation = findViewById(R.id.main_user_location);
        clearLocation = findViewById(R.id.button_clear_location);
        btnDrawer = findViewById(R.id.main_drawer_button);
        btnCloseDrawer = findViewById(R.id.drawer_close);
        drawer = findViewById(R.id.drawer_main);
        fabMain = findViewById(R.id.order_status_main);
        fabStart = findViewById(R.id.order_status_start);
        fabLoading = findViewById(R.id.order_status_loading);
        fabDone = findViewById(R.id.order_status_done);
        mainLayout = findViewById(R.id.main_page);
        paymentManage = findViewById(R.id.payment_manage);
        cafeListEmptyText = findViewById(R.id.cafe_list_empty_text);
        drawerUserName = findViewById(R.id.drawer_user_name);

        /* λ©μΈνλ©΄ νλ‘ν λ²νΌ */
        floatingOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_button_open);
        floatingClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_button_close);


        /* sharedPreferences */
        preferences = getSharedPreferences("Tokens", MODE_PRIVATE);
        prefLocation = getSharedPreferences("Location", MODE_PRIVATE);

        /* locationManager */
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /* permission check (whether granted or not) */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "κΆν νμ", Toast.LENGTH_SHORT).show();

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
        Location currentLocationInfo = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double currentLat = currentLocationInfo.getLatitude();
        double currentLng = currentLocationInfo.getLongitude();
        String nickName = preferences.getString("nickname", "");

        /* retrofit2 */
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL.toString())
                .addConverterFactory(GsonConverterFactory.create()) //json λΆμνκΈ° μν΄ μΆκ°
                .build();
        service = retrofit.create(LocationServiceApi.class);

        //λ‘κ·ΈμΈ λ²νΌ ν΄λ¦­μ
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginMain.class);
                startActivity(intent);
            }
        });


        //λ‘κ·Έμμ λ²νΌ ν΄λ¦­μ
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //μ μ₯λμ΄ μλ ν ν°, μ μ  μ λ³΄λ€μ μ­μ 
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(getApplicationContext(), "logout success", Toast.LENGTH_SHORT).show();

                //νλ©΄μ μλ‘κ³ μΉ¨
                refreshMain();
            }
        });


        //floating button ν΄λ¦­ μ
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabStart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                fabLoading.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown)));
                fabDone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                fabAnim();
            }
        });

        //λ€λ₯Έ κ³³ λλ¬λ floating button λ«νκ² μ€μ 
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFabOpen==1) {
                    fabAnim();
                }
            }
        });


        //μμΉ μ λ³΄ μ κ±° λ²νΌ
        clearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = prefLocation.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(getApplicationContext(), "location cleared", Toast.LENGTH_SHORT).show();

                //νλ©΄μ μλ‘κ³ μΉ¨
                refreshMain();
            }
        });

        //νμ¬ μμΉλ₯Ό μ‘°ννλ λ²νΌ
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuffer coord = new StringBuffer();
                        coord.append(currentLng+","+currentLat);
                        requestGeocode(); // reverse geocoding start
                    }
                }).start();

                mRecyclerViewAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                refreshMain();

                sendLocation(new LocationData(currentLat, currentLng,nickName));

                Toast.makeText(getApplicationContext(), "μμΉ λ°μμ€κΈ° μ±κ³΅", Toast.LENGTH_SHORT).show();
            }
        });


        //μΌμͺ½ μλ¨μ λ©λ΄λ²νΌμ ν΄λ¦­νλ©΄ λμ€λ drawerLayout
        btnDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });


        //drawerμμ λ«κΈ° λ²νΌ ν΄λ¦­νλ©΄ λ«ν
        btnCloseDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
            }
        });


        //λλ³΄κΈ° ν΄λ¦­μ λ§΅ νλ©΄μΌλ‘ μ΄λ
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapMain.class);
                startActivity(intent);
            }
        });

      
        //ν ν°μ΄ μ ν¨ν  κ²½μ° μ μ  μ λ³΄λ₯Ό νμ
        if(nickName!="") { //μ μ  μ λ³΄κ° μ‘΄μ¬ν  κ²½μ°
            btnLogin.setVisibility(View.GONE); //λ‘κ·ΈμΈ λ²νΌ μμ κ³ 

            userProfile.setText(nickName + " λ");
            drawerUserName.setText(nickName);
            userProfile.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
        }
        else { //μ μ  μ λ³΄κ° μ‘΄μ¬νμ§ μμ κ²½μ°
            userProfile.setVisibility(View.GONE);
            drawerUserName.setText("λ‘κ·ΈμΈ ν΄μ£ΌμΈμ");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }


        //μμΉ μ λ³΄κ° μ μ₯ λμ΄μμ κ²½μ° μμΉ νμ
        String curLocation = prefLocation.getString("userLocation", "");
        if(curLocation!="") { //μμΉ μ λ³΄κ° μ‘΄μ¬ν  κ²½μ°
//            clearLocation.setVisibility(View.VISIBLE);

            currentLocation.setText(curLocation);
            btnLocation.setClickable(false);
        }
        else {
            clearLocation.setVisibility(View.GONE);
            btnLocation.setClickable(true);
        }


        // μ΄μ© λ΄μ­ λ³΄μ¬μ£ΌκΈ°
        paymentManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "μ΄μ© λ΄μ­",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), PaymentMain.class);
                startActivity(intent);
            }
        });

        /* RecyclerView */
        firstInit();
        if(curLocation!="") {
            mRecyclerView.setVisibility(View.VISIBLE);
            cafeListEmptyText.setVisibility(View.GONE);
            for(int i=0; i<5; i++) {
                int tempCafeId = prefLocation.getInt("id"+i, -1);
                String tempCafeName = prefLocation.getString("cafe"+i, "");
                String tempEmptySeat = prefLocation.getString("seat"+i, "");
                String tempEmptySeatFiltered = tempEmptySeat.replaceAll("[^1-9]", "");
                addItem(tempCafeId, tempCafeName, "λΉμλ¦¬ "+tempEmptySeatFiltered);
            }
        }
        else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            cafeListEmptyText.setVisibility(View.VISIBLE);
        }

        mRecyclerViewAdapter = new RecyclerViewAdapter(this, mList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)); //κ°λ‘ λ°©ν₯
    }



    public void firstInit() {
        mRecyclerView = (RecyclerView) findViewById(R.id.cafe_list);
        mList = new ArrayList<>();
    }

    public void addItem(int cafeId, String cafeName, String emptySeat) {
        RecyclerViewItem item = new RecyclerViewItem();

        item.setCafeId(cafeId);
        item.setCafeName(cafeName);
        item.setEmptyseat(emptySeat);

        mList.add(item);
    }

    //λ©μΈνλ©΄ μλ‘κ³ μΉ¨
    public void refreshMain() {
        finish();
        overridePendingTransition(0, 0); //μλ‘κ³ μΉ¨ μμ νλ©΄ μ ν ν¨κ³Ό μ­μ 
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    //λ€λ‘κ°κΈ° λ²νΌμ ν΅ν΄ drawerλ₯Ό λ«μ μ μμ
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    //floating button animation
    public void fabAnim() {
        if(isFabOpen==0) {
            fabStart.startAnimation(floatingOpen);
            fabLoading.startAnimation(floatingOpen);
            fabDone.startAnimation(floatingOpen);
            drawer.setBackgroundColor(getResources().getColor(R.color.gray));
            isFabOpen = 1;

        }
        else {
            fabStart.startAnimation(floatingClose);
            fabLoading.startAnimation(floatingClose);
            fabDone.startAnimation(floatingClose);
            drawer.setBackgroundColor(getResources().getColor(R.color.white_transparent));
            isFabOpen = 0;
        }
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

    // json νμ±
    public void regionJsonParser(StringBuffer stringBuffer) {
        try {
            SharedPreferences.Editor editor = prefLocation.edit();
            StringBuffer userRegion =  new StringBuffer();
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject)jsonParser.parse(stringBuffer.toString());
            JsonArray jsonArray = (JsonArray) jsonObject.get("results");
            JsonObject regionObject = (JsonObject) jsonArray.get(0);
            JsonObject areaObject = regionObject.getAsJsonObject("region");
            Log.e(TAG, "received areaObject: "+ areaObject);


            String[] splitedUserRegion = new String[5]; //μ μ  μμΉ μ λ³΄λ₯Ό λλ μ μ μ₯ν  κ³΅κ°
            String match = "[^\\uAC00-\\uD7A3xfe0-9a-zA-Z]"; //νκΈ, μμ΄ μΈμ λ€ μ κ±°

            for (int i = 1; i<= 4; i++){
                JsonObject nameObject = areaObject.getAsJsonObject("area"+i);

                splitedUserRegion[i] = nameObject.get("name").toString().replaceAll(match, ""); //μΈλ°μλ κ²λ€ μ κ±°

                userRegion.append(nameObject.get("name").toString());
            }
            Log.e(TAG, "received userRegion: "+ userRegion);

            /* λ°μ λ°μ΄ν°λ€ μ€μμ 'κ΅¬'μ 'λ'λ§ λ½μμ μ¨μΌμ§ */
            String currentUserRegion = splitedUserRegion[2] + " " + splitedUserRegion[3];

            /* save user location with sharedPreferences */
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
                SharedPreferences.Editor editor = prefLocation.edit();
                Object result = response.body();
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonArray = (JsonArray) jsonParser.parse(new Gson().toJson(result));
                JsonObject jsonObject = (JsonObject) jsonArray.get(0);
                JsonArray jsonResult = jsonObject.getAsJsonArray("distanceResult");

                for (int i=0; i<5; i++){
                    JsonObject jsonCafeObject = (JsonObject) jsonResult.get(i);
                    Log.e(TAG, "received json: "+ jsonCafeObject);

                    editor.putInt("id"+i, jsonCafeObject.get("id").getAsInt());
                    editor.putString("cafe"+i, jsonCafeObject.get("name").getAsString());
                    editor.putString("seat"+i, jsonCafeObject.get("seat_empty").getAsString());
                    editor.putString("lat"+i, jsonCafeObject.get("latitude").getAsString());
                    editor.putString("lng"+i, jsonCafeObject.get("longitude").getAsString());
                    editor.commit();
                }

                // κ° κ°μ²΄μ κ°λ€ μκ³ μΆμλ
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