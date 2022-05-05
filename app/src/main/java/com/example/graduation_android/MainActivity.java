package com.example.graduation_android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.AttachedSurfaceControl;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnLogin, btnPayment;
    LinearLayout btnLocation;

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginMain.class);
                startActivity(intent);
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationMain.class);
                startActivity(intent);
            }
        });

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