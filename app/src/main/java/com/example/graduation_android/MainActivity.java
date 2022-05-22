package com.example.graduation_android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.AttachedSurfaceControl;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    Button btnLogin, btnPayment, btnLogout;
    LinearLayout btnLocation;
    TextView userProfile;
    SharedPreferences preferences;

    private RecyclerView mRecyclerView;
    private ArrayList<RecyclerViewItem> mList;
    private RecyclerViewAdapter mRecyclerViewAdapter;

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
        btnLogout = findViewById(R.id.button_logout);

        /* sharedPreferences */
        preferences = getSharedPreferences("Tokens", MODE_PRIVATE);

        
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


        //현재 위치를 조회하는 버튼
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationMain.class);
                startActivity(intent);
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


        /* RecyclerView */
        firstInit();
        for(int i=0; i<5; i++) {
            addItem("cafe "+(i+1), "빈자리 5");
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

}