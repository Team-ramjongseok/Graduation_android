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
    LinearLayout btnLocation, paymentManage;                     //현재 위치
    TextView userProfile, currentLocation;
    TextView showMore;                                           //지도로 넘어가기
    SharedPreferences preferences, prefLocation;
    Button clearLocation;                                        //(임시) 현재 위치 제거 버튼
    ImageView btnDrawer, btnCloseDrawer;                         //메인화면 사이드 바
    DrawerLayout drawer;                                         //메인화면 사이드 바
    FloatingActionButton fabMain, fabStart, fabLoading, fabDone; //메인화면 플로팅 버튼
    Animation floatingOpen, floatingClose;                       //메인화면 플로팅 버튼 애니메이션
    RelativeLayout mainLayout;                                   //메인화면
    TextView cafeListEmptyText;                                  //카페 리스트가 비어있을 시 화면에 나타남
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

        /* 메인화면 플로팅 버튼 */
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
        Location currentLocationInfo = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double currentLat = currentLocationInfo.getLatitude();
        double currentLng = currentLocationInfo.getLongitude();
        String nickName = preferences.getString("nickname", "");

        /* retrofit2 */
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL.toString())
                .addConverterFactory(GsonConverterFactory.create()) //json 분석하기 위해 추가
                .build();
        service = retrofit.create(LocationServiceApi.class);

        //로그인 버튼 클릭시
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginMain.class);
                startActivity(intent);
            }
        });


        //로그아웃 버튼 클릭시
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //저장되어 있던 토큰, 유저 정보들을 삭제
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(getApplicationContext(), "logout success", Toast.LENGTH_SHORT).show();

                //화면을 새로고침
                refreshMain();
            }
        });


        //floating button 클릭 시
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabStart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                fabLoading.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown)));
                fabDone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                fabAnim();
            }
        });

        //다른 곳 눌러도 floating button 닫히게 설정
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFabOpen==1) {
                    fabAnim();
                }
            }
        });


        //위치 정보 제거 버튼
        clearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = prefLocation.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(getApplicationContext(), "location cleared", Toast.LENGTH_SHORT).show();

                //화면을 새로고침
                refreshMain();
            }
        });

        //현재 위치를 조회하는 버튼
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

                Toast.makeText(getApplicationContext(), "위치 받아오기 성공", Toast.LENGTH_SHORT).show();
            }
        });


        //왼쪽 상단의 메뉴버튼을 클릭하면 나오는 drawerLayout
        btnDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });


        //drawer에서 닫기 버튼 클릭하면 닫힘
        btnCloseDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
            }
        });


        //더보기 클릭시 맵 화면으로 이동
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapMain.class);
                startActivity(intent);
            }
        });

      
        //토큰이 유효할 경우 유저 정보를 표시
        if(nickName!="") { //유저 정보가 존재할 경우
            btnLogin.setVisibility(View.GONE); //로그인 버튼 없애고

            userProfile.setText(nickName + " 님");
            drawerUserName.setText(nickName);
            userProfile.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
        }
        else { //유저 정보가 존재하지 않을 경우
            userProfile.setVisibility(View.GONE);
            drawerUserName.setText("로그인 해주세요");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }


        //위치 정보가 저장 되어있을 경우 위치 표시
        String curLocation = prefLocation.getString("userLocation", "");
        if(curLocation!="") { //위치 정보가 존재할 경우
//            clearLocation.setVisibility(View.VISIBLE);

            currentLocation.setText(curLocation);
            btnLocation.setClickable(false);
        }
        else {
            clearLocation.setVisibility(View.GONE);
            btnLocation.setClickable(true);
        }


        // 이용 내역 보여주기
        paymentManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "이용 내역",Toast.LENGTH_SHORT).show();
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
                addItem(tempCafeId, tempCafeName, "빈자리 "+tempEmptySeatFiltered);
            }
        }
        else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            cafeListEmptyText.setVisibility(View.VISIBLE);
        }

        mRecyclerViewAdapter = new RecyclerViewAdapter(this, mList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)); //가로 방향
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

    //메인화면 새로고침
    public void refreshMain() {
        finish();
        overridePendingTransition(0, 0); //새로고침 시에 화면 전환 효과 삭제
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    //뒤로가기 버튼을 통해 drawer를 닫을 수 있음
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

    // json 파싱
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