package com.example.graduation_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.AttachedSurfaceControl;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    Button btnLogin, btnPayment, btnLogout;
    LinearLayout btnLocation;                                    //현재 위치
    TextView userProfile, currentLocation;
    TextView showMore;                                           //지도로 넘어가기
    SharedPreferences preferences, prefLocation;
    Button clearLocation;                                        //(임시) 현재 위치 제거 버튼
    Button btnRefresh, tempPayment;                              //새로고침 버튼, (임시) 결제창 버튼
    ImageView btnDrawer, btnCloseDrawer;                         //메인화면 사이드 바
    DrawerLayout drawer;                                         //메인화면 사이드 바
    FloatingActionButton fabMain, fabStart, fabLoading, fabDone; //메인화면 플로팅 버튼
    Animation floatingOpen, floatingClose;                       //메인화면 플로팅 버튼 애니메이션
    RelativeLayout mainLayout;                                   //메인화면

<<<<<<< HEAD

    private RecyclerView mRecyclerView;                          //cafe list viewer on main page
    private ArrayList<RecyclerViewItem> mList;                   //cafe list
    private RecyclerViewAdapter mRecyclerViewAdapter;            //cafe list viewer adapter

    private int isFabOpen = 0;                                   //check whether floating button is expanded
=======
    private RecyclerView mRecyclerView;
    private ArrayList<RecyclerViewItem> mList;
    private RecyclerViewAdapter mRecyclerViewAdapter;
>>>>>>> 6b5c9ce430fb6b79d69cad80345c55f3c4f78d4b

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        btnLogin = findViewById(R.id.goto_login);
        btnLocation = findViewById(R.id.goto_location);
        //btnPayment = findViewById(R.id.goto_payment);
        userProfile = findViewById(R.id.main_user_nickname);
        showMore = findViewById(R.id.main_show_more);
        btnLogout = findViewById(R.id.button_logout);
        currentLocation = findViewById(R.id.main_user_location);
        clearLocation = findViewById(R.id.button_clear_location);
        btnRefresh = findViewById(R.id.button_refresh);
        tempPayment = findViewById(R.id.temp_payment);
        btnDrawer = findViewById(R.id.main_drawer_button);
        btnCloseDrawer = findViewById(R.id.drawer_close);
        drawer = findViewById(R.id.drawer_main);
        fabMain = findViewById(R.id.order_status_main);
        fabStart = findViewById(R.id.order_status_start);
        fabLoading = findViewById(R.id.order_status_loading);
        fabDone = findViewById(R.id.order_status_done);
        mainLayout = findViewById(R.id.main_page);
        
        /* 메인화면 플로팅 버튼 */
        floatingOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_button_open);
        floatingClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_button_close);


        /* sharedPreferences */
        preferences = getSharedPreferences("Tokens", MODE_PRIVATE);
        prefLocation = getSharedPreferences("Location", MODE_PRIVATE);

        
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
                finish();
                overridePendingTransition(0, 0); //새로고침 시에 화면 전환 효과 삭제
                Intent intent = getIntent();
                startActivity(intent);
                overridePendingTransition(0, 0);
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
                finish();
                overridePendingTransition(0, 0); //새로고침 시에 화면 전환 효과 삭제
                Intent intent = getIntent();
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //리프레시 버튼
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //화면을 새로고침
                finish();
                overridePendingTransition(0, 0); //새로고침 시에 화면 전환 효과 삭제
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        //현재 위치를 조회하는 버튼
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationMain.class);
                startActivity(intent);
            }
        });


        //결제 화면으로 가는 임시 버튼
        tempPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Payment.class);
                startActivity(intent);
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

      
        //토큰이 유효할 경우 유저 정보를 표시
        String nickName = preferences.getString("nickname", "");
        if(nickName!="") { //유저 정보가 존재할 경우
            btnLogin.setVisibility(View.GONE); //로그인 버튼 없애고

            userProfile.setText(nickName + " 님");
            userProfile.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
        }
        else { //유저 정보가 존재하지 않을 경우
            userProfile.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }


        //위치 정보가 저장 되어있을 경우 위치 표시
        String curLocation = prefLocation.getString("userLocation", "");
        if(curLocation!="") { //위치 정보가 존재할 경우
            clearLocation.setVisibility(View.VISIBLE);

            currentLocation.setText(curLocation);
            btnLocation.setClickable(false);
        }
        else {
            clearLocation.setVisibility(View.GONE);
            btnLocation.setClickable(true);
        }


        /* RecyclerView */
        firstInit();
        if(curLocation!="") {
            for(int i=0; i<5; i++) {
                String tempCafeName = prefLocation.getString("cafe"+i, "");
                String tempEmptySeat = prefLocation.getString("seat"+i, "");
                String tempEmptySeatFiltered = tempEmptySeat.replaceAll("[^1-9]", "");
                addItem(tempCafeName, "빈자리 "+tempEmptySeatFiltered);
            }
        }
        else {
            for(int i=0; i<5; i++) {
                addItem("cafe "+(i+1), "빈자리 "+i);
            }
        }

        mRecyclerViewAdapter = new RecyclerViewAdapter(mList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)); //가로 방향
    }

    public void firstInit() {
        mRecyclerView = (RecyclerView) findViewById(R.id.cafe_list);
        mList = new ArrayList<>();
    }

    public void addItem(String cafeName, String emptySeat) {
        RecyclerViewItem item = new RecyclerViewItem();

        item.setCafeName(cafeName);
        item.setEmptyseat(emptySeat);

        mList.add(item);
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

}