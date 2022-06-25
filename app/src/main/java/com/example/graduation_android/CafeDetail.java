package com.example.graduation_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduation_android.clientPayment.Payment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CafeDetail extends AppCompatActivity {
    private final String TAG = "CafeDetail";

    TextView cafeName, cafeSeat, cafePhone, cafeLocation;
    Button gotoPayment;
    ListView listView;
    MenuListAdapter menuListAdapter;

    private Retrofit retrofit;
    private CafeMenuApi service;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafe_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        cafeName = findViewById(R.id.cafe_details_cafename);
        cafeSeat = findViewById(R.id.cafe_details_seat);
        cafePhone = findViewById(R.id.cafe_details_phone);
        cafeLocation = findViewById(R.id.cafe_details_location);
        gotoPayment = findViewById(R.id.button_goto_payment);
        listView = findViewById(R.id.menu_list_view);

        menuListAdapter = new MenuListAdapter();


        /* retrofit2 */
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL.toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(CafeMenuApi.class);



        gotoPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Payment.class);
                startActivity(intent);
            }
        });

        Intent receivedIntent = getIntent();
        cafeName.setText(receivedIntent.getStringExtra("cafeName"));
        cafeSeat.setText(receivedIntent.getStringExtra("emptySeat"));

        int cafeId=1;
        getMenus(cafeId);

    }

    /* 서버로부터 메뉴 가져오기 */
    private void getMenus(int data) {
        service.getMenus1(data).enqueue(new Callback<List<CafeMenuResponse>>() {
            @Override
            public void onResponse(Call<List<CafeMenuResponse>> call, Response<List<CafeMenuResponse>> response) {
                if(response.isSuccessful()) {
                    List<CafeMenuResponse> menuList = response.body();

                    Log.d(TAG, "test receive: "+menuList);

                    Toast.makeText(getApplicationContext(), "메뉴 가져오기 성공", Toast.LENGTH_SHORT).show();

                    if(menuList.size()==0) {
                        Log.d(TAG, "result is empty");
                    }
                    for(int i=0; i<menuList.size(); i++) {
                        CafeMenuResponse item = menuList.get(i);

                        String receivedMenuName = item.getName();
                        int receivedPrice = item.getPrice();

                        menuListAdapter.addItem(new MenuItem(receivedMenuName, receivedPrice));
                        menuListAdapter.addItem(new MenuItem(receivedMenuName, receivedPrice));
                        listView.setAdapter(menuListAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CafeMenuResponse>> call, Throwable t) {
                Toast.makeText(CafeDetail.this, "메뉴 긁어오기 에러", Toast.LENGTH_SHORT).show();
                Log.e("메뉴 긁어오기 에러 발생", t.getMessage());
            }
        });

    }

    class MenuListAdapter extends BaseAdapter {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();

        public void addItem(MenuItem item) {
            items.add(item);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            MenuItemView menuItemView = null;

            if(convertView==null) {
                menuItemView = new MenuItemView(getApplicationContext());
            }
            else {
                menuItemView = (MenuItemView) convertView;
            }

            MenuItem item = items.get(position);
            menuItemView.setName(item.getName());
            menuItemView.setPrice(item.getPrice());

            return menuItemView;
        }
    }

}
